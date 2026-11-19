package org.fiap.gs_final.mensageria;

import org.fiap.gs_final.ai.OrientacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consome o evento de ocorrência registrada e gera a orientação com a IA, fora
 * da requisição do cidadão: o app recebe a resposta do POST na hora, e a
 * orientação fica disponível em GET /api/ocorrencias/{id}/orientacoes.
 */
@Component
public class OrientacaoListener {
    private static final Logger log = LoggerFactory.getLogger(OrientacaoListener.class);

    private final OrientacaoService orientacoes;

    public OrientacaoListener(OrientacaoService orientacoes) { this.orientacoes = orientacoes; }

    @RabbitListener(queues = "${app.mensageria.fila-orientacao}")
    public void aoRegistrarOcorrencia(OcorrenciaRegistradaEvent evento) {
        log.info("Gerando orientação para a ocorrência {}", evento.ocorrenciaId());
        orientacoes.gerar(evento.ocorrenciaId(), evento.perfil());
    }
}
