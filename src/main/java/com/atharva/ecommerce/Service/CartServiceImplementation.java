package com.atharva.ecommerce.Service;


import com.atharva.ecommerce.Exception.CartItemException;
import com.atharva.ecommerce.Exception.ProductException;
import com.atharva.ecommerce.Exception.UserException;
import com.atharva.ecommerce.Model.Cart;
import com.atharva.ecommerce.Model.CartItem;
import com.atharva.ecommerce.Model.Product;
import com.atharva.ecommerce.Model.User;
import com.atharva.ecommerce.Repository.CartRepository;
import com.atharva.ecommerce.Request.AddItemRequest;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImplementation implements CartService {
    private final UserService userService;
    private ProductService productService;
    private CartRepository cartRepository;
    private CartItemServiceImplementation cartItemService;

    public CartServiceImplementation(ProductService productService, CartRepository cartRepository, CartItemServiceImplementation cartItemService, UserService userService) {
        this.productService = productService;
        this.cartRepository = cartRepository;
        this.cartItemService = cartItemService;
        this.userService = userService;
    }

    @Override
    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    @Override
    public String addCartItem(Long userId, AddItemRequest req) throws ProductException, CartItemException, UserException {
        Cart cart=cartRepository.findByUserId(userId);
        if (cart == null) {
            User user = userService.findUserById(userId);
            cart = createCart(user);
        }

        Product product=productService.findProductById(req.getProductId());

        CartItem isPresent=cartItemService.isCartItemExist(cart,product, req.getSize(), userId);
        if (isPresent==null) {
            CartItem cartItem=new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(req.getQuantity());
            cartItem.setUserId(userId);
            cartItem.setCart(cart);

            int price= (int) (req.getQuantity()*product.getPrice());

            cartItem.setPrice(price);
            cartItem.setDiscountedPrice( (req.getQuantity()*product.getDiscountPrice()));
           cartItem.setSize(req.getSize());

            CartItem createdCartItem=cartItemService.createCartItem(cartItem);

            cart.getCartItems().add(createdCartItem);
            cartRepository.save(cart);

        }
       else {
         isPresent.setQuantity(isPresent.getQuantity()+req.getQuantity());
            int price= (int) (req.getQuantity()*product.getPrice());
            isPresent.setPrice(price);
            isPresent.setDiscountedPrice(req.getQuantity()*product.getDiscountPrice());


         CartItem   updatedCartItem  = cartItemService.updateCartItem(userId,isPresent.getId(),isPresent);
//            cart.getCartItems().removeIf(item -> item.getId().equals(updatedCartItem.getId()));
//            cart.getCartItems().add(updatedCartItem);
//            cartRepository.save(cart);
        }

        return "Item added to cart";
    }

    @Override
    public Cart findUserCart(Long userId) {
       Cart cart=cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new RuntimeException("Cart not found for user: " + userId);
        }
       int totalPrice=0;
       int totalDiscountPrice=0;
       int totalItem=0;

       for (CartItem cartItem:cart.getCartItems()){
           totalPrice+=cartItem.getPrice();
           totalDiscountPrice+=cartItem.getDiscountedPrice();
           totalItem+=cartItem.getQuantity();
       }

       cart.setTotalPrice(totalPrice);
       cart.setTotalDiscountPrice(totalDiscountPrice);
       cart.setTotalItem(totalItem);
       cart.setDiscount(totalPrice-totalDiscountPrice);
       return cartRepository.save(cart);
    }
}
