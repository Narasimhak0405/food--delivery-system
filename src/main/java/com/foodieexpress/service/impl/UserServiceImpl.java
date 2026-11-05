package com.foodieexpress.service.impl;

import com.foodieexpress.dto.UserLoginDTO;
import com.foodieexpress.dto.UserRegisterDTO;
import com.foodieexpress.dto.UserResponseDTO;
import com.foodieexpress.exception.BadRequestException;
import com.foodieexpress.exception.ResourceNotFoundException;
import com.foodieexpress.model.Cart;
import com.foodieexpress.model.User;
import com.foodieexpress.repository.CartRepository;
import com.foodieexpress.repository.UserRepository;
import com.foodieexpress.security.CustomUserDetails;
import com.foodieexpress.security.JwtUtils;
import com.foodieexpress.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    @Transactional
    public UserResponseDTO register(UserRegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("An account with email " + dto.getEmail() + " already exists!");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        // 🔒 Secure BCrypt password hashing
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole() != null ? dto.getRole() : User.Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        // Initialize empty shopping cart for customer
        Cart cart = new Cart(savedUser);
        cartRepository.save(cart);

        // Authenticate new user automatically and generate token
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String token = jwtUtils.generateToken(authentication);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole(),
                token,
                "User registered successfully! Welcome to FoodieExpress."
        );
    }

    @Override
    public UserResponseDTO login(UserLoginDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtils.generateToken(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            User user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + dto.getEmail()));

            return new UserResponseDTO(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole(),
                    token,
                    "Login successful!"
            );
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid credentials: incorrect email or password.");
        }
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
