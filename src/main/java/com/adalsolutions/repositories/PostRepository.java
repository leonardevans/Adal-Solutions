package com.adalsolutions.repositories;

import com.adalsolutions.models.Post;
import com.adalsolutions.models.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Repository
public interface PostRepository  extends JpaRepository<Post, Integer> {
    List<Post> findAllByPublished(boolean published);
    boolean existsByPostCategoryId(int id);
    boolean existsByTitle(String title);
    Post findByTitle(String title);
    List<Post> findAllByPostCategoryAndPublished(PostCategory postCategory, boolean published);

    @Query("SELECT p FROM Post p INNER JOIN p.postCategory c WHERE CONCAT(p.title, c.name, p.details) LIKE %?1% AND p.published = true ")
    List<Post> findAllByTitleContainsOrDetailsContainsOrPostCategoryN( String title);
}
