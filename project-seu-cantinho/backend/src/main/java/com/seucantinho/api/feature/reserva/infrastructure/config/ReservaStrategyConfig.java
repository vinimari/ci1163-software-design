package com.seucantinho.api.feature.reserva.infrastructure.config;

import com.seucantinho.api.feature.reserva.domain.strategy.StatusTransitionStrategy;
import com.seucantinho.api.feature.reserva.domain.strategy.impl.QuitacaoStatusTransitionStrategy;
import com.seucantinho.api.feature.reserva.domain.strategy.impl.SinalStatusTransitionStrategy;
import com.seucantinho.api.feature.reserva.domain.strategy.impl.TotalStatusTransitionStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ReservaStrategyConfig {

    @Bean
    public TotalStatusTransitionStrategy totalStatusTransitionStrategy() {
        return new TotalStatusTransitionStrategy();
    }

    @Bean
    public SinalStatusTransitionStrategy sinalStatusTransitionStrategy() {
        return new SinalStatusTransitionStrategy();
    }

    @Bean
    public QuitacaoStatusTransitionStrategy quitacaoStatusTransitionStrategy() {
        return new QuitacaoStatusTransitionStrategy();
    }

    @Bean
    public List<StatusTransitionStrategy> statusTransitionStrategies(
            TotalStatusTransitionStrategy totalStrategy,
            SinalStatusTransitionStrategy sinalStrategy,
            QuitacaoStatusTransitionStrategy quitacaoStrategy) {
        return List.of(totalStrategy, sinalStrategy, quitacaoStrategy);
    }
}