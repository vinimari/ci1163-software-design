package com.seucantinho.api.feature.funcionario.infrastructure.adapter.in.web;

import com.seucantinho.api.core.validation.OnCreate;
import com.seucantinho.api.core.validation.OnUpdate;
import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioRequestDTO;
import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioResponseDTO;
import com.seucantinho.api.feature.funcionario.domain.port.in.FuncionarioServicePort;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funcionarios")
@RequiredArgsConstructor
@Tag(name = "Funcionários", description = "Endpoints para gerenciamento de funcionários do sistema")
@SecurityRequirement(name = "bearer-jwt")
public class FuncionarioWebAdapter {

    private final FuncionarioServicePort funcionarioService;

    @Operation(
        summary = "Listar funcionários",
        description = "Retorna a lista de todos os funcionários cadastrados. Opcionalmente pode filtrar por filial específica."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de funcionários retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public ResponseEntity<List<FuncionarioResponseDTO>> findAll(
            @Parameter(description = "ID da filial para filtrar funcionários (opcional)")
            @RequestParam(required = false) Integer filialId) {
        List<FuncionarioResponseDTO> funcionarios = filialId != null
                ? funcionarioService.findByFilialId(filialId)
                : funcionarioService.findAll();
        return ResponseEntity.ok(funcionarios);
    }

    @Operation(
        summary = "Buscar funcionário por ID",
        description = "Retorna os dados completos de um funcionário específico, incluindo informações da filial associada."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Funcionário encontrado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso", content = @Content),
        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public ResponseEntity<FuncionarioResponseDTO> findById(
            @Parameter(description = "ID do funcionário", required = true)
            @PathVariable Integer id) {
        FuncionarioResponseDTO funcionario = funcionarioService.findById(id);
        return ResponseEntity.ok(funcionario);
    }

    @Operation(
        summary = "Cadastrar novo funcionário",
        description = "Cria um novo funcionário no sistema com matrícula única e associação a uma filial. " +
                     "O email deve ser único em todo o sistema e a matrícula deve ser única entre funcionários."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Funcionário cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = FuncionarioResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Apenas administradores podem cadastrar funcionários", content = @Content),
        @ApiResponse(responseCode = "409", description = "Email ou matrícula já cadastrados", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados do funcionário a ser cadastrado",
                required = true,
                content = @Content(schema = @Schema(implementation = FuncionarioRequestDTO.class))
            )
            @Validated(OnCreate.class) @RequestBody FuncionarioRequestDTO requestDTO) {
        FuncionarioResponseDTO funcionario = funcionarioService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionario);
    }

    @Operation(
        summary = "Atualizar funcionário",
        description = "Atualiza os dados de um funcionário existente. A senha é opcional na atualização. " +
                     "Email e matrícula devem permanecer únicos no sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Funcionário atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = FuncionarioResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Apenas administradores podem atualizar funcionários", content = @Content),
        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Email ou matrícula já cadastrados para outro funcionário", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> update(
            @Parameter(description = "ID do funcionário a ser atualizado", required = true)
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Novos dados do funcionário",
                required = true,
                content = @Content(schema = @Schema(implementation = FuncionarioRequestDTO.class))
            )
            @Validated(OnUpdate.class) @RequestBody FuncionarioRequestDTO requestDTO) {
        FuncionarioResponseDTO funcionario = funcionarioService.update(id, requestDTO);
        return ResponseEntity.ok(funcionario);
    }

    @Operation(
        summary = "Excluir funcionário",
        description = "Remove permanentemente um funcionário do sistema. Esta operação não pode ser desfeita."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Funcionário excluído com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Apenas administradores podem excluir funcionários", content = @Content),
        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do funcionário a ser excluído", required = true)
            @PathVariable Integer id) {
        funcionarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Ativar ou desativar funcionário",
        description = "Altera o status de ativo/inativo do funcionário. Funcionários inativos não podem acessar o sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Status do funcionário atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = FuncionarioResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Apenas administradores podem alterar status de funcionários", content = @Content),
        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado", content = @Content)
    })
    @PatchMapping("/{id}/ativo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> toggleAtivo(
            @Parameter(description = "ID do funcionário", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Novo status (true para ativar, false para desativar)", required = true)
            @RequestParam Boolean ativo) {
        FuncionarioResponseDTO funcionario = funcionarioService.toggleAtivo(id, ativo);
        return ResponseEntity.ok(funcionario);
    }

    @Operation(
        summary = "Transferir funcionário para outra filial",
        description = "Realiza a transferência de um funcionário para uma filial diferente. " +
                     "A filial de destino deve existir e estar ativa no sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Funcionário transferido com sucesso",
            content = @Content(schema = @Schema(implementation = FuncionarioResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Apenas administradores podem transferir funcionários", content = @Content),
        @ApiResponse(responseCode = "404", description = "Funcionário ou filial não encontrados", content = @Content)
    })
    @PatchMapping("/{id}/filial")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> trocarFilial(
            @Parameter(description = "ID do funcionário a ser transferido", required = true)
            @PathVariable Integer id,
            @Parameter(description = "ID da filial de destino", required = true)
            @RequestParam Integer filialId) {
        FuncionarioResponseDTO funcionario = funcionarioService.trocarFilial(id, filialId);
        return ResponseEntity.ok(funcionario);
    }
}
