package com.adalsolutions.controllers;

import com.adalsolutions.models.Post;
import com.adalsolutions.models.Product;
import com.adalsolutions.repositories.PostCategoryRepository;
import com.adalsolutions.repositories.PostRepository;
import com.adalsolutions.repositories.ProductCategoryRepository;
import com.adalsolutions.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {
    @Autowired
    PostRepository postRepository;

    @Autowired
    ProductRepository productRepository;

    @PostMapping("/search")
    public String showSearch(@RequestParam("search") String search, Model model){
        if (search.isEmpty()){
            return "redirect:/";
        }
        List<Product> products = productRepository.findAllByNameContainsOrDescriptionContainsOrProductCategoryContains(search);
        List<Post> posts = postRepository.findAllByTitleContainsOrDetailsContainsOrPostCategoryN(search);

        model.addAttribute("posts", posts);
        model.addAttribute("products", products);

        return "search";
    }
}
