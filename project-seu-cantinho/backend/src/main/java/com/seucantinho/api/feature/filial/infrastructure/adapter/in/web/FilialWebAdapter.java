package com.seucantinho.api.feature.filial.infrastructure.adapter.in.web;

import com.seucantinho.api.feature.filial.application.dto.FilialRequestDTO;
import com.seucantinho.api.feature.filial.application.dto.FilialResponseDTO;
import com.seucantinho.api.feature.filial.domain.port.in.FilialServicePort;
import com.seucantinho.api.feature.filial.domain.port.in.FilialWebPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filiais")
@RequiredArgsConstructor
@Tag(name = "Filiais", description = "Endpoints para gerenciamento de filiais da empresa")
@SecurityRequirement(name = "bearer-jwt")
public class FilialWebAdapter implements FilialWebPort {

    private final FilialServicePort filialService;

    @GetMapping
    @Operation(
        summary = "Listar filiais",
        description = "Retorna a lista de todas as filiais cadastradas no sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de filiais retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content)
    })
    public ResponseEntity<List<FilialResponseDTO>> findAll() {
        List<FilialResponseDTO> filiais = filialService.findAll();
        return ResponseEntity.ok(filiais);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar filial por ID",
        description = "Retorna os dados completos de uma filial específica."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Filial encontrada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content),
        @ApiResponse(responseCode = "404", description = "Filial não encontrada", content = @Content)
    })
    public ResponseEntity<FilialResponseDTO> findById(
            @Parameter(description = "ID da filial", required = true)
            @PathVariable Integer id) {
        FilialResponseDTO filial = filialService.findById(id);
        return ResponseEntity.ok(filial);
    }

    @PostMapping
    @Operation(
        summary = "Cadastrar nova filial",
        description = "Cria uma nova filial no sistema. Apenas administradores podem cadastrar filiais."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Filial cadastrada com sucesso",
            content = @Content(schema = @Schema(implementation = FilialResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Apenas administradores podem cadastrar filiais", content = @Content)
    })
    public ResponseEntity<FilialResponseDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados da filial a ser cadastrada",
                required = true,
                content = @Content(schema = @Schema(implementation = FilialRequestDTO.class))
            )
            @Valid @RequestBody FilialRequestDTO requestDTO) {
        FilialResponseDTO filial = filialService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(filial);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar filial",
        description = "Atualiza os dados de uma filial existente. Apenas administradores podem atualizar filiais."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Filial atualizada com sucesso",
            content = @Content(schema = @Schema(implementation = FilialResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Apenas administradores podem atualizar filiais", content = @Content),
        @ApiResponse(responseCode = "404", description = "Filial não encontrada", content = @Content)
    })
    public ResponseEntity<FilialResponseDTO> update(
            @Parameter(description = "ID da filial a ser atualizada", required = true)
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Novos dados da filial",
                required = true,
                content = @Content(schema = @Schema(implementation = FilialRequestDTO.class))
            )
            @Valid @RequestBody FilialRequestDTO requestDTO) {
        FilialResponseDTO filial = filialService.update(id, requestDTO);
        return ResponseEntity.ok(filial);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir filial",
        description = "Remove permanentemente uma filial do sistema. Esta operação não pode ser desfeita. " +
                     "Filiais com funcionários ou espaços associados não podem ser excluídas."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Filial excluída com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Apenas administradores podem excluir filiais", content = @Content),
        @ApiResponse(responseCode = "404", description = "Filial não encontrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Filial possui dependências (funcionários ou espaços)", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da filial a ser excluída", required = true)
            @PathVariable Integer id) {
        filialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}