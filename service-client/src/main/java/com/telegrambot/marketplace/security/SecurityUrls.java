package com.telegrambot.marketplace.security;

import lombok.experimental.UtilityClass;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@UtilityClass
public class SecurityUrls {

    public static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/auth"),
            new AntPathRequestMatcher("/api/auth/refresh-token"),
            new AntPathRequestMatcher("/api/swagger-ui/**"),
            new AntPathRequestMatcher("/api/swagger-ui**"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/swagger-ui**"),
            new AntPathRequestMatcher("/v3/api-docs**"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/system/settings"),
            new AntPathRequestMatcher("/auth"),
            new AntPathRequestMatcher("/static/**")
    );

    public static final RequestMatcher PROTECTED_BASIC_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/v1/**")
    );
}
