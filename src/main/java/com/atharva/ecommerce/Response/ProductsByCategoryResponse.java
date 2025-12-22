package com.atharva.ecommerce.Response;

import com.atharva.ecommerce.DTO.ProductDTO;
import com.atharva.ecommerce.Model.Product;

import java.io.Serializable;
import java.util.List;


public class ProductsByCategoryResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String categoryName;
    private List<ProductDTO> products;

    // Default constructor
    public ProductsByCategoryResponse() {}

    // All fields constructor
    public ProductsByCategoryResponse(String categoryName, List<ProductDTO> products) {
        this.categoryName = categoryName;
        this.products = products;
    }

    // Getters and Setters
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }
}
