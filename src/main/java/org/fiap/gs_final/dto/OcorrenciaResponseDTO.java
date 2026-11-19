package org.fiap.gs_final.dto;

import org.fiap.gs_final.model.Ocorrencia;

/**
 * O que a API devolve de uma ocorrência. Não expõe o autor: outros cidadãos
 * veem o relato, mas não quem o fez.
 */
public record OcorrenciaResponseDTO(
        Long id,
        String descricao,
        String urgencia,
        String status,
        Double latitude,
        Double longitude,
        String perfil
) {
    public static OcorrenciaResponseDTO de(Ocorrencia o) {
        return new OcorrenciaResponseDTO(o.getId(), o.getDescricao(), o.getUrgencia(), o.getStatus(),
                o.getLatitude(), o.getLongitude(), o.getPerfil());
    }
}
