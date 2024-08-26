package com.telegrambot.marketplace.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super();
    }

    public NotFoundException(final String message) {
        super(message);
    }

    public NotFoundException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public NotFoundException(final Throwable throwable) {
        super(throwable);
    }
}
