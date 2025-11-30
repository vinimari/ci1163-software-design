package com.seucantinho.api.feature.reserva.infrastructure.adapter.in.web;

import com.seucantinho.api.feature.reserva.application.dto.ReservaRequestDTO;
import com.seucantinho.api.feature.reserva.application.dto.ReservaResponseDTO;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.reserva.domain.port.in.ReservaServicePort;
import com.seucantinho.api.feature.reserva.domain.port.in.ReservaWebPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Endpoints para gerenciamento de reservas de espaços para eventos")
@SecurityRequirement(name = "bearer-jwt")
public class ReservaWebAdapter implements ReservaWebPort {

    private final ReservaServicePort reservaService;

    @GetMapping
    @Operation(
        summary = "Listar reservas",
        description = "Retorna a lista de todas as reservas cadastradas no sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content)
    })
    public ResponseEntity<List<ReservaResponseDTO>> findAll() {
        List<ReservaResponseDTO> reservas = reservaService.findAll();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar reserva por ID",
        description = "Retorna os dados completos de uma reserva específica, incluindo informações do cliente, espaço e pagamentos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva encontrada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reserva não encontrada", content = @Content)
    })
    public ResponseEntity<ReservaResponseDTO> findById(
            @Parameter(description = "ID da reserva", required = true)
            @PathVariable Integer id) {
        ReservaResponseDTO reserva = reservaService.findById(id);
        return ResponseEntity.ok(reserva);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(
        summary = "Listar reservas por usuário",
        description = "Retorna todas as reservas realizadas por um usuário específico (cliente)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas do usuário retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar reservas deste usuário", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<List<ReservaResponseDTO>> findByUsuarioId(
            @Parameter(description = "ID do usuário (cliente)", required = true)
            @PathVariable Integer usuarioId) {
        List<ReservaResponseDTO> reservas = reservaService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/espaco/{espacoId}")
    @Operation(
        summary = "Listar reservas por espaço",
        description = "Retorna todas as reservas realizadas para um espaço específico. Útil para verificar agenda de ocupação."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas do espaço retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content),
        @ApiResponse(responseCode = "404", description = "Espaço não encontrado", content = @Content)
    })
    public ResponseEntity<List<ReservaResponseDTO>> findByEspacoId(
            @Parameter(description = "ID do espaço", required = true)
            @PathVariable Integer espacoId) {
        List<ReservaResponseDTO> reservas = reservaService.findByEspacoId(espacoId);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/acesso/{email}")
    @Operation(
        summary = "Listar reservas por email do usuário",
        description = "Retorna todas as reservas de um usuário identificado pelo email. Endpoint de conveniência."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar reservas deste usuário", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<List<ReservaResponseDTO>> findByAcessoPorEmail(
            @Parameter(description = "Email do usuário", required = true, example = "cliente@example.com")
            @PathVariable String email) {
        List<ReservaResponseDTO> reservas = reservaService.findByAcessoPorEmail(email);
        return ResponseEntity.ok(reservas);
    }

    @PostMapping
    @Operation(
        summary = "Criar nova reserva",
        description = "Cria uma nova reserva de espaço para um evento. Valida disponibilidade do espaço na data solicitada. " +
                     "A reserva inicia com status PENDENTE até confirmação de pagamento."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Reserva criada com sucesso",
            content = @Content(schema = @Schema(implementation = ReservaResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou data no passado", content = @Content),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para criar reservas", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuário ou espaço não encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Espaço não disponível na data solicitada", content = @Content)
    })
    public ResponseEntity<ReservaResponseDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados da reserva a ser criada",
                required = true,
                content = @Content(schema = @Schema(implementation = ReservaRequestDTO.class))
            )
            @Valid @RequestBody ReservaRequestDTO requestDTO) {
        ReservaResponseDTO reserva = reservaService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar reserva",
        description = "Atualiza os dados de uma reserva existente. Apenas reservas com status PENDENTE podem ser totalmente atualizadas."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Reserva atualizada com sucesso",
            content = @Content(schema = @Schema(implementation = ReservaResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou operação não permitida para o status atual", content = @Content),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar esta reserva", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reserva não encontrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflito com disponibilidade do espaço", content = @Content)
    })
    public ResponseEntity<ReservaResponseDTO> update(
            @Parameter(description = "ID da reserva a ser atualizada", required = true)
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Novos dados da reserva",
                required = true,
                content = @Content(schema = @Schema(implementation = ReservaRequestDTO.class))
            )
            @Valid @RequestBody ReservaRequestDTO requestDTO) {
        ReservaResponseDTO reserva = reservaService.update(id, requestDTO);
        return ResponseEntity.ok(reserva);
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Atualizar status da reserva",
        description = "Altera o status da reserva seguindo o fluxo: PENDENTE → SINAL → CONFIRMADA → QUITADA. " +
                     "Também permite cancelamento (CANCELADA) a qualquer momento."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Status da reserva atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = ReservaResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Transição de status inválida", content = @Content),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para alterar status desta reserva", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reserva não encontrada", content = @Content)
    })
    public ResponseEntity<ReservaResponseDTO> updateStatus(
            @Parameter(description = "ID da reserva", required = true)
            @PathVariable Integer id,
            @Parameter(
                description = "Novo status da reserva",
                required = true,
                schema = @Schema(implementation = StatusReservaEnum.class, example = "CONFIRMADA")
            )
            @RequestParam StatusReservaEnum status) {
        ReservaResponseDTO reserva = reservaService.updateStatus(id, status);
        return ResponseEntity.ok(reserva);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Cancelar reserva",
        description = "Cancela uma reserva existente. A reserva não é excluída, mas seu status é alterado para CANCELADA."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reserva cancelada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para cancelar esta reserva", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reserva não encontrada", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da reserva a ser cancelada", required = true)
            @PathVariable Integer id) {
        reservaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}