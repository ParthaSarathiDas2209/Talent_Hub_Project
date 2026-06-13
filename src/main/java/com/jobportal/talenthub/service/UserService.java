package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.UserRequestDto;
import com.jobportal.talenthub.dto.UserResponseDto;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);

    UserResponseDto updateUser(Long id, UserRequestDto userRequestDto);

    UserResponseDto patchUser(Long id, Map<String, Object> updates);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long id);

    void deleteUser(Long id);

}
