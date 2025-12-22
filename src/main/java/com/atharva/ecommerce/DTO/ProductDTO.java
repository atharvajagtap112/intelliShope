package com.atharva.ecommerce.DTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ProductDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private double price;
    private String description;
    private int discountPresent;
    private int discountPrice;
    private String brand;
    private int quantity;
    private String color;
    private Set<SizeDTO> sizes = new HashSet<>();
    private String imageUrl;
    private int numRating;
    private Double averageRating; // Calculated field
    private String categoryName;
    private LocalDateTime createdAt;

    // Default constructor
    public ProductDTO() {}

    // All fields constructor
    public ProductDTO(Long id, String title, double price, String description,
                      int discountPresent, int discountPrice, String brand,
                      int quantity, String color, Set<SizeDTO> sizes,
                      String imageUrl, int numRating, Double averageRating,
                      String categoryName, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.discountPresent = discountPresent;
        this. discountPrice = discountPrice;
        this.brand = brand;
        this.quantity = quantity;
        this.color = color;
        this.sizes = sizes;
        this.imageUrl = imageUrl;
        this.numRating = numRating;
        this.averageRating = averageRating;
        this. categoryName = categoryName;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDiscountPresent() {
        return discountPresent;
    }

    public void setDiscountPresent(int discountPresent) {
        this.discountPresent = discountPresent;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Set<SizeDTO> getSizes() {
        return sizes;
    }

    public void setSizes(Set<SizeDTO> sizes) {
        this.sizes = sizes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getNumRating() {
        return numRating;
    }

    public void setNumRating(int numRating) {
        this.numRating = numRating;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}