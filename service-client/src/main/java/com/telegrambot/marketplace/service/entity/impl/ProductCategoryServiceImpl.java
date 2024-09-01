package com.telegrambot.marketplace.service.entity.impl;

import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.enums.ProductCategoryName;
import com.telegrambot.marketplace.repository.ProductCategoryRepository;
import com.telegrambot.marketplace.service.entity.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public ProductCategory findByNameAndAllowedTrue(final String productCategoryName) {
        return productCategoryRepository.findByAllowedIsTrueAndName(ProductCategoryName.valueOf(productCategoryName))
                .orElse(null);
    }

    @Override
    public ProductCategory findByName(final String productCategoryName) {
        return productCategoryRepository.findByName(ProductCategoryName.valueOf(productCategoryName))
                .orElse(null);
    }

    @Override
    @Transactional
    public ProductCategory save(final ProductCategory category) {
        return productCategoryRepository.save(category);
    }

    @Override
    public ProductCategory findById(final Long categoryId) {
        return productCategoryRepository.findById(categoryId).orElse(null);
    }

}
