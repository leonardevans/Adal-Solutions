package com.adalsolutions.repositories;

import com.adalsolutions.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAllByPublished(boolean published);
    boolean existsByProductCategoryId(int id);
    boolean existsByName(String name);
    Product findByName(String name);
    List<Product> findAllByProductCategoryIdAndPublished(int id, boolean published);

    @Query("SELECT p FROM Product p INNER JOIN p.productCategory c WHERE CONCAT(p.name, p.price, c.name, p.description) LIKE %?1% AND p.published = true ")
    List<Product> findAllByNameContainsOrDescriptionContainsOrProductCategoryContains(String search);
}
