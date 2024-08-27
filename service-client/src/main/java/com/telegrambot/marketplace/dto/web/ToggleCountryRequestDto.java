package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Tag(name = "ToggleCountryRequestDto", description = "Объект для изменения активности страны")
@Data
public class ToggleCountryRequestDto {
    @NotBlank
    @Schema(description = "Страна", example = "THAILAND", required = true)
    private String country;
}
