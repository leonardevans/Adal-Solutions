package com.adalsolutions.controllers;

import com.adalsolutions.models.PostCategory;
import com.adalsolutions.payload.PostCategoryRequest;
import com.adalsolutions.repositories.PostCategoryRepository;
import com.adalsolutions.repositories.PostRepository;
import com.adalsolutions.services.PostCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class PostCategoryController {
    @Autowired
    private PostCategoryService postCategoryService;

    @Autowired
    private PostCategoryRepository postCategoryRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping({"/admin/post_categories/**", "/admin/post_categories"})
    public String showPostCategories(Model model){
        model.addAttribute("allPostCategories", postCategoryService.getAll());
        model.addAttribute("postCategoryRequest", new PostCategoryRequest());
        return "admin/post_categories";
    }

    @PostMapping("/admin/add/post_category")
    public String addPostCategory(@Valid @ModelAttribute("postCategoryRequest") PostCategoryRequest postCategoryRequest, BindingResult bindingResult){
        if (postCategoryRepository.existsByName(postCategoryRequest.getCatName())){
            bindingResult.addError(new FieldError("postCategoryRequest","name", "Post category name exist"));
        }

        if (bindingResult.hasErrors()){
            return "admin/post_categories";
        }

        postCategoryService.save(new PostCategory(postCategoryRequest.getCatName()));

        return "redirect:/admin/post_categories?add_success";
    }

    @GetMapping("/admin/edit/post_category/{id}")
    public String editPostCategory(@PathVariable("id") int id, Model model){
        Optional<PostCategory> optionalPostCategory = postCategoryRepository.findById(id);
        optionalPostCategory.ifPresent(postCategory -> model.addAttribute("postCategory", postCategory));
        return "admin/post_categories";

    }

    @PostMapping("/admin/update/post_category")
    public String updatePostCategory(@Valid @ModelAttribute("postCategory") PostCategory postCategory, BindingResult bindingResult){
        Optional<PostCategory> optionalPostCategory = Optional.ofNullable(postCategoryRepository.findByName(postCategory.getName()));
        if (optionalPostCategory.isPresent()){
            if (postCategory.getId() != optionalPostCategory.get().getId()){
                bindingResult.addError(new FieldError("postCategory","name", "Post category name exist"));
            }
        }


        if (bindingResult.hasErrors()){
            return "admin/post_categories";
        }

        postCategoryService.save(postCategory);

        return "redirect:/admin/post_categories?update_success";
    }

    @GetMapping("/admin/delete/post_category/{id}")
    public String deletePostCategory(@PathVariable("id") int id){
        if (postRepository.existsByPostCategoryId(id)){
            return "redirect:/admin/post_categories?delete_error";
        }
        postCategoryRepository.deleteById(id);;
        return "redirect:/admin/post_categories?delete_success";
    }
}
