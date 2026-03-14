package com.fedangon.urlshortener.exception;

/**
 * Excecao generica para recursos nao encontrados.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
