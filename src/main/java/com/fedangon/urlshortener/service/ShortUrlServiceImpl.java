package com.fedangon.urlshortener.service;

import com.fedangon.urlshortener.entity.ShortUrl;
import com.fedangon.urlshortener.exception.ShortUrlExpiredException;
import com.fedangon.urlshortener.exception.ShortUrlNotFoundException;
import com.fedangon.urlshortener.repository.ShortUrlRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação da regra de negócio de encurtamento de URL.
 * Orquestra validações, geração de código curto e atualização de métricas de acesso.
 */
@Service
@RequiredArgsConstructor
public class ShortUrlServiceImpl implements ShortUrlService {

    private static final String SHORT_CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_LENGTH = 8;
    private static final int SHORT_CODE_MAX_ATTEMPTS = 20;

    private final ShortUrlRepository shortUrlRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public ShortUrl createShortUrl(String originalUrl, LocalDateTime expiresAt) {
        String sanitizedOriginalUrl = validateOriginalUrl(originalUrl);

        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(sanitizedOriginalUrl)
                .shortCode(generateUniqueShortCode())
                .expiresAt(expiresAt)
                .build();

        return shortUrlRepository.save(shortUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public String retrieveOriginalUrl(String shortCode) {
        return findValidShortUrl(shortCode).getOriginalUrl();
    }

    @Override
    @Transactional
    public void incrementClickCounter(String shortCode) {
        ShortUrl shortUrl = findValidShortUrl(shortCode);
        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
        shortUrlRepository.save(shortUrl);
    }

    /**
     * Garante que a URL original recebida é válida para persistência.
     */
    private String validateOriginalUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isBlank()) {
            throw new IllegalArgumentException("Original URL must not be blank");
        }
        return originalUrl.trim();
    }

    /**
     * Busca a URL encurtada e valida se ainda está disponível para uso.
     */
    private ShortUrl findValidShortUrl(String shortCode) {
        String sanitizedShortCode = validateShortCode(shortCode);

        ShortUrl shortUrl = shortUrlRepository.findByShortCode(sanitizedShortCode)
                .orElseThrow(() -> new ShortUrlNotFoundException(sanitizedShortCode));

        if (shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ShortUrlExpiredException(sanitizedShortCode);
        }

        return shortUrl;
    }

    /**
     * Gera um código curto aleatório e confirma unicidade antes de retornar.
     */
    private String generateUniqueShortCode() {
        for (int attempt = 0; attempt < SHORT_CODE_MAX_ATTEMPTS; attempt++) {
            String shortCode = generateRandomShortCode();
            if (shortUrlRepository.findByShortCode(shortCode).isEmpty()) {
                return shortCode;
            }
        }
        throw new IllegalStateException("Unable to generate a unique short code");
    }

    /**
     * Gera um código alfanumérico no tamanho definido para URL encurtada.
     */
    private String generateRandomShortCode() {
        StringBuilder codeBuilder = new StringBuilder(SHORT_CODE_LENGTH);
        for (int index = 0; index < SHORT_CODE_LENGTH; index++) {
            int randomIndex = secureRandom.nextInt(SHORT_CODE_CHARACTERS.length());
            codeBuilder.append(SHORT_CODE_CHARACTERS.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    /**
     * Garante que o código curto recebido é válido para consulta.
     */
    private String validateShortCode(String shortCode) {
        if (shortCode == null || shortCode.isBlank()) {
            throw new IllegalArgumentException("Short code must not be blank");
        }
        return shortCode.trim();
    }
}
