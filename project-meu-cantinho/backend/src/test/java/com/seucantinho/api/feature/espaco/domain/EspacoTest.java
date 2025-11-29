package com.seucantinho.api.feature.espaco.domain;

import com.seucantinho.api.feature.espaco.domain.valueobject.Capacidade;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.shared.domain.exception.BusinessException;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes da classe Espaco")
class EspacoTest {

    @Test
    @DisplayName("Deve criar espaço válido com todos os atributos")
    void deveCriarEspacoValidoComTodosOsAtributos() {
        // Arrange
        Filial filial = criarFilial();
        Capacidade capacidade = Capacidade.of(50);
        ValorMonetario preco = ValorMonetario.of("300.00");

        // Act
        Espaco espaco = Espaco.builder()
                .nome("Salão de Festas")
                .descricao("Espaço amplo para eventos")
                .capacidade(capacidade)
                .precoDiaria(preco)
                .filial(filial)
                .ativo(true)
                .urlFotoPrincipal("https://exemplo.com/foto.jpg")
                .build();

        // Assert
        assertThat(espaco).isNotNull();
        assertThat(espaco.getNome()).isEqualTo("Salão de Festas");
        assertThat(espaco.getDescricao()).isEqualTo("Espaço amplo para eventos");
        assertThat(espaco.getCapacidade()).isEqualTo(capacidade);
        assertThat(espaco.getPrecoDiaria()).isEqualTo(preco);
        assertThat(espaco.getFilial()).isEqualTo(filial);
        assertThat(espaco.getAtivo()).isTrue();
        assertThat(espaco.getUrlFotoPrincipal()).isEqualTo("https://exemplo.com/foto.jpg");
    }

