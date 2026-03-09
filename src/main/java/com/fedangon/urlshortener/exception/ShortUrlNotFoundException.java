package com.fedangon.urlshortener.exception;

/**
 * Exceção lançada quando um código curto não possui URL associada no sistema.
 */
public class ShortUrlNotFoundException extends RuntimeException {

    public ShortUrlNotFoundException(String shortCode) {
        super("Short URL not found for code: " + shortCode);
    }
}
