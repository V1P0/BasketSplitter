package com.ocado.basket.exceptions;

public class ConfigLoadException extends RuntimeException {
    public ConfigLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
