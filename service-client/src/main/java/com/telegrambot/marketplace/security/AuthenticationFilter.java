package com.telegrambot.marketplace.security;

import com.telegrambot.marketplace.service.auth.TokenHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER = "Bearer";

    private final TokenHelper tokenHelper;
    private final CustomAuthenticationProvider authenticationProvider;
    private final RequestMatcher requiresAuthenticationRequestMatcher;
    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain chain
    ) throws ServletException, IOException {
        if (requiresAuthenticationRequestMatcher.matches(request)) {
            final String authHeader = request.getHeader(AUTHORIZATION);
            final AuthenticationToken token = Optional.ofNullable(authHeader)
                    .map(header -> StringUtils.removeStart(header, BEARER))
                    .map(String::trim)
                    .map(AuthenticationToken::new)
                    .orElse(null);

            if (token != null && tokenHelper.validateToken((String) token.getCredentials())) {
                final Authentication authentication = authenticationProvider.authenticate(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return request.getServletPath().matches(
                "(/swagger-ui|/v3/api-docs|/system/settings|/static|" +
                        "/auth|/auth/refresh-token).*");
    }
}
