package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Tag(name = "ToggleProductRequestDto", description = "Объект для изменения активности продукта")
@Data
public class ToggleProductRequestDto {

    @NotBlank
    @Schema(description = "Категория продукта", example = "ORGANIC", required = true)
    private String productCategory;

    @NotBlank
    @Schema(description = "Подкатегория продукта", example = "CANNABIS", required = true)
    private String productSubcategory;

    @NotBlank
    @Schema(description = "Наименование продукта", example = "CANNABIS", required = true)
    private String productName;

}
