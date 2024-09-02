package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Tag(name = "AddProductRequestDto", description = "Объект для добавления продукта")
@Data
public class AddProductRequestDto {

    @NotBlank
    @Schema(description = "Подкатегория продукта", example = "CANNABIS", required = true)
    private String productSubcategory;

    @NotBlank
    @Schema(description = "Имя продукта", example = "Белый", required = true)
    private String productName;

    @NotBlank
    @Schema(description = "Описание", example = "")
    private String description;

    @Schema(description = "Price", example = "3.6", required = true)
    private BigDecimal price;
}
