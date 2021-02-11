package com.adalsolutions.repositories;

import com.adalsolutions.models.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Integer> {
    boolean existsByName(String name);
    PostCategory findByName(String name);
}
