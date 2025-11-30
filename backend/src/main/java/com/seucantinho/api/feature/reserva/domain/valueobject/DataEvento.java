package com.seucantinho.api.feature.reserva.domain.valueobject;

import com.seucantinho.api.shared.domain.exception.BusinessException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataEvento implements Serializable {

    private static final int DIAS_MINIMOS_ANTECEDENCIA = 1;
    private static final int DIAS_MAXIMOS_ANTECEDENCIA = 365;

    private LocalDate data;

    private DataEvento(LocalDate data) {
        this.data = data;
    }

    public static DataEvento of(LocalDate data) {
        if (data == null) {
            throw new BusinessException("Data do evento não pode ser nula");
        }
        validarData(data);
        return new DataEvento(data);
    }

    private static void validarData(LocalDate data) {
        LocalDate hoje = LocalDate.now();

        if (data.isBefore(hoje)) {
            throw new BusinessException("Data do evento não pode ser no passado");
        }

        long diasAteEvento = ChronoUnit.DAYS.between(hoje, data);

        if (diasAteEvento < DIAS_MINIMOS_ANTECEDENCIA) {
            throw new BusinessException(
                String.format("Evento deve ser agendado com pelo menos %d dia(s) de antecedência",
                    DIAS_MINIMOS_ANTECEDENCIA)
            );
        }

        if (diasAteEvento > DIAS_MAXIMOS_ANTECEDENCIA) {
            throw new BusinessException(
                String.format("Evento não pode ser agendado com mais de %d dias de antecedência",
                    DIAS_MAXIMOS_ANTECEDENCIA)
            );
        }
    }

    public String getDataFormatada() {
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Override
    public String toString() {
        return getDataFormatada();
    }
}
