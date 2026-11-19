package org.fiap.gs_final.dto;

import org.fiap.gs_final.model.LocalSeguro;
import org.fiap.gs_final.model.Orientacao;

import java.time.Instant;
import java.util.List;

public record OrientacaoResponseDTO(
        Long id,
        Long ocorrenciaId,
        String mensagem,
        String perfil,
        boolean geradoPorIA,
        List<Long> locaisConsiderados,
        Instant criadaEm
) {
    public static OrientacaoResponseDTO de(Orientacao o) {
        return new OrientacaoResponseDTO(o.getId(), o.getOcorrencia().getId(), o.getMensagem(), o.getPerfil(),
                o.isGeradoPorIA(), o.getLocaisConsiderados().stream().map(LocalSeguro::getId).toList(),
                o.getCriadaEm());
    }
}
