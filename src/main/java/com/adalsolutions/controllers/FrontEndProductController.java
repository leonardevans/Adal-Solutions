package com.adalsolutions.controllers;

import com.adalsolutions.models.PostCategory;
import com.adalsolutions.models.Product;
import com.adalsolutions.models.ProductCategory;
import com.adalsolutions.repositories.ProductCategoryRepository;
import com.adalsolutions.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class FrontEndProductController {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @GetMapping("/products")
    public String showProducts(Model model){
        model.addAttribute("products", productRepository.findAllByPublished(true));
        model.addAttribute("productCategories", productCategoryRepository.findAll());
        return "products";
    }

    @GetMapping("/view/product/{id}")
    public String showProduct(@PathVariable("id") int id, Model model){
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()){
            return "redirect:/products";
        }

        List<Product> relatedProducts = productRepository.findAllByProductCategoryIdAndPublished(optionalProduct.get().getProductCategory().getId(), true);
        relatedProducts.remove(optionalProduct.get());
        model.addAttribute("product", optionalProduct.get());
        model.addAttribute("relatedProducts", relatedProducts);
        return "product";
    }

    @GetMapping("/products/{categoryId}")
    public String showPostsByCategory(@PathVariable("categoryId") int categoryId, Model model){
        Optional<ProductCategory> optionalProductCategory = productCategoryRepository.findById(categoryId);
        if (optionalProductCategory.isEmpty()){
            return "redirect:/products";
        }
        model.addAttribute("productCategories", productCategoryRepository.findAll());
        model.addAttribute("products", productRepository.findAllByProductCategoryIdAndPublished(optionalProductCategory.get().getId(), true));
        return "products";
    }
}
