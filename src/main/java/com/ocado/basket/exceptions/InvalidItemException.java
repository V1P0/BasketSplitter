package com.ocado.basket.exceptions;

/**
 * InvalidItemException class
 * Thrown when an invalid item is found in the basket
 *
 * @version 1.0
 */
public class InvalidItemException extends RuntimeException {
    public InvalidItemException(String message) {
        super(message);
    }
}
