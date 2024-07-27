package com.telegrambot.marketplace.service.entity;

import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;

public interface ProductSubcategoryService {

    ProductSubcategory findByName(String productCategoryName);

    ProductSubcategory save(ProductSubcategory subcategory);

    ProductSubcategory findById(Long subcategoryId);
}
