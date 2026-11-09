package com.example.md03.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}