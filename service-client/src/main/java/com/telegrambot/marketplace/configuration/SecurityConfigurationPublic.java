package com.telegrambot.marketplace.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import static com.telegrambot.marketplace.security.SecurityUrls.PUBLIC_URLS;

@Order(1)
@Configuration
@RequiredArgsConstructor
public class SecurityConfigurationPublic {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(PUBLIC_URLS);
    }

}
