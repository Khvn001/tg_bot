package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Tag(name = "TwoTokenResponseDto", description = "Генерированные Access и Refresh токенов")
@Data
@AllArgsConstructor
public class TwoTokenResponseDto {

    @NotNull
    @Schema(description = "Access Токен", example = "eyJhbGNiJ9.eyJDZ9.yL7qSo0upE",
            required = true)
    private String accessToken;

    @NotNull
    @Schema(description = "Refresh Токен", example = "eyJhbGNiJ9.eyJDZ9.yL7qSo0upE",
            required = true)
    private String refreshToken;

}
