package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Tag(name = "AddProductCategoryRequestDto", description = "Объект для добавления категории продукта")
@Data
public class AddProductCategoryRequestDto {

    @NotBlank
    @Schema(description = "Категория продукта", example = "ORGANIC", required = true)
    private String productCategory;

}
