package com.seucantinho.api.feature.pagamento.application.service;

import com.seucantinho.api.feature.pagamento.domain.Pagamento;
import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoRequestDTO;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoResponseDTO;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import com.seucantinho.api.feature.pagamento.infrastructure.mapper.PagamentoMapper;
import com.seucantinho.api.feature.pagamento.infrastructure.persistence.PagamentoRepository;
import com.seucantinho.api.feature.reserva.infrastructure.persistence.ReservaRepository;
import com.seucantinho.api.feature.reserva.application.service.ReservaStatusService;
import com.seucantinho.api.feature.pagamento.application.port.in.IPagamentoService;
import com.seucantinho.api.feature.pagamento.application.validator.PagamentoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagamentoService implements IPagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ReservaRepository reservaRepository;
    private final PagamentoMapper pagamentoMapper;
    private final PagamentoValidator pagamentoValidator;
    private final ReservaStatusService reservaStatusService;

    @Override
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> findAll() {
        return pagamentoRepository.findAll().stream()
                .map(pagamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagamentoResponseDTO findById(Integer id) {
        Pagamento pagamento = findPagamentoById(id);
        return pagamentoMapper.toResponseDTO(pagamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> findByReservaId(Integer reservaId) {
        return pagamentoRepository.findByReservaId(reservaId).stream()
                .map(pagamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PagamentoResponseDTO create(PagamentoRequestDTO requestDTO) {
        Reserva reserva = reservaRepository.findByIdWithPagamentos(requestDTO.getReservaId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com ID: " + requestDTO.getReservaId()));

        pagamentoValidator.validatePagamento(requestDTO, reserva);

        Pagamento pagamento = pagamentoMapper.toEntity(requestDTO, reserva);
        Pagamento savedPagamento = pagamentoRepository.save(pagamento);

        reservaStatusService.updateStatusAfterPayment(reserva, savedPagamento);
        reservaRepository.save(reserva);

        return pagamentoMapper.toResponseDTO(savedPagamento);
    }

    private Pagamento findPagamentoById(Integer id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado com ID: " + id));
    }
}
