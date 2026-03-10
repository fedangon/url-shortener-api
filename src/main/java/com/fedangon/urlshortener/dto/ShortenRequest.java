package com.fedangon.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

/**
 * DTO de entrada para solicitação de encurtamento de URL.
 */
public record ShortenRequest(
        @NotBlank
        @Size(max = 2048)
        @URL
        String url
) {
}
