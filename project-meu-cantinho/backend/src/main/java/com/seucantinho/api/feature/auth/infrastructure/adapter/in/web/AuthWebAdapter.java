package com.seucantinho.api.feature.auth.infrastructure.adapter.in.web;

import com.seucantinho.api.feature.auth.application.dto.LoginRequest;
import com.seucantinho.api.feature.auth.application.dto.LoginResponse;
import com.seucantinho.api.feature.auth.domain.port.in.AuthServicePort;
import com.seucantinho.api.feature.auth.domain.port.in.AuthWebPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação e autorização de usuários no sistema")
public class AuthWebAdapter implements AuthWebPort {

    private final AuthServicePort authService;

    @Override
    @PostMapping("/login")
    @Operation(
        summary = "Autenticar usuário",
        description = "Realiza autenticação de usuário (Cliente, Funcionário ou Administrador) e retorna um token JWT para acesso aos recursos protegidos."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso. Retorna token JWT e informações do usuário",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou malformados",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciais inválidas ou usuário inativo",
            content = @Content
        )
    })
    public ResponseEntity<LoginResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Credenciais de acesso (email e senha)",
                required = true,
                content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}