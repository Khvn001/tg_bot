package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Tag(name = "ToggleProductSubcategoryRequestDto", description = "Объект для изменения активности подкатегории продукта")
@Data
public class ToggleProductSubcategoryRequestDto {

    @NotBlank
    @Schema(description = "Подкатегория продукта", example = "CANNABIS", required = true)
    private String productSubcategory;

}
