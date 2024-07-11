package com.example.coffeeshop.services;

import com.example.coffeeshop.dtos.UpdateUserDTO;
import com.example.coffeeshop.dtos.UserDTO;
import com.example.coffeeshop.entitys.User;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password, Long roleId) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws Exception;
}
