package com.fedangon.urlshortener.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fedangon.urlshortener.entity.ShortUrl;
import com.fedangon.urlshortener.repository.ShortUrlRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceImplTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @InjectMocks
    private ShortUrlServiceImpl shortUrlService;

    @Test
    void shouldCreateShortUrl() {
        // Cenário: criação de URL encurtada.
        // - O repositório não encontra colisões para o código gerado.
        // - O save devolve a própria entidade para facilitar asserções sem JPA real.
        when(shortUrlRepository.findByShortCode(anyString())).thenReturn(Optional.empty());
        when(shortUrlRepository.save(any(ShortUrl.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShortUrl shortUrl = shortUrlService.createShortUrl(" https://example.com ", null);

        assertNotNull(shortUrl.getShortCode());
        // O gerador deve produzir códigos entre 6 e 8 caracteres (Base62).
        assertTrue(shortUrl.getShortCode().length() >= 6 && shortUrl.getShortCode().length() <= 8);
        // A URL original deve ser sanitizada (trim) pela regra de negócio.
        assertEquals("https://example.com", shortUrl.getOriginalUrl());

        verify(shortUrlRepository).save(any(ShortUrl.class));
    }

    @Test
    void shouldResolveOriginalUrlAndIncrementClickCount() {
        // Cenário: "redirect" no nível de service consiste em:
        // - Resolver a URL original a partir do shortCode.
        // - Incrementar o contador de cliques.
        ShortUrl stored = ShortUrl.builder()
                .originalUrl("https://example.com")
                .shortCode("abc123")
                .clickCount(5L)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        when(shortUrlRepository.findByShortCode("abc123")).thenReturn(Optional.of(stored));
        when(shortUrlRepository.save(any(ShortUrl.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String originalUrl = shortUrlService.retrieveOriginalUrl("abc123");
        shortUrlService.incrementClickCounter("abc123");

        assertEquals("https://example.com", originalUrl);

        ArgumentCaptor<ShortUrl> captor = ArgumentCaptor.forClass(ShortUrl.class);
        verify(shortUrlRepository).save(captor.capture());
        assertEquals(6L, captor.getValue().getClickCount());
        // O repositório é consultado na resolução e novamente no incremento.
        verify(shortUrlRepository, times(2)).findByShortCode("abc123");
    }
}
