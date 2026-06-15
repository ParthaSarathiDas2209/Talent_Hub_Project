package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.UserPatchDto;
import com.jobportal.talenthub.dto.UserRequestDto;
import com.jobportal.talenthub.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserRequestDto userRequestDto);

    UserResponseDto updateUser(Long id, UserRequestDto userRequestDto);

    UserResponseDto patchUser(Long id, UserPatchDto userPatchDto);

//    UserResponseDto patchUser(Long id, Map<String, Object> updates);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long id);

    void deleteUser(Long id);

}
