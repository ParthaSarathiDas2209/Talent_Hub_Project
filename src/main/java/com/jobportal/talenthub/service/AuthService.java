package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.AuthResponseDto;
import com.jobportal.talenthub.dto.LoginRequestDto;
import com.jobportal.talenthub.dto.UserRequestDto;
import com.jobportal.talenthub.dto.UserResponseDto;

public interface AuthService {

    UserResponseDto register(UserRequestDto userRequestDto);

    AuthResponseDto login(LoginRequestDto loginRequestDto);
}
