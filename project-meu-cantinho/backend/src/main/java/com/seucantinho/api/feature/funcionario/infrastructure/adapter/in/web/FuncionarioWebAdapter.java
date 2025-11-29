package com.seucantinho.api.feature.funcionario.infrastructure.adapter.in.web;

import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioRequestDTO;
import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioResponseDTO;
import com.seucantinho.api.feature.funcionario.domain.port.in.FuncionarioServicePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funcionarios")
@RequiredArgsConstructor
public class FuncionarioWebAdapter {

    private final FuncionarioServicePort funcionarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public ResponseEntity<List<FuncionarioResponseDTO>> findAll(
            @RequestParam(required = false) Integer filialId) {
        List<FuncionarioResponseDTO> funcionarios = filialId != null
                ? funcionarioService.findByFilialId(filialId)
                : funcionarioService.findAll();
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public ResponseEntity<FuncionarioResponseDTO> findById(@PathVariable Integer id) {
        FuncionarioResponseDTO funcionario = funcionarioService.findById(id);
        return ResponseEntity.ok(funcionario);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> create(
            @Valid @RequestBody FuncionarioRequestDTO requestDTO) {
        FuncionarioResponseDTO funcionario = funcionarioService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionario);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody FuncionarioRequestDTO requestDTO) {
        FuncionarioResponseDTO funcionario = funcionarioService.update(id, requestDTO);
        return ResponseEntity.ok(funcionario);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        funcionarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> toggleAtivo(
            @PathVariable Integer id,
            @RequestParam Boolean ativo) {
        FuncionarioResponseDTO funcionario = funcionarioService.toggleAtivo(id, ativo);
        return ResponseEntity.ok(funcionario);
    }

    @PatchMapping("/{id}/filial")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> trocarFilial(
            @PathVariable Integer id,
            @RequestParam Integer filialId) {
        FuncionarioResponseDTO funcionario = funcionarioService.trocarFilial(id, filialId);
        return ResponseEntity.ok(funcionario);
    }
}
