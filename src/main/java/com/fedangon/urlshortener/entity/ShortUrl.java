package com.fedangon.urlshortener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "short_urls")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ShortUrl {

    /**
     * Identificador único do registro encurtado.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    /**
     * URL original informada pelo cliente e que será redirecionada.
     */
    @NotBlank
    @Size(max = 2048)
    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    /**
     * Código curto único utilizado para compor a URL encurtada.
     */
    @NotBlank
    @Size(min = 4, max = 32)
    @Column(name = "short_code", nullable = false, unique = true, length = 32)
    private String shortCode;

    /**
     * Data e hora em que o encurtamento foi criado no sistema.
     */
    @NotNull
    @PastOrPresent
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data e hora limite para expiração da URL encurtada.
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * Quantidade total de acessos recebidos pela URL encurtada.
     */
    @NotNull
    @PositiveOrZero
    @Column(name = "click_count", nullable = false)
    private Long clickCount;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (clickCount == null) {
            clickCount = 0L;
        }
    }
}
