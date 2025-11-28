package com.seucantinho.api.feature.reserva.infrastructure.web;

import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.reserva.application.dto.ReservaRequestDTO;
import com.seucantinho.api.feature.reserva.application.dto.ReservaResponseDTO;
import com.seucantinho.api.feature.reserva.application.port.in.IReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Gerenciamento de reservas de espaços")
public class ReservaController {

    private final IReservaService reservaService;

    @GetMapping
    @Operation(summary = "Listar todas as reservas")
    public ResponseEntity<List<ReservaResponseDTO>> findAll() {
        List<ReservaResponseDTO> reservas = reservaService.findAll();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/acesso/{usuarioEmail}")
    @Operation(summary = "Listar reservas respeitando permissão do usuário autenticado")
    public ResponseEntity<List<ReservaResponseDTO>> findByAcesso(@PathVariable String usuarioEmail) {
        List<ReservaResponseDTO> reservas = reservaService.findByAcessoPorEmail(usuarioEmail);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reserva por ID")
    public ResponseEntity<ReservaResponseDTO> findById(@PathVariable Integer id) {
        ReservaResponseDTO reserva = reservaService.findById(id);
        return ResponseEntity.ok(reserva);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar reservas por usuário")
    public ResponseEntity<List<ReservaResponseDTO>> findByUsuarioId(@PathVariable Integer usuarioId) {
        List<ReservaResponseDTO> reservas = reservaService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(reservas);
    }



    @GetMapping("/espaco/{espacoId}")
    @Operation(summary = "Listar reservas por espaço")
    public ResponseEntity<List<ReservaResponseDTO>> findByEspacoId(@PathVariable Integer espacoId) {
        List<ReservaResponseDTO> reservas = reservaService.findByEspacoId(espacoId);
        return ResponseEntity.ok(reservas);
    }

    @PostMapping
    @Operation(summary = "Criar nova reserva")
    public ResponseEntity<ReservaResponseDTO> create(@Valid @RequestBody ReservaRequestDTO requestDTO) {
        ReservaResponseDTO reserva = reservaService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar reserva existente")
    public ResponseEntity<ReservaResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody ReservaRequestDTO requestDTO) {
        ReservaResponseDTO reserva = reservaService.update(id, requestDTO);
        return ResponseEntity.ok(reserva);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status da reserva")
    public ResponseEntity<ReservaResponseDTO> updateStatus(
            @PathVariable Integer id,
            @RequestParam StatusReservaEnum status) {
        ReservaResponseDTO reserva = reservaService.updateStatus(id, status);
        return ResponseEntity.ok(reserva);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir reserva")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        reservaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
