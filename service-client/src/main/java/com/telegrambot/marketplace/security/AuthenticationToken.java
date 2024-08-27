package com.telegrambot.marketplace.security;

import com.telegrambot.marketplace.entity.user.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;
import java.util.Objects;

public class AuthenticationToken extends AbstractAuthenticationToken {

    private String token;

    private User user;

    public AuthenticationToken(final String token) {
        super(Collections.emptyList());
        this.token = token;
        this.setAuthenticated(true);
    }

    public AuthenticationToken(final String token, final User user) {
        super(Collections.emptyList());
        this.token = token;
        this.user = user;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.token = null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AuthenticationToken that = (AuthenticationToken) o;
        return Objects.equals(token, that.token) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token, user);
    }
}
