package com.fedangon.urlshortener.util;

import java.security.SecureRandom;

/**
 * Utilitário responsável por gerar códigos curtos com caracteres Base62.
 * O algoritmo escolhe um tamanho aleatório entre 6 e 8 e sorteia cada posição
 * a partir do alfabeto Base62 (a-z, A-Z, 0-9) usando SecureRandom.
 */
public final class ShortCodeGenerator {

    private static final char[] BASE62_ALPHABET =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 8;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private ShortCodeGenerator() {
        // Classe utilitária: previne instanciação.
    }

    /**
     * Gera um código curto aleatório no alfabeto Base62.
     *
     * @return código com tamanho entre 6 e 8 caracteres
     */
    public static String generate() {
        int length = MIN_LENGTH + SECURE_RANDOM.nextInt(MAX_LENGTH - MIN_LENGTH + 1);
        char[] buffer = new char[length];
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(BASE62_ALPHABET.length);
            buffer[i] = BASE62_ALPHABET[index];
        }
        return new String(buffer);
    }
}
