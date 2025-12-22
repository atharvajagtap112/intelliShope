package com.atharva.ecommerce.Repository;

import com.atharva.ecommerce.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    @Query("SELECT p FROM Product p " +
            "WHERE (p.category.name = :category OR :category = '') " +
            "AND ((:minPrice IS NULL AND :maxPrice IS NULL) OR (p.discountPrice BETWEEN :minPrice AND :maxPrice))" +
            "AND (:minDiscount IS NULL OR p.discountPresent >= :minDiscount) " +
            "ORDER BY " +
            "CASE WHEN :sort = 'price_low' THEN p.discountPrice END ASC, " +
            "CASE WHEN :sort = 'price_high' THEN p.discountPrice END DESC")
    public List<Product> filterProduct(@Param("category") String category,
                                       @Param("minPrice") Integer minPrice,
                                       @Param("maxPrice") Integer maxPrice,
                                       @Param("minDiscount") Integer minDiscount,
                                       @Param("sort") String sort
                                       );


    @Query("SELECT p FROM Product p WHERE p.category.name = :category")
    List<Product> findbyCategory(@Param("category") String category);



    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +
            "WHERE p.category. name = :categoryName")
    List<Product> findProductsByCategoryWithSizes(@Param("categoryName") String categoryName);
}
