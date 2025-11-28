package com.seucantinho.api.feature.pagamento.infrastructure.adapter.in.web;

import com.seucantinho.api.feature.pagamento.application.dto.PagamentoRequestDTO;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoResponseDTO;
import com.seucantinho.api.feature.pagamento.domain.port.in.PagamentoServicePort;
import com.seucantinho.api.feature.pagamento.domain.port.in.PagamentoWebPort;
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
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamento", description = "API para gerenciamento de pagamentos")
public class PagamentoWebAdapter implements PagamentoWebPort {

    private final PagamentoServicePort pagamentoService;

    @Override
    @GetMapping
    @Operation(summary = "Listar todos os pagamentos")
    @ApiResponse(responseCode = "200", description = "Lista de pagamentos retornada com sucesso")
    public ResponseEntity<List<PagamentoResponseDTO>> findAll() {
        List<PagamentoResponseDTO> pagamentos = pagamentoService.findAll();
        return ResponseEntity.ok(pagamentos);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "Buscar pagamento por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pagamento encontrado"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
    })
    public ResponseEntity<PagamentoResponseDTO> findById(@PathVariable Integer id) {
        PagamentoResponseDTO pagamento = pagamentoService.findById(id);
        return ResponseEntity.ok(pagamento);
    }

    @Override
    @GetMapping("/reserva/{reservaId}")
    @Operation(summary = "Buscar pagamentos por reserva")
    @ApiResponse(responseCode = "200", description = "Lista de pagamentos da reserva retornada com sucesso")
    public ResponseEntity<List<PagamentoResponseDTO>> findByReservaId(@PathVariable Integer reservaId) {
        List<PagamentoResponseDTO> pagamentos = pagamentoService.findByReservaId(reservaId);
        return ResponseEntity.ok(pagamentos);
    }

    @Override
    @PostMapping
    @Operation(summary = "Criar novo pagamento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pagamento criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<PagamentoResponseDTO> create(@Valid @RequestBody PagamentoRequestDTO requestDTO) {
        PagamentoResponseDTO pagamento = pagamentoService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamento);
    }
}