package com.seucantinho.api.controller;

import com.seucantinho.api.dto.espaco.EspacoRequestDTO;
import com.seucantinho.api.dto.espaco.EspacoResponseDTO;
import com.seucantinho.api.service.interfaces.IEspacoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/espacos")
@RequiredArgsConstructor
@Tag(name = "Espaços", description = "Gerenciamento de espaços para eventos")
public class EspacoController {

    private final IEspacoService espacoService;

    @GetMapping
    @Operation(summary = "Listar todos os espaços")
    public ResponseEntity<List<EspacoResponseDTO>> findAll() {
        List<EspacoResponseDTO> espacos = espacoService.findAll();
        return ResponseEntity.ok(espacos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar espaço por ID")
    public ResponseEntity<EspacoResponseDTO> findById(@PathVariable Integer id) {
        EspacoResponseDTO espaco = espacoService.findById(id);
        return ResponseEntity.ok(espaco);
    }

    @GetMapping("/filial/{filialId}")
    @Operation(summary = "Listar espaços por filial")
    public ResponseEntity<List<EspacoResponseDTO>> findByFilialId(@PathVariable Integer filialId) {
        List<EspacoResponseDTO> espacos = espacoService.findByFilialId(filialId);
        return ResponseEntity.ok(espacos);
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar espaços ativos")
    public ResponseEntity<List<EspacoResponseDTO>> findAtivos() {
        List<EspacoResponseDTO> espacos = espacoService.findAtivos();
        return ResponseEntity.ok(espacos);
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Buscar espaços disponíveis para uma data")
    public ResponseEntity<List<EspacoResponseDTO>> findDisponiveis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(defaultValue = "1") Integer capacidadeMinima) {
        List<EspacoResponseDTO> espacos = espacoService.findDisponiveisPorData(data, capacidadeMinima);
        return ResponseEntity.ok(espacos);
    }

    @PostMapping
    @Operation(summary = "Criar novo espaço")
    public ResponseEntity<EspacoResponseDTO> create(@Valid @RequestBody EspacoRequestDTO requestDTO) {
        EspacoResponseDTO espaco = espacoService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(espaco);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar espaço existente")
    public ResponseEntity<EspacoResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody EspacoRequestDTO requestDTO) {
        EspacoResponseDTO espaco = espacoService.update(id, requestDTO);
        return ResponseEntity.ok(espaco);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir espaço")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        espacoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
