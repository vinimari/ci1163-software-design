package com.seucantinho.api.feature.espaco.domain.valueobject;

import com.seucantinho.api.shared.domain.exception.BusinessException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Capacidade implements Serializable {

    private static final int CAPACIDADE_MINIMA = 1;
    private static final int CAPACIDADE_MAXIMA = 1000;

    private Integer quantidade;

    private Capacidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public static Capacidade of(Integer quantidade) {
        if (quantidade == null) {
            throw new BusinessException("Capacidade não pode ser nula");
        }
        validarCapacidade(quantidade);
        return new Capacidade(quantidade);
    }

    private static void validarCapacidade(Integer quantidade) {
        if (quantidade < CAPACIDADE_MINIMA) {
            throw new BusinessException(
                String.format("Capacidade deve ser no mínimo %d pessoa(s)", CAPACIDADE_MINIMA)
            );
        }

        if (quantidade > CAPACIDADE_MAXIMA) {
            throw new BusinessException(
                String.format("Capacidade não pode exceder %d pessoas por questões de segurança",
                    CAPACIDADE_MAXIMA)
            );
        }
    }

    @Override
    public String toString() {
        return quantidade + " pessoa(s)";
    }
}
