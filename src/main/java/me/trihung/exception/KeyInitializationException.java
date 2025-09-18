package me.trihung.exception;
public class KeyInitializationException extends RuntimeException {
    public KeyInitializationException(String message) { super(message); }
    public KeyInitializationException(String message, Throwable cause) { super(message, cause); }
}