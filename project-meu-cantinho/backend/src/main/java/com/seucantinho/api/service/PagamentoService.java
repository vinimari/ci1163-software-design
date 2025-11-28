package com.seucantinho.api.service;

import com.seucantinho.api.domain.entity.Pagamento;
import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.dto.pagamento.PagamentoRequestDTO;
import com.seucantinho.api.dto.pagamento.PagamentoResponseDTO;
import com.seucantinho.api.exception.ResourceNotFoundException;
import com.seucantinho.api.mapper.PagamentoMapper;
import com.seucantinho.api.repository.PagamentoRepository;
import com.seucantinho.api.repository.ReservaRepository;
import com.seucantinho.api.service.interfaces.IPagamentoService;
import com.seucantinho.api.validator.PagamentoValidator;
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

        // Atualizar status da reserva com base no tipo de pagamento usando o serviço de domínio
        reservaStatusService.updateStatusAfterPayment(reserva, savedPagamento);
        reservaRepository.save(reserva);

        return pagamentoMapper.toResponseDTO(savedPagamento);
    }

    private Pagamento findPagamentoById(Integer id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado com ID: " + id));
    }
}
