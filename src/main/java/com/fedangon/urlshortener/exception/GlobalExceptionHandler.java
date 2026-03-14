package com.fedangon.urlshortener.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handler global para transformar exceções em respostas HTTP consistentes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata falhas de validação de campos do payload.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation failed");

        return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    /**
     * Trata payload ausente ou com JSON inválido.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<ApiErrorResponse> handleNotReadableException(HttpMessageNotReadableException ex,
                                                                       HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Request body is required", request.getRequestURI());
    }

    /**
     * Trata recursos nao encontrados com resposta 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
                                                                   HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    /**
     * Trata URL invalida com resposta 400.
     */
    @ExceptionHandler(InvalidUrlException.class)
    @ResponseBody
    public ResponseEntity<ApiErrorResponse> handleInvalidUrl(InvalidUrlException ex,
                                                             HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    /**
     * Trata erros de negocio de URL encurtada inexistente.
     */
    @ExceptionHandler(ShortUrlNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ApiErrorResponse> handleShortUrlNotFound(ShortUrlNotFoundException ex,
                                                                   HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    /**
     * Trata erros de negocio de URL encurtada expirada.
     */
    @ExceptionHandler(ShortUrlExpiredException.class)
    @ResponseBody
    public ResponseEntity<ApiErrorResponse> handleShortUrlExpired(ShortUrlExpiredException ex,
                                                                  HttpServletRequest request) {
        return buildResponse(HttpStatus.GONE, ex.getMessage(), request.getRequestURI());
    }

    /**
     * Trata falhas genericas com resposta 500.
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex,
                                                                   HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, String path) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
        return ResponseEntity.status(status).body(response);
    }
}
