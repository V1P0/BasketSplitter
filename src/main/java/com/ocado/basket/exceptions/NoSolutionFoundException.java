package com.ocado.basket.exceptions;

/**
 * NoSolutionFoundException class
 * Thrown when no solution is found for the given basket
 *
 * @version 1.0
 */
public class NoSolutionFoundException extends RuntimeException{
    public NoSolutionFoundException(String message) {
        super(message);
    }
}
