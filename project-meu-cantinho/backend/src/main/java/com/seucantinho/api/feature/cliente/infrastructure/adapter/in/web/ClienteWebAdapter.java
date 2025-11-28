package com.seucantinho.api.feature.cliente.infrastructure.adapter.in.web;

import com.seucantinho.api.feature.cliente.application.dto.ClienteRequestDTO;
import com.seucantinho.api.feature.cliente.application.dto.ClienteResponseDTO;
import com.seucantinho.api.feature.cliente.domain.port.in.ClienteServicePort;
import com.seucantinho.api.feature.cliente.domain.port.in.ClienteWebPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Cliente", description = "API para gerenciamento de clientes")
public class ClienteWebAdapter implements ClienteWebPort {

    private final ClienteServicePort clienteService;

    @Override
    @GetMapping
    @Operation(summary = "Listar todos os clientes")
    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    public ResponseEntity<List<ClienteResponseDTO>> findAll() {
        List<ClienteResponseDTO> clientes = clienteService.findAll();
        return ResponseEntity.ok(clientes);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ClienteResponseDTO> findById(@PathVariable Integer id) {
        ClienteResponseDTO cliente = clienteService.findById(id);
        return ResponseEntity.ok(cliente);
    }

    @Override
    @PostMapping
    @Operation(summary = "Criar novo cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ClienteResponseDTO> create(@Valid @RequestBody ClienteRequestDTO requestDTO) {
        ClienteResponseDTO cliente = clienteService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    @Override
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ClienteResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody ClienteRequestDTO requestDTO) {
        ClienteResponseDTO cliente = clienteService.update(id, requestDTO);
        return ResponseEntity.ok(cliente);
    }

    @Override
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{id}/ativo")
    @Operation(summary = "Ativar/desativar cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status do cliente atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ClienteResponseDTO> toggleAtivo(@PathVariable Integer id, @RequestParam Boolean ativo) {
        ClienteResponseDTO cliente = clienteService.toggleAtivo(id, ativo);
        return ResponseEntity.ok(cliente);
    }
}