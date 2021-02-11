package com.adalsolutions.payload;

public class DashBoardRequest {
    private long users;
    private long posts;
    private long products;
    private long productCategories;
    private long postCategories;

    public DashBoardRequest(long users, long posts, long products, long productCategories, long postCategories) {
        this.users = users;
        this.posts = posts;
        this.products = products;
        this.productCategories = productCategories;
        this.postCategories = postCategories;
    }

    public long getUsers() {
        return users;
    }

    public void setUsers(long users) {
        this.users = users;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public long getProducts() {
        return products;
    }

    public void setProducts(long products) {
        this.products = products;
    }

    public long getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(long productCategories) {
        this.productCategories = productCategories;
    }

    public long getPostCategories() {
        return postCategories;
    }

    public void setPostCategories(long postCategories) {
        this.postCategories = postCategories;
    }
}
