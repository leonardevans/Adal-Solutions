package com.adalsolutions.controllers;

import com.adalsolutions.payload.DashBoardRequest;
import com.adalsolutions.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PostCategoryRepository postCategoryRepository;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @GetMapping("/admin/dashboard")
    public String showDashboard(Model model){
        DashBoardRequest dashBoardRequest = new DashBoardRequest(userRepository.count(),postRepository.count(),productRepository.count(),productCategoryRepository.count(), postCategoryRepository.count());
        model.addAttribute("dashBoardRequest",dashBoardRequest);
        return "admin/dashboard";
    }
}
