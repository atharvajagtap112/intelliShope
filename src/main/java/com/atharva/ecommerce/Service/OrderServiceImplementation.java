package com.atharva.ecommerce.Service;

import com.atharva.ecommerce.Exception.OrderException;
import com.atharva.ecommerce.Model.*;
import com.atharva.ecommerce.Repository.*;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImplementation implements OrderService {
     private OrderRepository orderRepository;
     private CartRepository cartRepository;
     private CartService cartService;
     private ProductService productService;
     private AddressRepository addressRepository;
     private OrderItemService orderItemService;
     private OrderItemRepository orderItemRepository;
     private UserRepository userRepository;

    public OrderServiceImplementation(OrderRepository orderRepository, CartRepository cartRepository,
                                      CartService cartService, ProductService productService,
                                      AddressRepository addressRepository,
                                      OrderItemService orderItemService,
                                      OrderItemRepository orderItemRepository,
                                      UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.productService = productService;
        this.addressRepository = addressRepository;
        this.orderItemService = orderItemService;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Order createOrder(User user, Address shippAddress) {
        shippAddress.setUser(user);
        Address address = addressRepository.save(shippAddress);
        user.getAddress().add(address);
        userRepository.save(user);

        Cart cart = cartService.findUserCart(user.getId());
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem item : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setPrice(item.getPrice());
            orderItem.setDiscountedPrice(item.getDiscountedPrice());
            orderItem.setProduct(item.getProduct());
            orderItem.setSize(item.getSize());
            orderItem.setUserId(user.getId());
            orderItem.setQuantity(item.getQuantity());

            orderItems.add(orderItem);

        }

        Order createdOrder = new Order();
        createdOrder.setUser(user);
        createdOrder.setOrderItems(orderItems);
        createdOrder.setDiscount(cart.getTotalDiscountPrice());
        createdOrder.setTotalItem(cart.getTotalItem());
        createdOrder.setTotalPrice(cart.getTotalPrice());
        createdOrder.setTotalDiscountedPrice(cart.getTotalDiscountPrice());
        createdOrder.setShippingAddress(address);
        createdOrder.setOrderDate(LocalDateTime.now());
        createdOrder.setOrderStatus("PENDING");
        createdOrder.getPaymentDetails().setStatus("PENDING");
        createdOrder.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(createdOrder);

        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }

        return savedOrder;
    }

    @Override
    public Order findOrderById(Long orderId) throws OrderException {
      Optional<Order> order= orderRepository.findById(orderId);

      if (order.isPresent()) {
          return order.get();
      }
      throw new OrderException("Order not exists with id"+orderId);

    }

    @Override
    public List<Order> usersOrderHistory(Long userId) {
      List<Order> orders= orderRepository.getUserOrders(userId);
      return orders;
    }

    @Override
    public Order placedOrder(Long orderId) throws OrderException {
    Order order= orderRepository.findById(orderId).orElseThrow();
     order.setOrderStatus("PLACED");
     order.getPaymentDetails().setStatus("COMPLETED");

     return orderRepository.save(order);
    }

    @Override
    public Order confirmedOrder(Long orderId) throws OrderException {
        Order order= orderRepository.findById(orderId).orElseThrow();
        order.setOrderStatus("CONFIRMED");
        return orderRepository.save(order);
    }

    @Override
    public Order shippedOrder(Long orderId) throws OrderException {
        Order order= findOrderById(orderId);
        order.setOrderStatus("SHIPPED");


        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
       return orderRepository.findAll();
    }

    @Override
    public void deleteOrder(Long orderId) throws OrderException {
     Order order= findOrderById(orderId);
      orderRepository.deleteById(order.getId());
    }

    @Override
    public Order deliveredOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus("DELIVERED"); //
        return orderRepository.save(order);
    }

    @Override
    public Order cancledOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus("CANCELLED");
        return orderRepository.save(order);
    }

}
