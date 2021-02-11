package com.adalsolutions.controllers;

import com.adalsolutions.models.Image;
import com.adalsolutions.models.Product;
import com.adalsolutions.models.ProductCategory;
import com.adalsolutions.payload.ImagesRequest;
import com.adalsolutions.payload.ProductRequest;
import com.adalsolutions.repositories.ImageRepository;
import com.adalsolutions.repositories.ProductCategoryRepository;
import com.adalsolutions.repositories.ProductRepository;
import com.adalsolutions.services.FileUploadService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

        @Autowired
        private FileUploadService fileUploadService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private ImageRepository imageRepository;

    @GetMapping({"/admin/products", "/admin/products/**"})
    public String showAdminProducts(Model model) {
        model.addAttribute("allProducts", productRepository.findAll());
        return "admin/products";
    }

    @GetMapping("/admin/add/product")
    public String showAddProduct(Model model) {
        model.addAttribute("productRequest", new ProductRequest());
        model.addAttribute("categories", productCategoryRepository.findAll());
        return "admin/add_product";
    }

    @PostMapping("/admin/add/product")
    public String addProduct(@Valid @ModelAttribute("productRequest") ProductRequest productRequest, BindingResult bindingResult, Model model) throws IOException {
        model.addAttribute("categories", productCategoryRepository.findAll());
        if (productRepository.existsByName(productRequest.getName())) {
            bindingResult.addError(new FieldError("productRequest", "name", "Product name exist"));
        }

        try {
            if (productRequest.getFile1().isEmpty() && productRequest.getFile1() != null) {
                bindingResult.addError(new FieldError("productRequest", "file1", "Product should have at least one image"));
            }
        } catch (NullPointerException e) {
            bindingResult.addError(new FieldError("productRequest", "file1", "Product should have at least one image"));
        }

        Product existingProduct = productRepository.findByName(productRequest.getName());
        if (existingProduct != null ){
            bindingResult.addError(new FieldError("product", "name", "Product name is taken by another product"));
        }


        if (bindingResult.hasErrors()) {
            return "admin/add_product";
        }

        Set<Image> pImages = new HashSet<>();
        if (!productRequest.getFile1().isEmpty() && productRequest.getFile1() != null) {
            String filename = fileUploadService.uploadToLocal(productRequest.getFile1(), "uploads/images/product/");
            if (filename == null) {
                bindingResult.addError(new FieldError("productRequest", "file1", "Error uploading product images"));
                return "admin/add_product";
            }
            Image pImage = new Image("/" + filename, "main");
            pImages.add(pImage);
        }

        if (!productRequest.getFile2().isEmpty() && productRequest.getFile2() != null) {
            String filename = fileUploadService.uploadToLocal(productRequest.getFile2(), "uploads/images/product/");
            if (filename == null) {
                bindingResult.addError(new FieldError("productRequest", "file1", "Error uploading product images"));
                return "admin/add_product";
            }
            Image pImage = new Image("/" + filename, "other");
            pImages.add(pImage);
        }

        if (!productRequest.getFile3().isEmpty() && productRequest.getFile3() != null) {
            String filename = fileUploadService.uploadToLocal(productRequest.getFile3(), "uploads/images/product/");
            if (filename == null) {
                bindingResult.addError(new FieldError("productRequest", "file1", "Error uploading product images"));
                return "admin/add_product";
            }
            Image pImage = new Image("/" + filename, "other");
            pImages.add(pImage);
        }

        Optional<ProductCategory> optionalProductCategory = productCategoryRepository.findById(productRequest.getCategoryId());
        if (optionalProductCategory.isEmpty()) {
            bindingResult.addError(new FieldError("productRequest", "categoryId", "Category is required"));
            return "admin/add_product";
        }
        Product product = new Product(productRequest.getName(), productRequest.getPrice(), productRequest.getStock(), productRequest.getDescription(), optionalProductCategory.get());
        product.setPublished(productRequest.isPublished());
        List<Image> savedImages = imageRepository.saveAll(pImages);
        savedImages.forEach(image -> product.getImages().add(image));

        Product addedProduct = productRepository.save(product);
        return "redirect:/admin/view/product/" + addedProduct.getId();
    }

    @GetMapping("/admin/view/product/{id}")
    public String showProduct(@PathVariable("id") int id, Model model){
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()){
            return "redirect:/admin/products?not_found";
        }
        model.addAttribute("product", optionalProduct.get());
        return "admin/view_product";
    }

    @GetMapping("/admin/edit/product/{id}")
    public String showEditProduct(@PathVariable("id") int id, Model model){
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()){
            return "redirect:/admin/products?not_found";
        }
        model.addAttribute("imagesRequest", new ImagesRequest());
        model.addAttribute("product", optionalProduct.get());
        model.addAttribute("categories", productCategoryRepository.findAll());
        return "admin/edit_product";
    }

    @PostMapping("/admin/update/product")
    public String updateProduct(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult, @ModelAttribute("imagesRequest") ImagesRequest imagesRequest, Model model){
        model.addAttribute("categories", productCategoryRepository.findAll());
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

        Optional<Product> optionalProduct = productRepository.findById(product.getId());
        if(optionalProduct.isEmpty()){
            return "redirect:/admin/products?not_found";
        }
        Product productToSave = optionalProduct.get();


        Product existingProduct = productRepository.findByName(product.getName());
        if (existingProduct != null && product.getId() != existingProduct.getId()){
            bindingResult.addError(new FieldError("product", "name", "Product name is taken by another product"));
        }

        if (bindingResult.hasErrors()){
            return "admin/edit_product";
        }

        if (product.getImages().isEmpty() || product.getImages().size() == 0){
            if (noImagesToAdd){
                bindingResult.addError(new FieldError("product", "images", "To delete all images, you need to upload a new image for this product"));
                return "admin/edit_product";
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

        Set<Image> deletedImages = productToSave.getImages().stream().filter(image -> !product.getImages().contains(image)).collect(Collectors.toSet());

        deletedImages.forEach(imageToDelete -> {
            try {
                fileUploadService.deleteLocalFile(imageToDelete.getUrl());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        productToSave.setName(product.getName());
        productToSave.setProductCategory(product.getProductCategory());
        productToSave.setPrice(product.getPrice());
        productToSave.setStock(product.getStock());
        productToSave.setDescription(product.getDescription());
        productToSave.setImages(product.getImages());
        productToSave.getImages().addAll(imagesToAdd);
        productToSave.setPublished(product.isPublished());


        Product savedProduct = productRepository.save(productToSave);
        return "redirect:/admin/view/product/" + savedProduct.getId();
    }

    @GetMapping("/admin/delete/product/{id}")
    public String deleteProduct(@PathVariable("id") int id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()){
            return "redirect:/admin/products?not_found";
        }

        optionalProduct.get().getImages().forEach(image -> {
            try {
                fileUploadService.deleteLocalFile(image.getUrl());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        productRepository.delete(optionalProduct.get());
        return "redirect:/admin/products?delete_success";
    }


}
