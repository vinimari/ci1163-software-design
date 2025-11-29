package com.seucantinho.api.feature.reserva.application.service;

import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.espaco.domain.valueobject.Capacidade;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.reserva.domain.service.ReservaAvailabilityService;
import com.seucantinho.api.feature.reserva.domain.valueobject.DataEvento;
import com.seucantinho.api.shared.domain.exception.BusinessException;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ReservaValidationService")
class ReservaValidationServiceTest {

    @Mock
    private ReservaAvailabilityService reservaAvailabilityService;

    @InjectMocks
    private ReservaValidationService reservaValidationService;

    private Espaco espaco;
    private Reserva reserva;
    private Filial filial;

    @BeforeEach
    void setUp() {
        filial = Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .build();

        espaco = Espaco.builder()
                .id(1)
                .nome("Salão de Eventos")
                .capacidade(Capacidade.of(50))
                .precoDiaria(ValorMonetario.of("300.00"))
                .filial(filial)
                .ativo(true)
                .build();

        reserva = Reserva.builder()
                .id(1)
                .espaco(espaco)
                .dataEvento(DataEvento.of(LocalDate.now().plusDays(10)))
                .valorTotal(ValorMonetario.of("300.00"))
                .build();
    }

    @Test
    @DisplayName("Deve validar espaço ativo com sucesso")
    void deveValidarEspacoAtivoComSucesso() {
        // Act & Assert
        assertThatCode(() -> reservaValidationService.validateEspacoAtivo(espaco))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar exceção quando espaço está inativo")
    void deveLancarExcecaoQuandoEspacoEstaInativo() {
        // Arrange
        espaco.setAtivo(false);

        // Act & Assert
        assertThatThrownBy(() -> reservaValidationService.validateEspacoAtivo(espaco))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Espaço não está ativo para reservas");
    }

    @Test
    @DisplayName("Deve validar disponibilidade com sucesso")
    void deveValidarDisponibilidadeComSucesso() {
        // Arrange
        Integer espacoId = 1;
        LocalDate dataEvento = LocalDate.now().plusDays(10);
        Integer reservaId = null;
        
        doNothing().when(reservaAvailabilityService).validarDisponibilidade(espacoId, dataEvento, reservaId);

        // Act & Assert
        assertThatCode(() -> reservaValidationService.validateDisponibilidade(espacoId, dataEvento, reservaId))
                .doesNotThrowAnyException();
        
        verify(reservaAvailabilityService).validarDisponibilidade(espacoId, dataEvento, reservaId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar disponibilidade com data inválida")
    void deveLancarExcecaoAoValidarDisponibilidadeComDataInvalida() {
        // Arrange
        Integer espacoId = 1;
        LocalDate dataPassada = LocalDate.now().minusDays(1);
        Integer reservaId = null;

        // Act & Assert
        assertThatThrownBy(() -> reservaValidationService.validateDisponibilidade(espacoId, dataPassada, reservaId))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Deve validar valor total correto")
    void deveValidarValorTotalCorreto() {
        // Act & Assert
        assertThatCode(() -> reservaValidationService.validateValorTotal(reserva))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor total está incorreto")
    void deveLancarExcecaoQuandoValorTotalEstaIncorreto() {
        // Arrange
        reserva.setValorTotal(ValorMonetario.of("500.00")); // Valor diferente do preço da diária

        // Act & Assert
        assertThatThrownBy(() -> reservaValidationService.validateValorTotal(reserva))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Valor total incorreto");
    }

    @Test
    @DisplayName("Deve lançar exceção quando espaço é nulo")
    void deveLancarExcecaoQuandoEspacoENulo() {
        // Arrange
        reserva.setEspaco(null);

        // Act & Assert
        assertThatThrownBy(() -> reservaValidationService.validateValorTotal(reserva))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Espaço não pode ser nulo");
    }

    @Test
    @DisplayName("Deve validar disponibilidade com reservaId para atualização")
    void deveValidarDisponibilidadeComReservaIdParaAtualizacao() {
        // Arrange
        Integer espacoId = 1;
        LocalDate dataEvento = LocalDate.now().plusDays(15);
        Integer reservaId = 1;
        
        doNothing().when(reservaAvailabilityService).validarDisponibilidade(espacoId, dataEvento, reservaId);

        // Act & Assert
        assertThatCode(() -> reservaValidationService.validateDisponibilidade(espacoId, dataEvento, reservaId))
                .doesNotThrowAnyException();
        
        verify(reservaAvailabilityService).validarDisponibilidade(espacoId, dataEvento, reservaId);
    }

    @Test
    @DisplayName("Deve propagar exceção de disponibilidade")
    void devePropagarExcecaoDeDisponibilidade() {
        // Arrange
        Integer espacoId = 1;
        LocalDate dataEvento = LocalDate.now().plusDays(10);
        Integer reservaId = null;
        
        doThrow(new BusinessException("Espaço já possui reserva ativa para esta data"))
                .when(reservaAvailabilityService).validarDisponibilidade(espacoId, dataEvento, reservaId);

        // Act & Assert
        assertThatThrownBy(() -> reservaValidationService.validateDisponibilidade(espacoId, dataEvento, reservaId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Espaço já possui reserva ativa para esta data");
    }

    @Test
    @DisplayName("Deve validar valor total com valores decimais")
    void deveValidarValorTotalComValoresDecimais() {
        // Arrange
        espaco.setPrecoDiaria(ValorMonetario.of("299.99"));
        reserva.setValorTotal(ValorMonetario.of("299.99"));

        // Act & Assert
        assertThatCode(() -> reservaValidationService.validateValorTotal(reserva))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar exceção com mensagem detalhada sobre valor incorreto")
    void deveLancarExcecaoComMensagemDetalhadaSobreValorIncorreto() {
        // Arrange
        espaco.setPrecoDiaria(ValorMonetario.of("300.00"));
        reserva.setValorTotal(ValorMonetario.of("250.00"));

        // Act & Assert
        assertThatThrownBy(() -> reservaValidationService.validateValorTotal(reserva))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Valor total incorreto")
                .hasMessageContaining("Esperado:")
                .hasMessageContaining("Recebido:");
    }

    @Test
    @DisplayName("Deve validar espaço ativo múltiplas vezes")
    void deveValidarEspacoAtivoMultiplasVezes() {
        // Act & Assert
        assertThatCode(() -> {
            reservaValidationService.validateEspacoAtivo(espaco);
            reservaValidationService.validateEspacoAtivo(espaco);
            reservaValidationService.validateEspacoAtivo(espaco);
        }).doesNotThrowAnyException();
    }
}
