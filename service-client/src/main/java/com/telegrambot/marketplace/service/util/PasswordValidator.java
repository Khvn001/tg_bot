package com.telegrambot.marketplace.service.util;

import java.util.regex.Pattern;

public final class PasswordValidator {

    private PasswordValidator() { }

    // Regular expression for validating the password
    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$";

    private static final Pattern PATTERN = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isValid(final String password) {
        return password != null && PATTERN.matcher(password).matches();
    }
}
