package com.fedangon.urlshortener.exception;

/**
 * Exceção lançada quando uma URL encurtada já ultrapassou sua data de expiração.
 */
public class ShortUrlExpiredException extends RuntimeException {

    public ShortUrlExpiredException(String shortCode) {
        super("Short URL expired for code: " + shortCode);
    }
}
