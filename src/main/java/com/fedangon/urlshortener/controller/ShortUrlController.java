package com.fedangon.urlshortener.controller;

import com.fedangon.urlshortener.dto.ShortenRequest;
import com.fedangon.urlshortener.dto.ShortenResponse;
import com.fedangon.urlshortener.service.ShortUrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador responsável por expor os endpoints públicos de encurtamento de URL.
 */
@RestController
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    /**
     * Endpoint para criar uma URL encurtada a partir de uma URL original.
     */
    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request,
                                                   HttpServletRequest httpServletRequest) {
        String shortCode = shortUrlService.createShortUrl(request.url(), null).getShortCode();
        String shortUrl = buildShortUrl(httpServletRequest, shortCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ShortenResponse(shortUrl));
    }

    /**
     * Endpoint que redireciona o cliente para a URL original a partir do código curto.
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = shortUrlService.retrieveOriginalUrl(shortCode);
        shortUrlService.incrementClickCounter(shortCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    private String buildShortUrl(HttpServletRequest request, String shortCode) {
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();

        StringBuilder builder = new StringBuilder();
        builder.append(scheme).append("://").append(host);
        if (!("http".equalsIgnoreCase(scheme) && port == 80)
                && !("https".equalsIgnoreCase(scheme) && port == 443)) {
            builder.append(":").append(port);
        }
        builder.append("/").append(shortCode);
        return builder.toString();
    }
}
