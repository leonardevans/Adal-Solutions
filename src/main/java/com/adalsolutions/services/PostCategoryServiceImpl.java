package com.adalsolutions.services;

import com.adalsolutions.models.PostCategory;
import com.adalsolutions.repositories.PostCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostCategoryServiceImpl implements PostCategoryService{
    @Autowired
    PostCategoryRepository postCategoryRepository;

    @Override
    public List<PostCategory> getAll() {
        return postCategoryRepository.findAll();
    }

    @Override
    public void save(PostCategory postCategory) {
        postCategoryRepository.save(postCategory);
    }
}
