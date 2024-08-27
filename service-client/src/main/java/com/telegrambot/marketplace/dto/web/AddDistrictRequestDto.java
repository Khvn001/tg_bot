package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Tag(name = "AddDistrictRequestDto", description = "Объект для добавления района")
@Data
public class AddDistrictRequestDto {

    @NotBlank
    @Schema(description = "Страна", example = "THAILAND", required = true)
    private String country;

    @NotBlank
    @Schema(description = "Город", example = "Phuket", required = true)
    private String city;

    @NotBlank
    @Schema(description = "Район", example = "Walking Street", required = true)
    private String district;
}
