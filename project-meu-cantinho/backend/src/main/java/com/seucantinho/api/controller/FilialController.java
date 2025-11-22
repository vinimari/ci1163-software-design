package com.seucantinho.api.controller;

import com.seucantinho.api.dto.filial.FilialRequestDTO;
import com.seucantinho.api.dto.filial.FilialResponseDTO;
import com.seucantinho.api.service.interfaces.IFilialService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Filiais", description = "Gerenciamento de filiais da rede")
public class FilialController {

    private final IFilialService filialService;

    @GetMapping
    @Operation(summary = "Listar todas as filiais")
    public ResponseEntity<List<FilialResponseDTO>> findAll() {
        List<FilialResponseDTO> filiais = filialService.findAll();
        return ResponseEntity.ok(filiais);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar filial por ID")
    public ResponseEntity<FilialResponseDTO> findById(@PathVariable Integer id) {
        FilialResponseDTO filial = filialService.findById(id);
        return ResponseEntity.ok(filial);
    }

    @PostMapping
    @Operation(summary = "Criar nova filial")
    public ResponseEntity<FilialResponseDTO> create(@Valid @RequestBody FilialRequestDTO requestDTO) {
        FilialResponseDTO filial = filialService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(filial);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar filial existente")
    public ResponseEntity<FilialResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody FilialRequestDTO requestDTO) {
        FilialResponseDTO filial = filialService.update(id, requestDTO);
        return ResponseEntity.ok(filial);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir filial")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        filialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
