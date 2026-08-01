package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.dto.UserPatchDto;
import com.jobportal.talenthub.dto.UserRequestDto;
import com.jobportal.talenthub.dto.UserResponseDto;
import com.jobportal.talenthub.entity.User;
import com.jobportal.talenthub.entity.UserStatus;
import com.jobportal.talenthub.exception.JobApplicationException;
import com.jobportal.talenthub.exception.ResourceNotFoundException;
import com.jobportal.talenthub.mapper.UserMapper;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        if (userRepository.existsByEmail(userRequestDto.email())) {
            throw new JobApplicationException("Email already exists");
        }

        User user = UserMapper.toEntity(userRequestDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toResponseDto(savedUser);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {

        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User Not Found with id : " + id
                        )
                );

        if (userRepository.existsByEmail(userRequestDto.email())
                && !user.getEmail().equals(userRequestDto.email())) {

            throw new JobApplicationException(
                    "Email Already Exists."
            );
        }

        user.setFirstName(userRequestDto.firstName());
        user.setLastName(userRequestDto.lastName());
        user.setEmail(userRequestDto.email());
        user.setPassword(
                passwordEncoder.encode(
                        userRequestDto.password()));
        user.setRole(userRequestDto.role());

        User updatedUser = userRepository.save(user);

        return UserMapper.toResponseDto(updatedUser);
    }

    @Override
    public UserResponseDto patchUser(Long id, UserPatchDto userPatchDto) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User Not Found with id : " + id));

        if (userPatchDto.firstName() != null) {
            user.setFirstName(userPatchDto.firstName());
        }

        if (userPatchDto.lastName() != null) {
            user.setLastName(userPatchDto.lastName());
        }

        if (userPatchDto.email() != null
                && userRepository.existsByEmail(userPatchDto.email())
                && !user.getEmail().equals(userPatchDto.email())) {

            throw new JobApplicationException(
                    "Email Already Exists."
            );
        }

        if (userPatchDto.email() != null) {
            user.setEmail(userPatchDto.email());
        }

        if (userPatchDto.password() != null) {
            user.setPassword(
                    passwordEncoder.encode(
                            userPatchDto.password())
            );
        }

        if (userPatchDto.role() != null) {
            user.setRole(userPatchDto.role());
        }


        User updatedUser = userRepository.save(user);

        return UserMapper.toResponseDto(updatedUser);
    }


    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAllByDeletedFalse();
        return users.stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User Not Found with id : " + id
                        )
                );

        return UserMapper.toResponseDto(user);
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User Not Found with id: " + id
                        )
                );

        if (user.isDeleted()) {
            throw new JobApplicationException(
                    "User has already been deleted"
            );
        }

        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);
    }

}