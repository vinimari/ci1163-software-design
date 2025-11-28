package com.seucantinho.api.feature.cliente.infrastructure.web;

import com.seucantinho.api.feature.cliente.application.dto.ClienteRequestDTO;
import com.seucantinho.api.feature.cliente.application.dto.ClienteResponseDTO;
import com.seucantinho.api.feature.cliente.application.port.in.IClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
public class ClienteController {

    private final IClienteService clienteService;

    @GetMapping
    @Operation(summary = "Listar todos os clientes")
    public ResponseEntity<List<ClienteResponseDTO>> findAll() {
        List<ClienteResponseDTO> clientes = clienteService.findAll();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    public ResponseEntity<ClienteResponseDTO> findById(@PathVariable Integer id) {
        ClienteResponseDTO cliente = clienteService.findById(id);
        return ResponseEntity.ok(cliente);
    }

    @PostMapping
    @Operation(summary = "Criar novo cliente")
    public ResponseEntity<ClienteResponseDTO> create(@Valid @RequestBody ClienteRequestDTO requestDTO) {
        ClienteResponseDTO cliente = clienteService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente existente")
    public ResponseEntity<ClienteResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody ClienteRequestDTO requestDTO) {
        ClienteResponseDTO cliente = clienteService.update(id, requestDTO);
        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir cliente")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativo")
    @Operation(summary = "Ativar ou desativar cliente")
    public ResponseEntity<ClienteResponseDTO> toggleAtivo(
            @PathVariable Integer id,
            @RequestBody java.util.Map<String, Boolean> body) {
        Boolean ativo = body.get("ativo");
        if (ativo == null) {
            return ResponseEntity.badRequest().build();
        }
        ClienteResponseDTO cliente = clienteService.toggleAtivo(id, ativo);
        return ResponseEntity.ok(cliente);
    }
}
