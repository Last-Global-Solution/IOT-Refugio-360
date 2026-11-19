package org.fiap.gs_final.model;

import java.util.Locale;
import java.util.Set;

/**
 * Ciclo de vida de uma ocorrência. As transições permitidas ficam aqui, e
 * {@link Ocorrencia#moverPara(StatusOcorrencia)} é o único ponto que muda o status.
 *
 * <pre>
 * PENDENTE --> EM_ANALISE --> RESOLVIDA
 *     \______________________/
 * </pre>
 * Uma ocorrência resolvida não volta a ficar aberta: se o problema reaparecer,
 * o cidadão registra uma nova.
 */
public enum StatusOcorrencia {
    PENDENTE, EM_ANALISE, RESOLVIDA;

    public Set<StatusOcorrencia> proximos() {
        return switch (this) {
            case PENDENTE -> Set.of(EM_ANALISE, RESOLVIDA);
            case EM_ANALISE -> Set.of(PENDENTE, RESOLVIDA);
            case RESOLVIDA -> Set.of();
        };
    }

    public boolean podeIrPara(StatusOcorrencia destino) {
        return proximos().contains(destino);
    }

    /** Converte o texto recebido na API; devolve null quando não é um status conhecido. */
    public static StatusOcorrencia deTexto(String valor) {
        if (valor == null) return null;
        try {
            return valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
