package com.atharva.ecommerce.Controller;

import com.atharva.ecommerce.Exception.CartItemException;
import com.atharva.ecommerce.Exception.ProductException;
import com.atharva.ecommerce.Exception.UserException;
import com.atharva.ecommerce.Model.Cart;
import com.atharva.ecommerce.Model.User;
import com.atharva.ecommerce.Request.AddItemRequest;
import com.atharva.ecommerce.Response.ApiResponse;
import com.atharva.ecommerce.Service.CartService;
import com.atharva.ecommerce.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cart")

public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<Cart>findUserCart(@RequestHeader("Authorization") String jwt) throws UserException {
        try {
           User user=  userService.findUserProfileByJwt(jwt);
       Cart cart= cartService.findUserCart(user.getId());
      return new ResponseEntity<Cart>(cart,HttpStatus.OK);

        } catch (Exception e) {
            // Log the error for debugging
            System. err.println("Error fetching cart: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping("/add")
    public ResponseEntity<ApiResponse> addItemToCart(@RequestHeader("Authorization") String jwt,
                                                     @RequestBody AddItemRequest req) throws UserException, CartItemException, ProductException {

        try{

        User user=  userService.findUserProfileByJwt(jwt);
        cartService.addCartItem(user.getId(), req);
     return new ResponseEntity<ApiResponse>(new ApiResponse("item added to cart",true),HttpStatus.CREATED);
    }
        catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error adding item to cart: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

}}
