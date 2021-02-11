package com.adalsolutions.payload;

import javax.validation.constraints.NotEmpty;

public class PostCategoryRequest {
    @NotEmpty
    private String catName;

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
}
