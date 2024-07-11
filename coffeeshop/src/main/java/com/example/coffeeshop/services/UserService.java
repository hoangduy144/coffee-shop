package com.example.coffeeshop.services;

import com.example.coffeeshop.components.JwtTokenUtils;
import com.example.coffeeshop.dtos.UpdateUserDTO;
import com.example.coffeeshop.dtos.UserDTO;
import com.example.coffeeshop.entitys.Role;
import com.example.coffeeshop.entitys.User;
import com.example.coffeeshop.repositories.RoleRepository;
import com.example.coffeeshop.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public User createUser(UserDTO userDTO) throws Exception {
        //register user
        //check phone number exists
        String phoneNumber = userDTO.getPhoneNumber();

        if(userRepository.existsByPhoneNumber(phoneNumber)){
           throw new Exception("Phone number already exists");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(()-> new Exception("Role not found"));
        if(role.getName().toUpperCase().equals(Role.ADMIN)){
            throw new Exception("You cannot create an admin account");
        }

        //Convert from userDTO => user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .active(true)
                .build();

        newUser.setRole(role);

        String password = userDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        newUser.setPassword(encodedPassword);

        //save user

        return userRepository.save(newUser);

    }

    @Override
    public String login(String phoneNumber, String password, Long roleId) throws Exception {

        //find user with phone_number
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);

        //check user in db
        if(optionalUser.isEmpty()){
            throw new Exception("User not found. Wrong password or phone number");
        }

        //lay user bang phone_number
        User existsUser = optionalUser.get();

        //check password login match password of user in db
        if(!passwordEncoder.matches(password, existsUser.getPassword())){
            throw new Exception("User not found. Wrong password or phone number");
        }

        //find role_id of user
        Optional<Role> optionalRole = roleRepository.findById(roleId);

        //check role user
        if(optionalRole.isEmpty() || !roleId.equals(existsUser.getRole().getId())){
            throw new Exception("Role does not exists");
        }
        //check user is active
        if(!optionalUser.get().isActive()){
            throw new Exception("User is not active");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber,
                password,
                existsUser.getAuthorities()
        );

        //authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);

        //create & return jwtToken
        return jwtTokenUtil.generateToken(existsUser);

    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {

        //check for expired token
        if(jwtTokenUtil.isTokenExpired(token)){
            throw new Exception("Token expired");
        }

        //extract phone_number from token
        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);

        //find user by phone_number
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        //check & return info user
        if(user.isPresent()){
            return user.get();
        }else {
            throw new Exception("User not found");
        }

    }

    @Override
    public User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws Exception {
        return null;
    }
}
