package org.fiap.gs_final.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusOcorrenciaTest {

	@Test
	void ocorrenciaNovaComecaPendenteEPodeSerAnalisadaEResolvida() {
		Ocorrencia o = new Ocorrencia("Rua alagada", "ALTA", -23.5, -46.6);
		assertEquals("PENDENTE", o.getStatus());
		assertTrue(o.moverPara(StatusOcorrencia.EM_ANALISE));
		assertTrue(o.moverPara(StatusOcorrencia.RESOLVIDA));
		assertEquals("RESOLVIDA", o.getStatus());
	}

	@Test
	void ocorrenciaResolvidaNaoReabre() {
		Ocorrencia o = new Ocorrencia("Sem energia", "MEDIA", -23.5, -46.6);
		assertTrue(o.moverPara(StatusOcorrencia.RESOLVIDA));
		assertFalse(o.moverPara(StatusOcorrencia.PENDENTE));
		assertFalse(o.moverPara(StatusOcorrencia.EM_ANALISE));
		assertEquals("RESOLVIDA", o.getStatus());
	}

	@Test
	void mesmoStatusNaoContaComoTransicao() {
		Ocorrencia o = new Ocorrencia("Calor extremo", "BAIXA", -23.5, -46.6);
		assertFalse(o.moverPara(StatusOcorrencia.PENDENTE));
	}

	@Test
	void textoDesconhecidoNaoViraStatus() {
		assertNull(StatusOcorrencia.deTexto("ARQUIVADA"));
		assertEquals(StatusOcorrencia.EM_ANALISE, StatusOcorrencia.deTexto(" em_analise "));
	}
}
