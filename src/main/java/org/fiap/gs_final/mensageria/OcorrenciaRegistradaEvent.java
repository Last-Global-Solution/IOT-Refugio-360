package org.fiap.gs_final.mensageria;

/** Mensagem publicada no RabbitMQ quando um cidadão registra uma ocorrência. */
public record OcorrenciaRegistradaEvent(Long ocorrenciaId, String perfil) {}
