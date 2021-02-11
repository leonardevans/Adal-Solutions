package com.adalsolutions.controllers;

import com.adalsolutions.models.*;
import com.adalsolutions.payload.ImagesRequest;
import com.adalsolutions.payload.PostRequest;
import com.adalsolutions.repositories.ImageRepository;
import com.adalsolutions.repositories.PostCategoryRepository;
import com.adalsolutions.repositories.PostRepository;
import com.adalsolutions.security.AuthUtil;
import com.adalsolutions.services.FileUploadService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PostController {
    @Autowired
    PostRepository postRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    PostCategoryRepository postCategoryRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    AuthUtil authUtil;

    @GetMapping({"/admin/posts", "/admin/posts/**"})
    public String showAdminPosts(Model model) {
        model.addAttribute("allPosts", postRepository.findAll());
        return "admin/posts";
    }

    @GetMapping("/admin/add/post")
    public String showAddPost(Model model) {
        model.addAttribute("postRequest", new PostRequest());
        model.addAttribute("categories", postCategoryRepository.findAll());
        return "admin/add_post";
    }

    @PostMapping("/admin/add/post")
    public String addPost(@Valid @ModelAttribute("postRequest") PostRequest postRequest, BindingResult bindingResult, Model model) throws IOException {
        model.addAttribute("categories", postCategoryRepository.findAll());
        if (postRepository.existsByTitle(postRequest.getTitle())) {
            bindingResult.addError(new FieldError("postRequest", "title", "Post title exist"));
        }

        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(postRequest.getFile1());
        multipartFiles.add(postRequest.getFile2());
        multipartFiles.add(postRequest.getFile3());

        List<MultipartFile> filesToUpload = new ArrayList<>();
        boolean noImagesToAdd = true;
        for (MultipartFile file: multipartFiles
        ) {
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            assert fileExtension != null;
            if ( !fileExtension.isEmpty()){
                filesToUpload.add(file);
                noImagesToAdd = false;
            }
        }

        if (noImagesToAdd){
            bindingResult.addError(new FieldError("postRequest", "file1", "Post should have at least one image"));
        }

        if (bindingResult.hasErrors()) {
            return "admin/add_post";
        }

        Set<Image> imagesToAdd = new HashSet<>();

        filesToUpload.forEach(file -> {
            String filename = null;
            try {
                filename = fileUploadService.uploadToLocal(file, "uploads/images/product/");
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            if (filename == null) {
                bindingResult.addError(new FieldError("productImagesRequest", "file1", "Error uploading product images"));
            }
            Image pImage = new Image("/" + filename, "other");
            imagesToAdd.add(pImage);
        });

        Optional<PostCategory> optionalPostCategory = postCategoryRepository.findById(postRequest.getCategoryId());
        if (optionalPostCategory.isEmpty()) {
            bindingResult.addError(new FieldError("productRequest", "categoryId", "Category is required"));
            return "admin/add_product";
        }

        if (bindingResult.hasErrors()){
            return "admin/add_post";
        }

        Post post = new Post(postRequest.getTitle(), postRequest.getDescription(), postRequest.isPublished(), optionalPostCategory.get());
        post.setImages(imagesToAdd);
        User user = authUtil.getLoggedInUser();
        post.setCreatedBy(user);

        Post addedPost = postRepository.save(post);
        return "redirect:/admin/view/post/" + addedPost.getId();
    }

    @GetMapping("/admin/view/post/{id}")
    public String showPost(@PathVariable("id") int id, Model model){
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()){
            return "redirect:/admin/posts?not_found";
        }
        model.addAttribute("post", optionalPost.get());
        return "admin/view_post";
    }

    @GetMapping("/admin/edit/post/{id}")
    public String showEditPost(@PathVariable("id") int id, Model model){
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()){
            return "redirect:/admin/post?not_found";
        }
        model.addAttribute("imagesRequest", new ImagesRequest());
        model.addAttribute("post", optionalPost.get());
        model.addAttribute("categories", postCategoryRepository.findAll());
        return "admin/edit_post";
    }

    @PostMapping("/admin/update/post")
    public String updatePost(@Valid @ModelAttribute("post") Post post, BindingResult bindingResult, @ModelAttribute("imagesRequest") ImagesRequest imagesRequest, Model model){
        model.addAttribute("categories", postCategoryRepository.findAll());
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(imagesRequest.getFile1());
        multipartFiles.add(imagesRequest.getFile2());
        multipartFiles.add(imagesRequest.getFile3());

        List<MultipartFile> filesToUpload = new ArrayList<>();
        boolean noImagesToAdd = true;
        for (MultipartFile file: multipartFiles
        ) {
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            assert fileExtension != null;
            if ( !fileExtension.isEmpty()){
                filesToUpload.add(file);
                noImagesToAdd = false;
            }
        }

        Optional<Post> optionalPost = postRepository.findById(post.getId());
        if(optionalPost.isEmpty()){
            return "redirect:/admin/posts?not_found";
        }
        Post postToSave = optionalPost.get();

        Post existingPost = postRepository.findByTitle(post.getTitle());
        if (existingPost != null && post.getId() != existingPost.getId()){
            bindingResult.addError(new FieldError("post", "title", "Post title is taken by another post"));
        }

        if (bindingResult.hasErrors()){
            return "admin/edit_post";
        }

        if (post.getImages().isEmpty() || post.getImages().size() == 0){
            if (noImagesToAdd){
                bindingResult.addError(new FieldError("post", "images", "To delete all images, you need to upload a new image for this post"));
                return "admin/edit_post";
            }
        }

        Set<Image> imagesToAdd = new HashSet<>();

        filesToUpload.forEach(file -> {
            String filename = null;
            try {
                filename = fileUploadService.uploadToLocal(file, "uploads/images/product/");
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            if (filename == null) {
                bindingResult.addError(new FieldError("imagesRequest", "file1", "Error uploading product images"));
            }
            Image pImage = new Image("/" + filename, "other");
            imagesToAdd.add(pImage);
        });

        if (bindingResult.hasErrors()){
            return "admin/edit_product";
        }

        Set<Image> deletedImages = postToSave.getImages().stream().filter(image -> !post.getImages().contains(image)).collect(Collectors.toSet());

        deletedImages.forEach(imageToDelete -> {
            try {
                fileUploadService.deleteLocalFile(imageToDelete.getUrl());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        postToSave.setTitle(post.getTitle());
        postToSave.setPostCategory(post.getPostCategory());
        postToSave.setDetails(post.getDetails());
        postToSave.setImages(post.getImages());
        postToSave.getImages().addAll(imagesToAdd);
        postToSave.setPublished(post.isPublished());

        Post savedPost = postRepository.save(postToSave);
        return "redirect:/admin/view/post/" + savedPost.getId();
    }

    @GetMapping("/admin/delete/post/{id}")
    public String deletePost(@PathVariable("id") int id){
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()){
            return "redirect:/admin/posts?not_found";
        }

        optionalPost.get().getImages().forEach(image -> {
            try {
                fileUploadService.deleteLocalFile(image.getUrl());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        postRepository.delete(optionalPost.get());
        return "redirect:/admin/posts?delete_success";
    }
}
