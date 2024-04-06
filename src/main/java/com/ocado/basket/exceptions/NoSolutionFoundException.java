package com.ocado.basket.exceptions;

public class NoSolutionFoundException extends RuntimeException{
    public NoSolutionFoundException(String message) {
        super(message);
    }
}
