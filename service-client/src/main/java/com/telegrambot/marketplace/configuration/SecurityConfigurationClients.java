package com.telegrambot.marketplace.configuration;

import com.telegrambot.marketplace.security.AuthenticationFilter;
import com.telegrambot.marketplace.security.CustomAuthenticationProvider;
import com.telegrambot.marketplace.security.Http401UnauthorizedEntryPoint;
import com.telegrambot.marketplace.service.auth.TokenHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static com.telegrambot.marketplace.security.SecurityUrls.PROTECTED_BASIC_URLS;

@Order(2)
@Configuration
@RequiredArgsConstructor
public class SecurityConfigurationClients {

    private final Http401UnauthorizedEntryPoint authenticationEntryPoint;

    private final TokenHelper tokenHelper;

    private final CustomAuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChainClientsAuth(final HttpSecurity http) throws Exception {
        http
                .securityContext(securityContext -> securityContext.requireExplicitSave(false))
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(PROTECTED_BASIC_URLS).authenticated())
                .addFilterAfter(authenticationFilter(), BasicAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }
    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter(tokenHelper, authenticationProvider, PROTECTED_BASIC_URLS);
    }
}
