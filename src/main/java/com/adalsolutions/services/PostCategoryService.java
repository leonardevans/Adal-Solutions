package com.adalsolutions.services;

import com.adalsolutions.models.PostCategory;

import java.util.List;

public interface PostCategoryService {
    List<PostCategory> getAll();
    void save(PostCategory postCategory);
}
