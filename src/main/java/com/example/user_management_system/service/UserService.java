package com.example.user_management_system.service;
import com.example.user_management_system.entity.User;
import com.example.user_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // Use BCrypt for password encoding

    // User registration with hashed password
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Validate user login
    public String validateUser(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user.getRole(); // Returns the user role if login is successful
        }
        return "invalid";
    }

    public List<User> getAllUsers() {
        return userRepository.findAllExcludingRole("ROLE_ADMIN");
    }

    public List<User> searchUsersByName(String keyword) {
        return userRepository.findByUsernameContainingIgnoreCase(keyword);
    }
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Ensure password is hashed
        userRepository.save(user);
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void updateUser(User updatedUser){
        User existingUser = userRepository.findById(updatedUser.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setRole(updatedUser.getRole());
        userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }







}
