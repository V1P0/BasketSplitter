package com.ocado.basket.exceptions;

/**
 * ConfigLoadException class
 * Thrown when an error occurs while loading the configuration
 *
 * @version 1.0
 */
public class ConfigLoadException extends RuntimeException {
    public ConfigLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
