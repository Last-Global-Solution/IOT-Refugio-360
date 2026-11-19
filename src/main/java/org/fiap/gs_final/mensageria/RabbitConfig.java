package org.fiap.gs_final.mensageria;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia do RabbitMQ. A API declara exchange, fila e ligação ao conectar,
 * então basta o broker estar no ar (docker compose up).
 */
@Configuration
public class RabbitConfig {

    @Bean
    DirectExchange eventosExchange(@Value("${app.mensageria.exchange}") String nome) {
        return new DirectExchange(nome, true, false);
    }

    @Bean
    Queue filaOrientacao(@Value("${app.mensageria.fila-orientacao}") String nome) {
        return QueueBuilder.durable(nome).build();
    }

    @Bean
    Binding ligacaoOrientacao(Queue filaOrientacao, DirectExchange eventosExchange,
                              @Value("${app.mensageria.routing-key}") String routingKey) {
        return BindingBuilder.bind(filaOrientacao).to(eventosExchange).with(routingKey);
    }

    /** Mensagens em JSON, legíveis no painel do RabbitMQ. */
    @Bean
    MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
