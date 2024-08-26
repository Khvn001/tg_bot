package com.telegrambot.marketplace.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {
    public CustomAuthenticationException(final String msg, final Throwable t) {
        super(msg, t);
    }

    public CustomAuthenticationException(final String msg) {
        super(msg);
    }
}
