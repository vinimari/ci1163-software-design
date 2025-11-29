package com.seucantinho.api.feature.cliente.infrastructure.adapter.in.web;

import com.seucantinho.api.feature.cliente.application.dto.ClienteRequestDTO;
import com.seucantinho.api.feature.cliente.application.dto.ClienteResponseDTO;
import com.seucantinho.api.feature.cliente.domain.port.in.ClienteServicePort;
import com.seucantinho.api.feature.cliente.domain.port.in.ClienteWebPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes do sistema")
@SecurityRequirement(name = "bearer-jwt")
public class ClienteWebAdapter implements ClienteWebPort {

    private final ClienteServicePort clienteService;

    @Override
    @GetMapping
    @Operation(
        summary = "Listar clientes",
        description = "Retorna a lista de todos os clientes cadastrados no sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content)
    })
    public ResponseEntity<List<ClienteResponseDTO>> findAll() {
        List<ClienteResponseDTO> clientes = clienteService.findAll();
        return ResponseEntity.ok(clientes);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar cliente por ID",
        description = "Retorna os dados completos de um cliente específico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    public ResponseEntity<ClienteResponseDTO> findById(
            @Parameter(description = "ID do cliente", required = true)
            @PathVariable Integer id) {
        ClienteResponseDTO cliente = clienteService.findById(id);
        return ResponseEntity.ok(cliente);
    }

    @Override
    @PostMapping
    @Operation(
        summary = "Cadastrar novo cliente",
        description = "Cria um novo cliente no sistema. Pode ser usado para auto-cadastro (endpoint público) ou criação por administrador. " +
                     "O email deve ser único em todo o sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Cliente cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "409", description = "Email já cadastrado", content = @Content)
    })
    public ResponseEntity<ClienteResponseDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados do cliente a ser cadastrado",
                required = true,
                content = @Content(schema = @Schema(implementation = ClienteRequestDTO.class))
            )
            @Valid @RequestBody ClienteRequestDTO requestDTO) {
        ClienteResponseDTO cliente = clienteService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    @Override
    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar cliente",
        description = "Atualiza os dados de um cliente existente. A senha é opcional na atualização. " +
                     "O email deve permanecer único no sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cliente atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar este cliente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Email já cadastrado para outro usuário", content = @Content)
    })
    public ResponseEntity<ClienteResponseDTO> update(
            @Parameter(description = "ID do cliente a ser atualizado", required = true)
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Novos dados do cliente",
                required = true,
                content = @Content(schema = @Schema(implementation = ClienteRequestDTO.class))
            )
            @Valid @RequestBody ClienteRequestDTO requestDTO) {
        ClienteResponseDTO cliente = clienteService.update(id, requestDTO);
        return ResponseEntity.ok(cliente);
    }

    @Override
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir cliente",
        description = "Remove permanentemente um cliente do sistema. Esta operação não pode ser desfeita."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente excluído com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para excluir clientes", content = @Content),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do cliente a ser excluído", required = true)
            @PathVariable Integer id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{id}/ativo")
    @Operation(
        summary = "Ativar ou desativar cliente",
        description = "Altera o status de ativo/inativo do cliente. Clientes inativos não podem acessar o sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Status do cliente atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para alterar status de clientes", content = @Content),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    public ResponseEntity<ClienteResponseDTO> toggleAtivo(
            @Parameter(description = "ID do cliente", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Novo status (true para ativar, false para desativar)", required = true)
            @RequestParam Boolean ativo) {
        ClienteResponseDTO cliente = clienteService.toggleAtivo(id, ativo);
        return ResponseEntity.ok(cliente);
    }
}