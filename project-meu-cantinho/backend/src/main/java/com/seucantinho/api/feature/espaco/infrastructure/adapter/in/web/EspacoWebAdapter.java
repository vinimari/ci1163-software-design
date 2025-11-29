package com.seucantinho.api.feature.espaco.infrastructure.adapter.in.web;

import com.seucantinho.api.feature.espaco.application.dto.EspacoRequestDTO;
import com.seucantinho.api.feature.espaco.application.dto.EspacoResponseDTO;
import com.seucantinho.api.feature.espaco.domain.port.in.EspacoServicePort;
import com.seucantinho.api.feature.espaco.domain.port.in.EspacoWebPort;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/espacos")
@RequiredArgsConstructor
@Tag(name = "Espaços", description = "Endpoints para gerenciamento de espaços disponíveis para eventos e reservas")
@SecurityRequirement(name = "bearer-jwt")
public class EspacoWebAdapter implements EspacoWebPort {

    private final EspacoServicePort espacoService;

    @GetMapping
    @Operation(
        summary = "Listar espaços",
        description = "Retorna a lista de todos os espaços cadastrados no sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de espaços retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content)
    })
    public ResponseEntity<List<EspacoResponseDTO>> findAll() {
        List<EspacoResponseDTO> espacos = espacoService.findAll();
        return ResponseEntity.ok(espacos);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar espaço por ID",
        description = "Retorna os dados completos de um espaço específico, incluindo capacidade e informações da filial."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Espaço encontrado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content),
        @ApiResponse(responseCode = "404", description = "Espaço não encontrado", content = @Content)
    })
    public ResponseEntity<EspacoResponseDTO> findById(
            @Parameter(description = "ID do espaço", required = true)
            @PathVariable Integer id) {
        EspacoResponseDTO espaco = espacoService.findById(id);
        return ResponseEntity.ok(espaco);
    }

    @GetMapping("/filial/{filialId}")
    @Operation(
        summary = "Listar espaços por filial",
        description = "Retorna todos os espaços cadastrados em uma filial específica."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de espaços da filial retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content),
        @ApiResponse(responseCode = "404", description = "Filial não encontrada", content = @Content)
    })
    public ResponseEntity<List<EspacoResponseDTO>> findByFilialId(
            @Parameter(description = "ID da filial", required = true)
            @PathVariable Integer filialId) {
        List<EspacoResponseDTO> espacos = espacoService.findByFilialId(filialId);
        return ResponseEntity.ok(espacos);
    }

    @GetMapping("/ativos")
    @Operation(
        summary = "Listar espaços ativos",
        description = "Retorna apenas os espaços que estão ativos e disponíveis para reserva."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de espaços ativos retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content)
    })
    public ResponseEntity<List<EspacoResponseDTO>> findAtivos() {
        List<EspacoResponseDTO> espacos = espacoService.findAtivos();
        return ResponseEntity.ok(espacos);
    }

    @GetMapping("/disponiveis")
    @Operation(
        summary = "Buscar espaços disponíveis",
        description = "Retorna espaços disponíveis para uma data específica, sem reservas confirmadas. " +
                     "Permite filtrar por capacidade mínima necessária."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de espaços disponíveis retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Data ou capacidade inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content)
    })
    public ResponseEntity<List<EspacoResponseDTO>> findDisponiveis(
            @Parameter(description = "Data desejada para a reserva (formato: YYYY-MM-DD)", required = true, example = "2024-12-25")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @Parameter(description = "Capacidade mínima necessária", example = "50")
            @RequestParam(defaultValue = "1") Integer capacidadeMinima) {
        List<EspacoResponseDTO> espacos = espacoService.findDisponiveisPorData(data, capacidadeMinima);
        return ResponseEntity.ok(espacos);
    }

    @PostMapping
    @Operation(
        summary = "Cadastrar novo espaço",
        description = "Cria um novo espaço para eventos associado a uma filial. Apenas administradores podem cadastrar espaços."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Espaço cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = EspacoResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou capacidade fora do intervalo permitido", content = @Content),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Apenas administradores podem cadastrar espaços", content = @Content),
        @ApiResponse(responseCode = "404", description = "Filial não encontrada", content = @Content)
    })
    public ResponseEntity<EspacoResponseDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados do espaço a ser cadastrado",
                required = true,
                content = @Content(schema = @Schema(implementation = EspacoRequestDTO.class))
            )
            @Valid @RequestBody EspacoRequestDTO requestDTO) {
        EspacoResponseDTO espaco = espacoService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(espaco);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar espaço",
        description = "Atualiza os dados de um espaço existente. Apenas administradores podem atualizar espaços."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Espaço atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = EspacoResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Apenas administradores podem atualizar espaços", content = @Content),
        @ApiResponse(responseCode = "404", description = "Espaço não encontrado", content = @Content)
    })
    public ResponseEntity<EspacoResponseDTO> update(
            @Parameter(description = "ID do espaço a ser atualizado", required = true)
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Novos dados do espaço",
                required = true,
                content = @Content(schema = @Schema(implementation = EspacoRequestDTO.class))
            )
            @Valid @RequestBody EspacoRequestDTO requestDTO) {
        EspacoResponseDTO espaco = espacoService.update(id, requestDTO);
        return ResponseEntity.ok(espaco);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir espaço",
        description = "Remove permanentemente um espaço do sistema. Esta operação não pode ser desfeita. " +
                     "Espaços com reservas associadas não podem ser excluídos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Espaço excluído com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Apenas administradores podem excluir espaços", content = @Content),
        @ApiResponse(responseCode = "404", description = "Espaço não encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Espaço possui reservas associadas", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do espaço a ser excluído", required = true)
            @PathVariable Integer id) {
        espacoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}