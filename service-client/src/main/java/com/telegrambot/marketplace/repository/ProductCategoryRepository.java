package com.telegrambot.marketplace.repository;

import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.enums.ProductCategoryName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Optional<ProductCategory> findByAllowedIsTrueAndName(ProductCategoryName name);

    Optional<ProductCategory> findByName(ProductCategoryName name);

    List<ProductCategory> findByAllowedIsFalse();

    List<ProductCategory> findByAllowedIsTrue();
}
