package com.example.coffeeshop.services;

import com.example.coffeeshop.dtos.UpdateUserDTO;
import com.example.coffeeshop.dtos.UserDTO;
import com.example.coffeeshop.entitys.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService implements IUserService{
    @Override
    public User createUser(UserDTO userDTO) throws Exception {
        return null;
    }

    @Override
    public String login(String phoneNumber, String password, Long roleId) throws Exception {
        return "";
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        return null;
    }

    @Override
    public User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws Exception {
        return null;
    }
}
