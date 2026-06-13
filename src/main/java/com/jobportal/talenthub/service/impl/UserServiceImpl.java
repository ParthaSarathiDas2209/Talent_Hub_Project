package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.dto.UserRequestDto;
import com.jobportal.talenthub.dto.UserResponseDto;
import com.jobportal.talenthub.entity.Role;
import com.jobportal.talenthub.entity.User;
import com.jobportal.talenthub.exception.ResourceNotFoundException;
import com.jobportal.talenthub.mapper.UserMapper;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = UserMapper.toEntity(userRequestDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toResponseDto(savedUser);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User Not Found with id : " + id));

        user.setFirstName(userRequestDto.firstName());
        user.setLastName(userRequestDto.lastName());
        user.setEmail(userRequestDto.email());
        user.setPassword(userRequestDto.password());
        user.setRole(userRequestDto.role());

        User updatedUser = userRepository.save(user);

        return UserMapper.toResponseDto(updatedUser);
    }

    @Override
    public UserResponseDto patchUser(Long id, Map<String, Object> updates) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User Not Found with id : " + id)
                );

        if (updates.containsKey("firstName")) {
            user.setFirstName(updates.get("firstName").toString());
        }

        if (updates.containsKey("lastName")) {
            user.setLastName(updates.get("lastName").toString());
        }

        if (updates.containsKey("email")) {
            user.setEmail(updates.get("email").toString());
        }

        if (updates.containsKey("password")) {
            user.setPassword(updates.get("password").toString());
        }

        if (updates.containsKey("role")) {
            user.setRole(Role.valueOf((String) updates.get("role")));
        }

        User updatedUser = userRepository.save(user);
        return UserMapper.toResponseDto(updatedUser);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException
                                ("User Not Found with id : " + id));

        return UserMapper.toResponseDto(user);
    }

//    @Override
//    public void deleteUser(Long id) {
//
//        User user = userRepository.findById(id)
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("User Not Found with id: " + id));
//
//        userRepository.delete(user);
//    }

    @Override
    public void deleteUser(Long id) {

        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("User Not Found with id : " + id);
        }
    }

}
