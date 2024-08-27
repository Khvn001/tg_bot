package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Tag(name = "AddCountryRequestDto", description = "Объект для добавления страны")
@Data
public class AddCountryRequestDto {

    @NotBlank
    @Schema(description = "Страна", example = "THAILAND", required = true)
    private String country;

}
