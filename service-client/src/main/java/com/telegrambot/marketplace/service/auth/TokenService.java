package com.telegrambot.marketplace.service.auth;

import com.telegrambot.marketplace.dto.web.OneTokenResponseDto;
import com.telegrambot.marketplace.dto.web.TwoTokenResponseDto;
import com.telegrambot.marketplace.entity.user.User;

public interface TokenService {

    TwoTokenResponseDto createTokens(User user);

    OneTokenResponseDto createAccessToken(User user);

    OneTokenResponseDto createRefreshToken(User user);

    TwoTokenResponseDto refreshToken(String accessToken, String refreshToken);

}
