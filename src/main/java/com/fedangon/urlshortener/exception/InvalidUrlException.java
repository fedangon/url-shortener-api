package com.fedangon.urlshortener.exception;

/**
 * Excecao lancada quando a URL informada e invalida para o dominio.
 */
public class InvalidUrlException extends RuntimeException {

    public InvalidUrlException(String message) {
        super(message);
    }
}
