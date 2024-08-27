package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Tag(name = "AddProductSubcategoryRequestDto", description = "Объект для добавления подкатегории продукта")
@Data
public class AddProductSubcategoryRequestDto {
    @NotBlank
    @Schema(description = "Категория продукта", example = "ORGANIC", required = true)
    private String productCategory;

    @NotBlank
    @Schema(description = "Подкатегория продукта", example = "CANNABIS", required = true)
    private String productSubcategory;
}
