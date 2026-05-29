package com.aksenova.auth.service;

import com.aksenova.auth.dto.AuthResponse;
import com.aksenova.auth.dto.LoginRequest;
import com.aksenova.auth.dto.RegisterRequest;
import com.aksenova.auth.entity.Role;
import com.aksenova.auth.entity.User;
import com.aksenova.auth.repository.UserRepository;
import com.aksenova.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new RuntimeException("Login already taken");
        }
        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        return new AuthResponse(jwtUtil.generateToken(user.getLogin(), user.getRole()), user.getLogin(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return new AuthResponse(jwtUtil.generateToken(user.getLogin(), user.getRole()), user.getLogin(), user.getRole().name());
    }
}
