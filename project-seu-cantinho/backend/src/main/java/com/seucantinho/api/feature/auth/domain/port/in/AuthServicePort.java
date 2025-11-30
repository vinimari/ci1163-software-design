package com.seucantinho.api.feature.auth.domain.port.in;

import com.seucantinho.api.feature.auth.application.dto.LoginRequest;
import com.seucantinho.api.feature.auth.application.dto.LoginResponse;

public interface AuthServicePort {

    LoginResponse login(LoginRequest request);
}