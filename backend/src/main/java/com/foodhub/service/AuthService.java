package com.foodhub.service;

import com.foodhub.dto.auth.AuthResponse;
import com.foodhub.dto.auth.LoginRequest;
import com.foodhub.dto.auth.RegisterRequest;
import com.foodhub.dto.user.UserResponse;
import com.foodhub.entity.DeliveryPartner;
import com.foodhub.entity.Role;
import com.foodhub.entity.User;
import com.foodhub.enums.RoleName;
import com.foodhub.exception.InvalidOrderException;
import com.foodhub.mapper.UserMapper;
import com.foodhub.repository.DeliveryPartnerRepository;
import com.foodhub.repository.RoleRepository;
import com.foodhub.repository.UserRepository;
import com.foodhub.security.CustomUserDetailsService;
import com.foodhub.security.JwtService;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final CurrentUserService currentUserService;

    @Transactional
    public AuthResponse register(RegisterRequest request, RoleName forcedRole) {
        RoleName roleName = forcedRole != null ? forcedRole : request.role();
        if (roleName == null) {
            roleName = RoleName.CUSTOMER;
        }
        if (roleName == RoleName.ADMIN) {
            throw new InvalidOrderException("Admin users must be created through secure seed configuration");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new InvalidOrderException("Email is already registered");
        }
        if (userRepository.existsByPhone(request.phone())) {
            throw new InvalidOrderException("Phone number is already registered");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new InvalidOrderException("Role is not configured"));

        User user = User.builder()
                .name(request.name())
                .email(request.email().toLowerCase())
                .phone(request.phone())
                .passwordHash(passwordEncoder.encode(request.password()))
                .roles(new HashSet<>(Set.of(role)))
                .active(true)
                .build();
        User saved = userRepository.save(user);

        if (roleName == RoleName.DELIVERY_PARTNER) {
            DeliveryPartner partner = DeliveryPartner.builder()
                    .user(saved)
                    .vehicleType(request.vehicleType())
                    .vehicleNumber(request.vehicleNumber())
                    .available(true)
                    .totalEarnings(BigDecimal.ZERO)
                    .build();
            deliveryPartnerRepository.save(partner);
        }

        return tokenResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidOrderException("Invalid email or password"));
        return tokenResponse(user);
    }

    public UserResponse currentUser() {
        return UserMapper.toResponse(currentUserService.getCurrentUser());
    }

    private AuthResponse tokenResponse(User user) {
        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse("Bearer", token, jwtService.expirationMs(), UserMapper.toResponse(user));
    }
}

