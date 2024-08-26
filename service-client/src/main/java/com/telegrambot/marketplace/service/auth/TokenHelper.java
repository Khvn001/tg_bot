package com.telegrambot.marketplace.service.auth;

import com.telegrambot.marketplace.exception.CustomAuthenticationException;
import com.telegrambot.marketplace.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;


@RequiredArgsConstructor
@Component
public class TokenHelper {

    @Value("${jwt.token.secret}")
    private String secretKey;
    @Value("${jwt.token.algorithm}")
    private String algorithm;
    @Value("${jwt.token.expiration.access-token-hours}")
    private int accessHours;
    @Value("${jwt.token.expiration.refresh-token-days}")
    private int refreshDays;
    @Value("${jwt.token.expiration.email-token-hours}")
    private int emailHours;
    @Value("${jwt.token.expiration.jitsi-token-hours}")
    private int jitsiHours;
    private final TokenRepository tokenRepository;

    public String generateAccessToken(final Long clientId) {
        final ZonedDateTime now = ZonedDateTime.now();
        final Duration duration = Duration.ofHours(accessHours);
        final var accessExpirationDate = Date.from(now.plus(duration).toInstant());
        return createToken(clientId.toString(), accessExpirationDate, secretKey);
    }

    public String generateRefreshToken(final Long clientId) {
        final ZonedDateTime now = ZonedDateTime.now();
        final Duration duration = Duration.ofDays(refreshDays);
        final var refreshExpirationDate = Date.from(now.plus(duration).toInstant());
        return createToken(clientId.toString(), refreshExpirationDate, secretKey);
    }

    public String generateEmailToken(final Long clientId) {
        final ZonedDateTime now = ZonedDateTime.now();
        final Duration duration = Duration.ofHours(emailHours);
        final var refreshExpirationDate = Date.from(now.plus(duration).toInstant());
        return createToken(clientId.toString(), refreshExpirationDate, secretKey);
    }

    public String generateJitsiToken(final String roomName) {
        final ZonedDateTime now = ZonedDateTime.now();
        final Duration duration = Duration.ofHours(jitsiHours);
        final var refreshExpirationDate = Date.from(now.plus(duration).toInstant());
        return createToken(roomName, refreshExpirationDate, secretKey);
    }

    private String createToken(final String subject, final Date expiration, final String secret) {
        final Date issuedAt = new Date(System.currentTimeMillis());
        final Claims claims = Jwts.claims()
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, encodeSecret(secret))
                .compact();
    }

    private Key encodeSecret(final String secret) {
        return new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                algorithm
        );
    }

    public boolean validateToken(final String token) {
        try {
            if (!getTokenExpiration(token).before(new Date())) {
                return true;
            } else {
                tokenRepository.getByValue(token).ifPresent(tokenRepository::delete);
                return false;
            }

        } catch (final JwtException | IllegalArgumentException e) {
            throw new CustomAuthenticationException("Jwt token is expired or invalid");
        }
    }

    public Date getTokenExpiration(final String token) {
        return extractAllClaims(token, secretKey).getExpiration();
    }

    public String getTokenSubject(final String token) {
        return extractAllClaims(token, secretKey).getSubject();
    }

    private Claims extractAllClaims(final String token, final String secret) {
        try {
            return Jwts.parser().setSigningKey(encodeSecret(secret)).parseClaimsJws(token).getBody();
        } catch (SignatureException ex) {
            throw new BadCredentialsException("Invalid JWT signature", ex);
        } catch (MalformedJwtException ex) {
            throw new BadCredentialsException("Invalid JWT token", ex);
        } catch (ExpiredJwtException ex) {
            return ex.getClaims();
        } catch (UnsupportedJwtException ex) {
            throw new BadCredentialsException("Unsupported JWT token", ex);
        } catch (IllegalArgumentException ex) {
            throw new BadCredentialsException("JWT claims string is empty", ex);
        }
    }

}
