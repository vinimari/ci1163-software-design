package com.seucantinho.api.domain.valueobject;

import com.seucantinho.api.exception.BusinessException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ValorMonetario implements Serializable {

    private BigDecimal valor;

    private ValorMonetario(BigDecimal valor) {
        this.valor = valor.setScale(2, RoundingMode.HALF_UP);
    }

    public static ValorMonetario of(BigDecimal valor) {
        if (valor == null) {
            throw new BusinessException("Valor monetário não pode ser nulo");
        }
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Valor monetário não pode ser negativo");
        }
        return new ValorMonetario(valor);
    }

    public static ValorMonetario of(String valor) {
        try {
            return of(new BigDecimal(valor));
        } catch (NumberFormatException e) {
            throw new BusinessException("Valor monetário inválido: " + valor);
        }
    }

    public static ValorMonetario of(double valor) {
        return of(BigDecimal.valueOf(valor));
    }

    public static ValorMonetario zero() {
        return new ValorMonetario(BigDecimal.ZERO);
    }

    public ValorMonetario somar(ValorMonetario outro) {
        if (outro == null) {
            return this;
        }
        return new ValorMonetario(this.valor.add(outro.valor));
    }

    public ValorMonetario subtrair(ValorMonetario outro) {
        if (outro == null) {
            return this;
        }
        BigDecimal resultado = this.valor.subtract(outro.valor);
        if (resultado.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Resultado da subtração não pode ser negativo");
        }
        return new ValorMonetario(resultado);
    }

    public ValorMonetario calcularMetade() {
        return new ValorMonetario(this.valor.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP));
    }

    public boolean isMaiorQue(ValorMonetario outro) {
        return this.valor.compareTo(outro.valor) > 0;
    }

    public boolean isMenorQue(ValorMonetario outro) {
        return this.valor.compareTo(outro.valor) < 0;
    }

    public boolean isIgualA(ValorMonetario outro) {
        return this.valor.compareTo(outro.valor) == 0;
    }

    public boolean isMaiorOuIgualA(ValorMonetario outro) {
        return this.valor.compareTo(outro.valor) >= 0;
    }

    public boolean isZero() {
        return this.valor.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositivo() {
        return this.valor.compareTo(BigDecimal.ZERO) > 0;
    }

    public String getValorFormatado() {
        return String.format("R$ %.2f", valor);
    }

    @Override
    public String toString() {
        return getValorFormatado();
    }
}
