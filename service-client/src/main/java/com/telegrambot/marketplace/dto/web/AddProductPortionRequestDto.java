package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO for adding a new product portion by a courier")
public class AddProductPortionRequestDto {

    @Schema(description = "ID of the courier adding the product portion", example = "123456789", required = true)
    @NotNull
    private String courierId;

    @Schema(description = "ID of the country", example = "1", required = true)
    @NotNull
    private Long countryId;

    @Schema(description = "ID of the city", example = "1", required = true)
    @NotNull
    private Long cityId;

    @Schema(description = "ID of the district", example = "1", required = true)
    @NotNull
    private Long districtId;

    @Schema(description = "ID of the product category", example = "1", required = true)
    @NotNull
    private Long categoryId;

    @Schema(description = "ID of the product subcategory", example = "1", required = true)
    @NotNull
    private Long subcategoryId;

    @Schema(description = "ID of the product", example = "1", required = true)
    @NotNull
    private Long productId;

    @Schema(description = "Latitude of the product location", example = "52.5200", required = true)
    @NotNull
    private BigDecimal latitude;

    @Schema(description = "Longitude of the product location", example = "13.4050", required = true)
    @NotNull
    private BigDecimal longitude;

    @Schema(description = "Amount of the product portion", example = "5.0", required = true)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;
}
