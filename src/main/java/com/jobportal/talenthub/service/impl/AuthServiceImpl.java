package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.dto.AuthResponseDto;
import com.jobportal.talenthub.dto.LoginRequestDto;
import com.jobportal.talenthub.dto.UserRequestDto;
import com.jobportal.talenthub.dto.UserResponseDto;
import com.jobportal.talenthub.entity.User;
import com.jobportal.talenthub.exception.InvalidCredentialsException;
import com.jobportal.talenthub.mapper.UserMapper;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.AuthService;
import com.jobportal.talenthub.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserResponseDto register(UserRequestDto userRequestDto) {

        if (userRepository.existsByEmail(userRequestDto.email())) {
            throw new RuntimeException("Email already exists");
        }

        User user = UserMapper.toEntity(userRequestDto);
        user.setPassword(passwordEncoder.encode(userRequestDto.password()));

        User savedUser = userRepository.save(user);

        return UserMapper.toResponseDto(savedUser);
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByEmail(loginRequestDto.email())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid e-mail or password")
                );

        boolean isPasswordValid = passwordEncoder.matches(
                loginRequestDto.password(),
                user.getPassword()
        );

        if (!isPasswordValid) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponseDto(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}
