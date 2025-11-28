package com.seucantinho.api.feature.pagamento.infrastructure.web;

import com.seucantinho.api.feature.pagamento.application.dto.PagamentoRequestDTO;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoResponseDTO;
import com.seucantinho.api.feature.pagamento.application.port.in.IPagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Gerenciamento de pagamentos de reservas")
public class PagamentoController {

    private final IPagamentoService pagamentoService;

    @GetMapping
    @Operation(summary = "Listar todos os pagamentos")
    public ResponseEntity<List<PagamentoResponseDTO>> findAll() {
        List<PagamentoResponseDTO> pagamentos = pagamentoService.findAll();
        return ResponseEntity.ok(pagamentos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pagamento por ID")
    public ResponseEntity<PagamentoResponseDTO> findById(@PathVariable Integer id) {
        PagamentoResponseDTO pagamento = pagamentoService.findById(id);
        return ResponseEntity.ok(pagamento);
    }

    @GetMapping("/reserva/{reservaId}")
    @Operation(summary = "Listar pagamentos por reserva")
    public ResponseEntity<List<PagamentoResponseDTO>> findByReservaId(@PathVariable Integer reservaId) {
        List<PagamentoResponseDTO> pagamentos = pagamentoService.findByReservaId(reservaId);
        return ResponseEntity.ok(pagamentos);
    }

    @PostMapping
    @Operation(summary = "Registrar novo pagamento")
    public ResponseEntity<PagamentoResponseDTO> create(@Valid @RequestBody PagamentoRequestDTO requestDTO) {
        PagamentoResponseDTO pagamento = pagamentoService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamento);
    }
}
