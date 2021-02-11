package com.adalsolutions.models;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    private String name;

    @NotNull
    private double price;

    @NotNull
    @Column(columnDefinition = "integer default 25")
    private int stock ;

    @NotEmpty
    private String description;

    private boolean published;

    @ManyToOne()
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private ProductCategory productCategory;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Image> images = new HashSet<>();

    public Product() {
    }

    public Product(String name, double price, int stock, String description, ProductCategory productCategory) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.productCategory = productCategory;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }
}
