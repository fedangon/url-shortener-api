package com.fedangon.urlshortener.repository;

import com.fedangon.urlshortener.entity.ShortUrl;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório responsável por abstrair o acesso aos dados da entidade ShortUrl.
 * Centraliza operações de persistência e consultas específicas de URL encurtada.
 */
public interface ShortUrlRepository extends JpaRepository<ShortUrl, UUID> {

    /**
     * Busca uma URL encurtada pelo código curto único.
     *
     * @param shortCode código curto associado à URL encurtada
     * @return URL encurtada encontrada ou vazio quando não existir
     */
    Optional<ShortUrl> findByShortCode(String shortCode);
}
