package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Tag(name = "ToggleProductCategoryRequestDto", description = "Объект для изменения активности категории продукта")
@Data
public class ToggleProductCategoryRequestDto {

    @NotBlank
    @Schema(description = "Категория продукта", example = "ORGANIC", required = true)
    private String productCategory;

}
