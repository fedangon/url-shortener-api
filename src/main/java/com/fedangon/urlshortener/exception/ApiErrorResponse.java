package com.fedangon.urlshortener.exception;

import java.time.LocalDateTime;

/**
 * DTO de erro padronizado para respostas HTTP com falhas de validação ou negócio.
 */
public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
