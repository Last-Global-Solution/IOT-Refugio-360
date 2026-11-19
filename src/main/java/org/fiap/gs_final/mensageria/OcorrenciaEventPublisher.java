package org.fiap.gs_final.mensageria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Leva o evento para o RabbitMQ só depois do commit: assim o consumidor nunca
 * procura uma ocorrência que ainda não foi gravada.
 *
 * Se o broker estiver fora do ar, a ocorrência continua registrada; a falha vai
 * para o log e a orientação pode ser pedida em POST /api/ai/ocorrencias/{id}/orientacao.
 */
@Component
public class OcorrenciaEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(OcorrenciaEventPublisher.class);

    private final RabbitTemplate rabbit;
    private final String exchange;
    private final String routingKey;

    public OcorrenciaEventPublisher(RabbitTemplate rabbit,
                                    @Value("${app.mensageria.exchange}") String exchange,
                                    @Value("${app.mensageria.routing-key}") String routingKey) {
        this.rabbit = rabbit;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publicar(OcorrenciaRegistradaEvent evento) {
        try {
            rabbit.convertAndSend(exchange, routingKey, evento);
            log.info("Evento publicado: ocorrência {} registrada", evento.ocorrenciaId());
        } catch (AmqpException ex) {
            log.warn("RabbitMQ indisponível; ocorrência {} salva sem orientação automática: {}",
                    evento.ocorrenciaId(), ex.getMessage());
        }
    }
}
