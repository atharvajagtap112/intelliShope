package com.atharva.ecommerce.Service;

import com.atharva.ecommerce.Exception.ProductException;
import com.atharva.ecommerce.Model.Product;
import com.atharva.ecommerce.Request.CreateProductRequest;
import com.atharva.ecommerce.Request.ProductCategoryRequest;
import com.atharva.ecommerce.Response.ProductsByCategoryResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    public Product createProduct(CreateProductRequest req);
    public String deleteProduct(Long id) throws ProductException;
    public Product updateProduct(Long productId,Product req) throws ProductException;
    public Product findProductById(Long id) throws ProductException;
    public List<Product> findProductsByCategory(String Category) ;
    public List<ProductsByCategoryResponse> getProductsByCategoriesLogic(List<ProductCategoryRequest> categories);


    public Page<Product> getAllProduct(
            String category,
            List<String> colors,
            List<String> sizes,
            Integer minPrice,
            Integer maxPrice,
            Integer minDiscount,
            String sort,
            String stock,
            Integer pageNumber,
            Integer pageSize
    );

    public List<Product> findAllProducts();
}
