package com.seucantinho.api.feature.pagamento.infrastructure.adapter.out;

import com.seucantinho.api.feature.pagamento.domain.Pagamento;
import com.seucantinho.api.feature.pagamento.infrastructure.persistence.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PagamentoRepositoryAdapter")
class PagamentoRepositoryAdapterTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    private PagamentoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new PagamentoRepositoryAdapter(pagamentoRepository);
    }

    @Test
    @DisplayName("Deve salvar pagamento")
    void deveSalvarPagamento() {
        Pagamento pagamento = Pagamento.builder().build();
        when(pagamentoRepository.save(pagamento)).thenReturn(pagamento);

        Pagamento result = adapter.save(pagamento);

        assertNotNull(result);
        verify(pagamentoRepository).save(pagamento);
    }

    @Test
    @DisplayName("Deve buscar todos os pagamentos")
    void deveBuscarTodosPagamentos() {
        Pagamento pagamento1 = Pagamento.builder().id(1).build();
        Pagamento pagamento2 = Pagamento.builder().id(2).build();
        List<Pagamento> pagamentos = Arrays.asList(pagamento1, pagamento2);

        when(pagamentoRepository.findAll()).thenReturn(pagamentos);

        List<Pagamento> result = adapter.findAll();

        assertEquals(2, result.size());
        verify(pagamentoRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar pagamento por ID")
    void deveBuscarPagamentoPorId() {
        Pagamento pagamento = Pagamento.builder().id(1).build();
        when(pagamentoRepository.findById(1)).thenReturn(Optional.of(pagamento));

        Optional<Pagamento> result = adapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(pagamentoRepository).findById(1);
    }

    @Test
    @DisplayName("Deve buscar pagamentos por reserva")
    void deveBuscarPagamentosPorReserva() {
        Pagamento pagamento = Pagamento.builder().id(1).build();
        List<Pagamento> pagamentos = Arrays.asList(pagamento);

        when(pagamentoRepository.findByReservaId(1)).thenReturn(pagamentos);

        List<Pagamento> result = adapter.findByReservaId(1);

        assertEquals(1, result.size());
        verify(pagamentoRepository).findByReservaId(1);
    }
}

