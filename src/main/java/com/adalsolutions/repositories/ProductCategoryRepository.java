package com.adalsolutions.repositories;

import com.adalsolutions.models.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
    boolean existsByName(String name);
    ProductCategory findByName(String name);

}
