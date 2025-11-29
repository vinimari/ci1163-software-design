package com.seucantinho.api.feature.pagamento.infrastructure.adapter.in.web;

import com.seucantinho.api.feature.pagamento.application.dto.PagamentoRequestDTO;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoResponseDTO;
import com.seucantinho.api.feature.pagamento.domain.port.in.PagamentoServicePort;
import com.seucantinho.api.feature.pagamento.domain.port.in.PagamentoWebPort;
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
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Endpoints para gerenciamento de pagamentos de reservas")
@SecurityRequirement(name = "bearer-jwt")
public class PagamentoWebAdapter implements PagamentoWebPort {

    private final PagamentoServicePort pagamentoService;

    @Override
    @GetMapping
    @Operation(
        summary = "Listar pagamentos",
        description = "Retorna a lista de todos os pagamentos registrados no sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pagamentos retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content)
    })
    public ResponseEntity<List<PagamentoResponseDTO>> findAll() {
        List<PagamentoResponseDTO> pagamentos = pagamentoService.findAll();
        return ResponseEntity.ok(pagamentos);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar pagamento por ID",
        description = "Retorna os dados completos de um pagamento específico, incluindo valor, método e data."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pagamento encontrado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado", content = @Content)
    })
    public ResponseEntity<PagamentoResponseDTO> findById(
            @Parameter(description = "ID do pagamento", required = true)
            @PathVariable Integer id) {
        PagamentoResponseDTO pagamento = pagamentoService.findById(id);
        return ResponseEntity.ok(pagamento);
    }

    @Override
    @GetMapping("/reserva/{reservaId}")
    @Operation(
        summary = "Buscar pagamentos por reserva",
        description = "Retorna todos os pagamentos (sinal e parcelas) associados a uma reserva específica. " +
                     "Útil para acompanhar o histórico de pagamentos de uma reserva."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pagamentos da reserva retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar pagamentos desta reserva", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reserva não encontrada", content = @Content)
    })
    public ResponseEntity<List<PagamentoResponseDTO>> findByReservaId(
            @Parameter(description = "ID da reserva", required = true)
            @PathVariable Integer reservaId) {
        List<PagamentoResponseDTO> pagamentos = pagamentoService.findByReservaId(reservaId);
        return ResponseEntity.ok(pagamentos);
    }

    @Override
    @PostMapping
    @Operation(
        summary = "Registrar novo pagamento",
        description = "Registra um novo pagamento para uma reserva. O pagamento pode ser sinal, parcela ou pagamento total. " +
                     "Atualiza automaticamente o status da reserva baseado nos pagamentos realizados."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Pagamento registrado com sucesso",
            content = @Content(schema = @Schema(implementation = PagamentoResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou valor do pagamento inconsistente", content = @Content),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para registrar pagamentos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reserva não encontrada", content = @Content)
    })
    public ResponseEntity<PagamentoResponseDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados do pagamento a ser registrado",
                required = true,
                content = @Content(schema = @Schema(implementation = PagamentoRequestDTO.class))
            )
            @Valid @RequestBody PagamentoRequestDTO requestDTO) {
        PagamentoResponseDTO pagamento = pagamentoService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamento);
    }
}