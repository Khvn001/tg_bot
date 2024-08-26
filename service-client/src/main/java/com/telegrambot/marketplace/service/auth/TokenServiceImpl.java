package com.telegrambot.marketplace.service.auth;

import com.telegrambot.marketplace.dto.web.OneTokenResponseDto;
import com.telegrambot.marketplace.dto.web.TwoTokenResponseDto;
import com.telegrambot.marketplace.entity.user.Token;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.TokenType;
import com.telegrambot.marketplace.exception.CustomAuthenticationException;
import com.telegrambot.marketplace.exception.NotFoundException;
import com.telegrambot.marketplace.repository.TokenRepository;
import com.telegrambot.marketplace.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final TokenHelper tokenHelper;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public TwoTokenResponseDto refreshToken(final String accessToken, final String refreshToken) {
        Token access = tokenRepository.getByValue(accessToken).orElseThrow(NotFoundException::new);
        Token refresh = tokenRepository.getByValue(refreshToken).orElseThrow(NotFoundException::new);

        if (!((TokenType.ACCESS).equals(access.getTokenType())
                && (TokenType.REFRESH).equals(refresh.getTokenType()))) {
            throw new ValidationException("Sent tokens are not ACCESS and REFRESH");
        }

        String accessTokenClientId = tokenHelper.getTokenSubject(accessToken);
        String refreshTokenClientId = tokenHelper.getTokenSubject(refreshToken);

        if (accessTokenClientId.equals(refreshTokenClientId)) {
            if (tokenHelper.validateToken(refreshToken)) {
                User user = userRepository.findAllById(
                        Long.valueOf(refreshTokenClientId)).orElseThrow(NotFoundException::new);
                if (user != null) {
                    return createTokens(user);
                } else {
                    throw new NotFoundException("Client Not Found");
                }
            } else {
                throw new CustomAuthenticationException("Jwt token is expired or invalid");
            }
        } else {
            throw new CustomAuthenticationException("Access Token does not match with Refresh Token user");
        }
    }

    @Transactional
    @Override
    public TwoTokenResponseDto createTokens(final User user) {
        final OneTokenResponseDto accessTokenValue = createAccessToken(user);
        final OneTokenResponseDto refreshTokenValue = createRefreshToken(user);

        return new TwoTokenResponseDto(accessTokenValue.getToken(), refreshTokenValue.getToken());
    }

    @Override
    public OneTokenResponseDto createAccessToken(final User user) {
        final String accessTokenValue = tokenHelper.generateAccessToken(user.getId());
        final Long accessExpiration = tokenHelper.getTokenExpiration(accessTokenValue).getTime();

        final Token accessToken = new Token();
        accessToken.setUser(user);
        accessToken.setValue(accessTokenValue);
        accessToken.setExpiredTime(accessExpiration);
        accessToken.setTokenType(TokenType.ACCESS);

        tokenRepository.deleteAllByUser(user);
        Token res = tokenRepository.save(accessToken);
        return new OneTokenResponseDto(res.getValue());
    }

    @Override
    public OneTokenResponseDto createRefreshToken(final User user) {
        final String refreshTokenValue = tokenHelper.generateRefreshToken(user.getId());
        final Long refreshExpiration = tokenHelper.getTokenExpiration(refreshTokenValue).getTime();

        final Token refreshToken = new Token();
        refreshToken.setUser(user);
        refreshToken.setValue(refreshTokenValue);
        refreshToken.setExpiredTime(refreshExpiration);
        refreshToken.setTokenType(TokenType.REFRESH);

        Token res = tokenRepository.save(refreshToken);
        return new OneTokenResponseDto(res.getValue());
    }
}
