package com.adalsolutions.payload;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

public class ProductCategoryRequest {
    @NotEmpty
    private String catName;

    private String imageUrl;

    private MultipartFile image;

    private  int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
