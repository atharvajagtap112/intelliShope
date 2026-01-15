package com.atharva.ecommerce.Controller;

import com.atharva.ecommerce.Exception.OrderException;
import com.atharva.ecommerce.Exception.UserException;
import com.atharva.ecommerce.Model.Address;
import com.atharva.ecommerce.Model.Order;
import com.atharva.ecommerce.Model.User;
import com.atharva.ecommerce.Repository.OrderRepository;
import com.atharva.ecommerce.Response.ApiResponse;
import com.atharva.ecommerce.Service.OrderService;
import com.atharva.ecommerce.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public ResponseEntity<Order> createOrder(@RequestBody Address shppingAdress,
                                             @RequestHeader("Authorization") String jwt) throws UserException{

        User user=userService.findUserProfileByJwt(jwt);

       Order order= orderService.createOrder(user,shppingAdress);
        System.out.println("Order "+order);
       return new ResponseEntity<Order>(order, HttpStatus.CREATED);
    }

    @PostMapping("/user")
    public ResponseEntity<List<Order>> userOrdersHistory(@RequestHeader("Authorization") String jwt , @RequestBody List<String> status)  throws UserException{

        User user =userService.findUserProfileByJwt(jwt);
        System.out.println("User "+user.getEmail());
       List<Order> orders= orderService.usersOrderHistory(user.getId());
        System.out.println("Orders "+orders.size() + "status "+status.toString());

       if (!status.get(0).equals("ALL")){
           orders = orders.stream()
                   .filter(order -> status.contains(order.getOrderStatus()))
                   .collect(Collectors.toList());
       }

        System.out.println("Orders f"+orders.size());
       return new ResponseEntity<>(orders, HttpStatus.CREATED);
    }

    @GetMapping("/{Id}")
    public ResponseEntity<Order> findOrderById(@PathVariable("Id") Long id,
                                              @RequestHeader("Authorization") String jwt) throws UserException, OrderException {
        User user =userService.findUserProfileByJwt(jwt);
        Order order=orderService.findOrderById(id);
        return new  ResponseEntity<Order>(order,HttpStatus.CREATED);
    }

        @PostMapping("/updateStatus/{orderId}")
        public ResponseEntity<ApiResponse> updateStatus(@PathVariable(name = "orderId") Long orderId,@RequestParam String status ) throws UserException, OrderException {
          Order order=orderService.findOrderById(orderId);
          order.setOrderStatus(status);

          orderRepository.save(order);


          ApiResponse apiResponse=new ApiResponse("updated",true);
          return new ResponseEntity<ApiResponse>(apiResponse,HttpStatus.OK);
        }

}
