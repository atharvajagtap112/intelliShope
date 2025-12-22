package com.atharva.ecommerce.Controller;

import com.atharva.ecommerce.Config.JwtProvider;
import com.atharva.ecommerce.Exception.UserException;
import com.atharva.ecommerce.Model.Cart;
import com.atharva.ecommerce.Model.User;
import com.atharva.ecommerce.Repository.UserRepository;
import com.atharva.ecommerce.Request.LoginRequest;
import com.atharva.ecommerce.Response.AuthResponse;
import com.atharva.ecommerce.Service.CartService;
import com.atharva.ecommerce.Service.CustomUserServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserRepository userRepository;
    private JwtProvider jwtProvider;
    private PasswordEncoder passwordEncoder;
    private CustomUserServiceImplementation customUserServiceImplementation;
    private CartService cartService;

    public AuthController(UserRepository userRepository,
                          JwtProvider jwtProvider,
                          PasswordEncoder passwordEncoder,
                          CustomUserServiceImplementation customUserServiceImplementation , CartService cartService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.customUserServiceImplementation = customUserServiceImplementation;
        this.cartService = cartService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException{
        String email = user.getEmail();
        String password = user.getPassword();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();

       User isEmailExist= userRepository.findByEmail(email);
       if(isEmailExist != null){

         throw new UserException("Email Already Exists");

       }

       User createdUser = new User();
       createdUser.setEmail(email);
       createdUser.setPassword(passwordEncoder.encode(password));
       createdUser.setFirstName(firstName);
       createdUser.setLastName(lastName);



      User savedUser= userRepository.save(createdUser);

       cartService.createCart(savedUser);

      Authentication authentication=new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
      SecurityContextHolder.getContext().setAuthentication(authentication);

      String token=jwtProvider.generateToken(authentication);
      AuthResponse authResponse=new AuthResponse();
      authResponse.setJwt(token);
      authResponse.setMessage("SignUp Success");
      return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);

    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> loginUserHandler(@RequestBody LoginRequest loginRequest) throws UserException, BadCredentialsException {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication=authentication(email,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      String token=  jwtProvider.generateToken(authentication);

        User user = userRepository.findByEmail(email);
        if (user != null) {
            try {
                cartService.findUserCart(user.getId());
            } catch (Exception e) {
                // Cart doesn't exist, create one
                cartService.createCart(user);
            }
        }
        AuthResponse authResponse=new AuthResponse(token,"SignIn Success");
      return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.CREATED) ;
    }

    private Authentication authentication(String email, String password) {
     UserDetails userDetails = customUserServiceImplementation.loadUserByUsername(email);
     if(userDetails == null){
         throw new BadCredentialsException("Invalid Email or Password");
     }

   if(!passwordEncoder.matches(password, userDetails.getPassword())){
       throw new BadCredentialsException("Invalid Password");
   }

    return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
    }

}
