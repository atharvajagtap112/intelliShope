package com.atharva.ecommerce.Controller;

import com.atharva.ecommerce.Exception.OrderException;
import com.atharva.ecommerce.Model.Order;
import com.atharva.ecommerce.Repository.OrderRepository;
import com.atharva.ecommerce.Response.ApiResponse;
import com.atharva.ecommerce.Response.PaymentLinkResponse;
import com.atharva.ecommerce.Service.EmailService;
import com.atharva.ecommerce.Service.OrderService;
import com.atharva.ecommerce.Service.UserService;
import com.razorpay.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.AccessType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {
 @Value("${razorpay.api.key}")
 private String apiKey;

 @Value("${razorpay.api.secret}")
 private String apiSecret;

 @Autowired
 private OrderService orderService;

 @Autowired
 private UserService userService;

 @Autowired
 private OrderRepository orderRepository;


 @Autowired
 private EmailService emailService;
    private String webhookSecret="rzp_webhook_test_123";


    @PostMapping("/payments/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature
    ) throws Exception {

        // 🔐 VERIFY SIGNATURE
        boolean isValid = Utils.verifyWebhookSignature(
                payload,
                signature,
                webhookSecret
        );

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid webhook signature");
        }

        JSONObject json = new JSONObject(payload);
        String event = json.getString("event");

        if ("payment.captured".equals(event)) {

            JSONObject paymentEntity =
                    json.getJSONObject("payload")
                            .getJSONObject("payment")
                            .getJSONObject("entity");

            String paymentId = paymentEntity.getString("id");
            String orderId = paymentEntity
                    .getJSONObject("notes")
                    .getString("orderId");

            Order order = orderService.findOrderById(Long.valueOf(orderId));

            order.getPaymentDetails().setPaymentId(paymentId);
            order.getPaymentDetails().setStatus("COMPLETED");
            order.setOrderStatus("ORDER_CONFIRMED");

            orderRepository.save(order);
            emailService.sendOrderConfirmationEmail(order);
        }

        return ResponseEntity.ok("OK");
    }


    @PostMapping("/payments/{orderId}")
   public ResponseEntity<PaymentLinkResponse> createPaymentLink(@PathVariable Long orderId,
                                                                @RequestHeader("Authorization") String jwt ) throws OrderException, RazorpayException {
      Order order =orderService.findOrderById(orderId);
      try{
        RazorpayClient razorpayClient =new RazorpayClient(apiKey,apiSecret);

          JSONObject paymentLinkRequest= new JSONObject();

          paymentLinkRequest.put("amount",order.getTotalDiscountedPrice()*100);
          paymentLinkRequest.put("currency","INR");

          JSONObject customer =new JSONObject();
          customer.put("name",order.getUser().getFirstName());
          customer.put("email",order.getUser().getEmail());

          paymentLinkRequest.put("customer",customer);

          JSONObject notify =new JSONObject();
          notify.put("sms",true);
          notify.put("email",true);

          paymentLinkRequest.put("notify",notify);
          JSONObject notes = new JSONObject();
          notes.put("orderId", order.getId());

          paymentLinkRequest.put("notes", notes);
         paymentLinkRequest.put("callback_url","https://intellishopy.vercel.app/payment/"+orderId);
  //        paymentLinkRequest.put("callback_url","http://localhost:3000/payment/"+orderId);
           paymentLinkRequest.put("callback_method","get");

          PaymentLink payment= razorpayClient.paymentLink.create(paymentLinkRequest);

          String paymentLinkId=payment.get("id");
          String paymentLinkUrl=payment.get("short_url");

          PaymentLinkResponse res=new PaymentLinkResponse();
          res.setPayment_link_Id(paymentLinkId);
          res.setPayment_link_url(paymentLinkUrl);

          return new ResponseEntity<PaymentLinkResponse>(res, HttpStatus.CREATED);
      }
      catch (Exception e){
         throw new RazorpayException(e.getMessage());
      }
  }

  @GetMapping("/payments")
  public ResponseEntity<ApiResponse> redirect(@RequestParam(name="payment_id") String paymentId,
                                              @RequestParam(name="order_id") Long orderId ) throws RazorpayException, OrderException {

       Order order=orderService.findOrderById(orderId);
       RazorpayClient razorpay =new RazorpayClient(apiKey,apiSecret);


       try{

          Payment payment= razorpay.payments.fetch(paymentId);

           System.out.println(payment.toString());

           String status = (String) payment.get("status");

           if ("captured".equals(status)){
              order.getPaymentDetails().setPaymentId(paymentId);
              order.getPaymentDetails().setStatus("COMPLETED");
              order.setOrderStatus("ORDER_CONFIRMED");

              emailService.sendOrderConfirmationEmail(order);
              orderRepository.save(order);
          }
          return new ResponseEntity<>(new ApiResponse("your order get placed", true), HttpStatus.OK);
       }
       catch (Exception e){
    throw new RazorpayException(e.getMessage());
       }

  }


}
