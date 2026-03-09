package com.fedangon.urlshortener.service;

import com.fedangon.urlshortener.entity.ShortUrl;
import java.time.LocalDateTime;

/**
 * Contrato da camada de serviço para os casos de uso de URL encurtada.
 * Define operações de negócio sem acoplamento com detalhes de infraestrutura.
 */
public interface ShortUrlService {

    /**
     * Cria e persiste uma nova URL encurtada.
     *
     * @param originalUrl URL original informada pelo cliente
     * @param expiresAt data de expiração opcional da URL encurtada
     * @return entidade criada com código curto único
     */
    ShortUrl createShortUrl(String originalUrl, LocalDateTime expiresAt);

    /**
     * Recupera a URL original a partir de um código curto válido.
     *
     * @param shortCode código curto da URL encurtada
     * @return URL original correspondente ao código
     */
    String retrieveOriginalUrl(String shortCode);

    /**
     * Incrementa o contador de cliques de uma URL encurtada válida.
     *
     * @param shortCode código curto da URL encurtada
     */
    void incrementClickCounter(String shortCode);
}
