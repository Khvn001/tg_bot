package com.telegrambot.marketplace.exception;

public class DatabaseEntryAlreadyExistsException extends RuntimeException {

    public DatabaseEntryAlreadyExistsException() {
        super();
    }

    public DatabaseEntryAlreadyExistsException(final String message) {
        super(message);
    }

    public DatabaseEntryAlreadyExistsException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public DatabaseEntryAlreadyExistsException(final Throwable throwable) {
        super(throwable);
    }
}
