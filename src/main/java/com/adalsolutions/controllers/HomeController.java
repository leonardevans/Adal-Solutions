package com.adalsolutions.controllers;

import com.adalsolutions.repositories.ProductCategoryRepository;
import com.adalsolutions.repositories.TestimonialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    TestimonialRepository testimonialRepository;

    @GetMapping({"/", "/home"})
    public String showHome(Model model){
        model.addAttribute("testimonials", testimonialRepository.findAll());
        model.addAttribute("productCategories", productCategoryRepository.findAll());
        return "index";
    }

}
