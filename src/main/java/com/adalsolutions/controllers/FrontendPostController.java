package com.adalsolutions.controllers;

import com.adalsolutions.models.Comment;
import com.adalsolutions.models.Post;
import com.adalsolutions.models.PostCategory;
import com.adalsolutions.models.User;
import com.adalsolutions.payload.CommentRequest;
import com.adalsolutions.repositories.CommentRepository;
import com.adalsolutions.repositories.PostCategoryRepository;
import com.adalsolutions.repositories.PostRepository;
import com.adalsolutions.repositories.UserRepository;
import com.adalsolutions.security.AuthUtil;
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
import java.util.List;
import java.util.Optional;

@Controller
public class FrontendPostController {
    @Autowired
    PostRepository postRepository;

    @Autowired
    PostCategoryRepository postCategoryRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @GetMapping("/blog")
    public String showBlog(Model model){
        model.addAttribute("postCategories", postCategoryRepository.findAll());
        model.addAttribute("posts", postRepository.findAllByPublished(true));
        return "blog";
    }

    @GetMapping("/view/post/{id}")
    public String showPost(@PathVariable("id") int id, Model model){
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()){
            return "redirect:/blog";
        }

        List<Post> relatedPosts = postRepository.findAllByPostCategoryAndPublished(optionalPost.get().getPostCategory(), true);
        relatedPosts.remove(optionalPost.get());
        model.addAttribute("post", optionalPost.get());
        model.addAttribute("relatedPosts", relatedPosts);
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setPostId(id);
        model.addAttribute("commentRequest", commentRequest);
        return "post";
    }

    @GetMapping("/blog/{categoryId}")
    public String showPostsByCategory(@PathVariable("categoryId") int categoryId, Model model){
        Optional<PostCategory> optionalPostCategory = postCategoryRepository.findById(categoryId);
        if (optionalPostCategory.isEmpty()){
            return "redirect:/blog";
        }
        model.addAttribute("postCategories", postCategoryRepository.findAll());
        model.addAttribute("posts", postRepository.findAllByPostCategoryAndPublished(optionalPostCategory.get(), true));
        return "blog";
    }

    @PostMapping("/view/post/comment")
    public String makeComment(@Valid @ModelAttribute("commentRequest")CommentRequest commentRequest, BindingResult bindingResult, Model model){
        Optional<Post> optionalPost = postRepository.findById(commentRequest.getPostId());
        if (optionalPost.isEmpty()){
            return "redirect:/blog";
        }

        List<Post> relatedPosts = postRepository.findAllByPostCategoryAndPublished(optionalPost.get().getPostCategory(), true);
        relatedPosts.remove(optionalPost.get());
        model.addAttribute("post", optionalPost.get());
        model.addAttribute("relatedPosts", relatedPosts);
        model.addAttribute("commentRequest", commentRequest);

        User user = authUtil.getLoggedInUser();
        if (user == null){
            if (userRepository.existsByUsername(commentRequest.getUsername())){
                bindingResult.addError(new FieldError("commentRequest", "username", "This username is taken"));
            }
        }else{
            commentRequest.setUsername(user.getUsername());
        }

        if (bindingResult.hasErrors()){
            return "post";
        }

        Comment comment = new Comment(commentRequest.getUsername(), commentRequest.getComment());
        comment.setPost(optionalPost.get());
        commentRepository.save(comment);

        return "redirect:/view/post/"+commentRequest.getPostId() + "?comment_posted";
    }
}
