package com.adalsolutions.controllers;

import com.adalsolutions.models.Image;
import com.adalsolutions.models.PostCategory;
import com.adalsolutions.models.ProductCategory;
import com.adalsolutions.payload.PostCategoryRequest;
import com.adalsolutions.payload.ProductCategoryRequest;
import com.adalsolutions.repositories.ProductCategoryRepository;
import com.adalsolutions.repositories.ProductRepository;
import com.adalsolutions.services.FileUploadService;
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
import java.io.IOException;
import java.util.Optional;

@Controller
public class ProductCategoryController {
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    ProductRepository productRepository;

    @GetMapping({"/admin/product_categories/**", "/admin/product_categories"})
    public String showProductCategories(Model model) {
        model.addAttribute("allProductCategories", productCategoryRepository.findAll());
        model.addAttribute("productCategoryRequest" , new ProductCategoryRequest());
        return "admin/product_categories";
    }

    @PostMapping("/admin/add/product_category")
    public String addProductCategory(@Valid @ModelAttribute("productCategoryRequest") ProductCategoryRequest productCategoryRequest, BindingResult bindingResult) throws IOException {
        if (productCategoryRepository.existsByName(productCategoryRequest.getCatName())){
            bindingResult.addError(new FieldError("productCategoryRequest","catName", "Product category name exist"));
        }

        if (  productCategoryRequest.getImage().isEmpty() && productCategoryRequest.getImage() != null){
            bindingResult.addError(new FieldError("productCategoryRequest","image", "Product image is required"));
        }

        if (bindingResult.hasErrors()){
            return "admin/product_categories";
        }

        String filename =  fileUploadService.uploadToLocal(productCategoryRequest.getImage(),"uploads/images/product/");
        if (filename == null){
            return  "redirect:/admin/product_categories?add_error";
        }

        Image image = new Image("/" +filename, "main");

        productCategoryRepository.save(new ProductCategory(productCategoryRequest.getCatName(), image));

        return "redirect:/admin/product_categories?add_success";
    }

    @GetMapping("/admin/edit/product_category/{id}")
    public String editProductCategory(@PathVariable("id") int id, Model model){
        Optional<ProductCategory> optionalProductCategory = productCategoryRepository.findById(id);
        if (optionalProductCategory.isPresent()){
            ProductCategory productCategory = optionalProductCategory.get();
            ProductCategoryRequest productCategoryRequestEdit = new ProductCategoryRequest();
            productCategoryRequestEdit.setId(productCategory.getId());
            productCategoryRequestEdit.setCatName(productCategory.getName());
            productCategoryRequestEdit.setImageUrl(productCategory.getImage().getUrl());;
            model.addAttribute("productCategoryRequestEdit", productCategoryRequestEdit);
        }
        return "admin/product_categories";
    }

    @PostMapping("/admin/update/product_category")
    public String updateProductCategory(@Valid @ModelAttribute("productCategoryRequestEdit") ProductCategoryRequest productCategoryRequest, BindingResult bindingResult) throws IOException {
        Optional<ProductCategory> optionalProductCategory = Optional.ofNullable(productCategoryRepository.findByName(productCategoryRequest.getCatName()));
        if (optionalProductCategory.isPresent()){
            if (productCategoryRequest.getId() != optionalProductCategory.get().getId()){
                bindingResult.addError(new FieldError("productCategoryRequestEdit","name", "Product category name exist"));
            }
        }


        if (bindingResult.hasErrors()){
            return "admin/product_categories";
        }

        Optional<ProductCategory> optionalProductCategory1 = productCategoryRepository.findById(productCategoryRequest.getId());
        if (optionalProductCategory1.isEmpty()){
            return  "redirect:/admin/product_categories?update_error";
        }

        ProductCategory productCategory = optionalProductCategory1.get();

        productCategory.setName(productCategoryRequest.getCatName());

        if ( !productCategoryRequest.getImage().isEmpty()){
            String filename =  fileUploadService.uploadToLocal(productCategoryRequest.getImage(),"uploads/images/product/");
            if (filename == null){
                return  "redirect:/admin/product_categories?update_error";
            }
            Image image = new Image("/" +filename, "main");
            productCategory.setImage(image);
            fileUploadService.deleteLocalFile(productCategoryRequest.getImageUrl().substring(1));
        }


        productCategoryRepository.save(productCategory);

        return "redirect:/admin/product_categories?update_success";
    }

    @GetMapping("/admin/delete/product_category/{id}")
    public String deleteProductCategory(@PathVariable("id") int id) throws IOException {
        if (productRepository.existsByProductCategoryId(id)){
            return "redirect:/admin/product_categories?delete_error";
        }
        Optional<ProductCategory> optionalProductCategory = productCategoryRepository.findById(id);
        if (optionalProductCategory.isPresent()){
            fileUploadService.deleteLocalFile(optionalProductCategory.get().getImage().getUrl().substring(1));
        }
        productCategoryRepository.deleteById(id);;
        return "redirect:/admin/product_categories?delete_success";
    }
}
