package com.seucantinho.api.feature.auth.domain.port.in;

import com.seucantinho.api.feature.auth.application.dto.LoginRequest;
import com.seucantinho.api.feature.auth.application.dto.LoginResponse;
import org.springframework.http.ResponseEntity;

public interface AuthWebPort {

    ResponseEntity<LoginResponse> login(LoginRequest request);
}