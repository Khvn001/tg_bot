package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Tag(name = "ToggleCityRequestDto", description = "Объект для изменения активности страны")
@Data
public class ToggleCityRequestDto {

    @NotBlank
    @Schema(description = "Страна", example = "THAILAND", required = true)
    private String country;

    @NotBlank
    @Schema(description = "Город", example = "Pattaya", required = true)
    private String city;

}
