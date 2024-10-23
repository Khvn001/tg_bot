package com.telegrambot.marketplace.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordVerifier {

    private final PasswordEncoder passwordEncoder;

    public boolean verifyPassword(final String rawPassword, final String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