    @Test
    @DisplayName("Deve inicializar espaco como ativo por padrão no @PrePersist")
    void deveInicializarEspacoComoAtivoPorPadrao() {
        // Arrange
        Espaco espaco = Espaco.builder()
                .nome("Espaço Teste")
                .filial(criarFilial())
                .build();

        // Act
        espaco.onCreate();

        // Assert
        assertThat(espaco.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve validar espaço com dados corretos sem lançar exceção")
    void deveValidarEspacoComDadosCorretosSemLancarExcecao() {
        // Arrange
        Espaco espaco = criarEspacoValido();

        // Act & Assert
        assertThatCode(() -> espaco.validar()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar espaço sem nome")
    void deveLancarExcecaoAoValidarEspacoSemNome() {
        // Arrange
        Espaco espaco = Espaco.builder()
                .nome(null)
                .filial(criarFilial())
                .build();

        // Act & Assert
        assertThatThrownBy(() -> espaco.validar())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Nome do espaço é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar espaço com nome vazio")
    void deveLancarExcecaoAoValidarEspacoComNomeVazio() {
        // Arrange
        Espaco espaco = Espaco.builder()
                .nome("   ")
                .filial(criarFilial())
                .build();

        // Act & Assert
        assertThatThrownBy(() -> espaco.validar())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Nome do espaço é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar espaço sem filial")
    void deveLancarExcecaoAoValidarEspacoSemFilial() {
        // Arrange
        Espaco espaco = Espaco.builder()
                .nome("Espaço Teste")
                .filial(null)
                .build();

        // Act & Assert
        assertThatThrownBy(() -> espaco.validar())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Filial é obrigatória para o espaço");
    }

    @Test
    @DisplayName("Deve permitir reserva quando espaço está ativo e data é futura")
    void devePermitirReservaQuandoEspacoEstaAtivoEDataEFutura() {
        // Arrange
        Espaco espaco = criarEspacoValido();
        LocalDate dataFutura = LocalDate.now().plusDays(10);

        // Act
        boolean podeSerReservado = espaco.podeSerReservadoPara(dataFutura);

        // Assert
        assertThat(podeSerReservado).isTrue();
    }

    @Test
    @DisplayName("Deve negar reserva quando espaço está inativo")
    void deveNegarReservaQuandoEspacoEstaInativo() {
        // Arrange
        Espaco espaco = criarEspacoValido();
        espaco.setAtivo(false);
        LocalDate dataFutura = LocalDate.now().plusDays(10);

        // Act
        boolean podeSerReservado = espaco.podeSerReservadoPara(dataFutura);

        // Assert
        assertThat(podeSerReservado).isFalse();
    }

    @Test
    @DisplayName("Deve negar reserva quando data é no passado")
    void deveNegarReservaQuandoDataENoPassado() {
        // Arrange
        Espaco espaco = criarEspacoValido();
        LocalDate dataPassada = LocalDate.now().minusDays(1);

        // Act
        boolean podeSerReservado = espaco.podeSerReservadoPara(dataPassada);

        // Assert
        assertThat(podeSerReservado).isFalse();
    }

    @Test
    @DisplayName("Deve negar reserva quando data é hoje")
    void deveNegarReservaQuandoDataEHoje() {
        // Arrange
        Espaco espaco = criarEspacoValido();
        LocalDate hoje = LocalDate.now();

        // Act
        boolean podeSerReservado = espaco.podeSerReservadoPara(hoje);

        // Assert
        assertThat(podeSerReservado).isFalse();
    }

    @Test
    @DisplayName("Deve inicializar lista de reservas vazia")
    void deveInicializarListaDeReservasVazia() {
        // Arrange & Act
        Espaco espaco = Espaco.builder()
                .nome("Espaço Teste")
                .filial(criarFilial())
                .build();

        // Assert
        assertThat(espaco.getReservas()).isNotNull();
        assertThat(espaco.getReservas()).isEmpty();
    }

    @Test
    @DisplayName("Deve permitir definir todos os atributos opcionais")
    void devePermitirDefinirTodosOsAtributosOpcionais() {
        // Arrange
        Filial filial = criarFilial();
        Capacidade capacidade = Capacidade.of(100);
        ValorMonetario preco = ValorMonetario.of("500.00");

        // Act
        Espaco espaco = Espaco.builder()
                .id(1)
                .nome("Auditório")
                .descricao("Grande auditório com equipamentos")
                .capacidade(capacidade)
                .precoDiaria(preco)
                .ativo(true)
                .urlFotoPrincipal("https://exemplo.com/auditorio.jpg")
                .filial(filial)
                .build();

        // Assert
        assertThat(espaco.getId()).isEqualTo(1);
        assertThat(espaco.getNome()).isEqualTo("Auditório");
        assertThat(espaco.getDescricao()).isEqualTo("Grande auditório com equipamentos");
        assertThat(espaco.getCapacidade()).isEqualTo(capacidade);
        assertThat(espaco.getPrecoDiaria()).isEqualTo(preco);
        assertThat(espaco.getAtivo()).isTrue();
        assertThat(espaco.getUrlFotoPrincipal()).isEqualTo("https://exemplo.com/auditorio.jpg");
        assertThat(espaco.getFilial()).isEqualTo(filial);
    }

    @Test
    @DisplayName("Deve permitir criar espaço sem descrição")
    void devePermitirCriarEspacoSemDescricao() {
        // Arrange & Act
        Espaco espaco = Espaco.builder()
                .nome("Espaço Simples")
                .filial(criarFilial())
                .build();

        // Assert
        assertThat(espaco.getDescricao()).isNull();
    }

    @Test
    @DisplayName("Deve permitir criar espaço sem URL de foto")
    void devePermitirCriarEspacoSemUrlFoto() {
        // Arrange & Act
        Espaco espaco = Espaco.builder()
                .nome("Espaço Sem Foto")
                .filial(criarFilial())
                .build();

        // Assert
        assertThat(espaco.getUrlFotoPrincipal()).isNull();
    }

    // Métodos auxiliares
    private Espaco criarEspacoValido() {
        return Espaco.builder()
                .nome("Salão de Eventos")
                .descricao("Espaço completo")
                .capacidade(Capacidade.of(50))
                .precoDiaria(ValorMonetario.of("300.00"))
                .filial(criarFilial())
                .ativo(true)
                .build();
    }

    private Filial criarFilial() {
        return Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .build();
    }
}
