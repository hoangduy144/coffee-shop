package com.example.coffeeshop.controllers;

import com.example.coffeeshop.dtos.UserDTO;
import com.example.coffeeshop.dtos.UserLoginDTO;
import com.example.coffeeshop.entitys.User;
import com.example.coffeeshop.responses.LoginResponse;
import com.example.coffeeshop.responses.RegisterResponse;
import com.example.coffeeshop.responses.UserResponse;
import com.example.coffeeshop.services.IUserService;
import com.example.coffeeshop.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    @PostMapping("register")
    public ResponseEntity<RegisterResponse> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
    ){
        RegisterResponse registerResponse = new RegisterResponse();

        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            registerResponse.setMessage(errorMessages.toString());
            return ResponseEntity.badRequest().body(registerResponse);
        }

        //check password & retype_password match?
        if(!userDTO.getPassword().equals(userDTO.getRetypePassword())){
            registerResponse.setMessage("Passwords do not match");

            return ResponseEntity.badRequest().body(registerResponse);
        }

        //create user
        try{
            User user = userService.createUser(userDTO);
            registerResponse.setMessage("User registered successfully");
            registerResponse.setUser(user);
            return ResponseEntity.ok(registerResponse);

        }catch (Exception e){
            registerResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(registerResponse);
        }

    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO
    ){
        //check info login & create token
        try {
            String token = userService.login(
                    userLoginDTO.getPhoneNumber(),
                    userLoginDTO.getPassword(),
                    userLoginDTO.getRoleId()== null ? 1 : userLoginDTO.getRoleId()
            );

            return ResponseEntity.ok(LoginResponse.builder()
                            .message("Login successful")
                            .token(token)
                    .build());

        }catch (Exception e){
            return ResponseEntity.badRequest().body(LoginResponse.builder()
                            .message("Login failed")
                            .token(null)
                    .build());
        }
    }

    @PostMapping("details")
    public ResponseEntity<UserResponse> getUserDetailsFromToken(
            @RequestHeader("Authorization") String authorizationHeader
    ){
        try {
            // Loại bỏ "Bearer " từ chuỗi token
            String extractToken = authorizationHeader.substring(7);

            // Lấy thông tin người dùng từ token
            User user = userService.getUserDetailsFromToken(extractToken);

            // Trả về phản hồi HTTP 200 (OK) chứa thông tin người dùng
            return ResponseEntity.ok(UserResponse.fromUser(user));
        }catch (Exception e){
            // Trả về phản hồi HTTP 400 (Bad Request) nếu có lỗi
            return ResponseEntity.badRequest().build();
        }
    }




}
