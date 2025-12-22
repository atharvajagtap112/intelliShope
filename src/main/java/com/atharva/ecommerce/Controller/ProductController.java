package com.atharva.ecommerce.Controller;

import com.atharva.ecommerce.Exception.ProductException;
import com.atharva.ecommerce.Model.Product;
import com.atharva.ecommerce.Repository.ProductRepository;
import com.atharva.ecommerce.Request.ProductCategoryRequest;
import com.atharva.ecommerce.Response.ProductsByCategoryResponse;
import com.atharva.ecommerce.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")



public class ProductController {
    @Autowired
     private ProductService productService;

   @Autowired
   private ProductRepository productRepository;


    @GetMapping()
    public ResponseEntity<Page<Product>> findProductByCategoryHandler(
            @RequestParam String category,
            @RequestParam List<String> color,
            @RequestParam List<String> size,
            @RequestParam Integer minPrice,
            @RequestParam Integer maxPrice,
            @RequestParam Integer minDiscount,
            @RequestParam String sort,
            @RequestParam String stock,
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize) {

        Page<Product> res = productService.getAllProduct(
                category, color, size, minPrice, maxPrice,
                minDiscount, sort, stock, pageNumber, pageSize
        );

        System.out.println("complete products");
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> findProductByIdHandler(@PathVariable Long productId) throws ProductException {
        Product product =productService.findProductById(productId);

        return new ResponseEntity<Product>(product, HttpStatus.ACCEPTED);
    }

//    @GetMapping("/products/search")
//    public ResponseEntity<List<Product>> searchProductHandler(@RequestParam String q) {
//        List<Product> products = productService.searchProduct(q);
//        return new ResponseEntity<>(products, HttpStatus.OK);
//    }


//
//    @PostMapping("/bycategories")
//    @Cacheable(value ="ProductsByCategories" ,key = "#categories.toString()")
//    public ResponseEntity<List<ProductsByCategoryResponse>> getProductsByCategoryHandler(@RequestBody List<ProductCategoryRequest> categories) throws ProductException {
//          List<ProductsByCategoryResponse> productsByCategoryResponseList =new ArrayList<>();
//        for (ProductCategoryRequest category : categories) {
//           List<Product> products= productService.findProductsByCategory(category.getCategoryName());
//           products=products.subList(0,Math.min(products.size(),10));
//
//           ProductsByCategoryResponse productsByCategoryResponse =new ProductsByCategoryResponse();
//           productsByCategoryResponse.setProducts(products);
//            productsByCategoryResponse.setCategoryName(category.getCategoryTitle());
//
//            productsByCategoryResponseList.add(productsByCategoryResponse);
//        }



//
//
//        return new ResponseEntity<>(productsByCategoryResponseList, HttpStatus.ACCEPTED);
//    }


    @PostMapping("/bycategories")
    public ResponseEntity<List<ProductsByCategoryResponse>> getProductsByCategoryHandler(@RequestBody List<ProductCategoryRequest> categories) throws ProductException {

        List<ProductsByCategoryResponse> data = productService.getProductsByCategoriesLogic(categories);
        return ResponseEntity.ok(data);
    }

    @PostMapping("delete/bycategories")
    public ResponseEntity<String> getProductsByCategoryHandler(@RequestParam String category) throws ProductException {


            List<Product> products= productService.findProductsByCategory(category);


                   productRepository.deleteAll(products);



        return new ResponseEntity<String>("deleted succesfully", HttpStatus.ACCEPTED);
    }

}
