package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Tag(name = "OneTokenResponseDto", description = "Ответ на запрос взаимодействия с механикой токенов")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OneTokenResponseDto {

    @NotNull
    @Schema(description = "Токен", example = "eyJhbGNiJ9.eyJDZ9.yL7qSo0upE", required = true)
    private String token;

}
