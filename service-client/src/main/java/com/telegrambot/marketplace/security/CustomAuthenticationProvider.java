package com.telegrambot.marketplace.security;

import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.service.entity.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String token = (String) authentication.getCredentials();
        final User user = userService.getByToken(token);

        return user == null ? null : new AuthenticationToken(token, user);
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return AuthenticationToken.class.isAssignableFrom(authentication);
    }

}
