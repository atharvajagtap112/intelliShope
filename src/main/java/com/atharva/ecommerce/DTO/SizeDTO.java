package com.atharva.ecommerce.DTO;

import java.io.Serializable;
import java.util.Objects;

public class SizeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int quantity;
    private int stock;

    // Default constructor
    public SizeDTO() {}

    // All fields constructor
    public SizeDTO(String name, int quantity, int stock) {
        this.name = name;
        this.quantity = quantity;
        this.stock = stock;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // Override equals and hashCode for Set operations
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SizeDTO sizeDTO = (SizeDTO) o;
        return quantity == sizeDTO.quantity &&
                stock == sizeDTO. stock &&
                Objects.equals(name, sizeDTO.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, quantity, stock);
    }
}