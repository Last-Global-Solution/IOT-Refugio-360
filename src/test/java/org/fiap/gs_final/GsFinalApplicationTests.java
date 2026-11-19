package org.fiap.gs_final;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// O teste de contexto não depende de um RabbitMQ no ar: o consumidor da fila não é iniciado.
@SpringBootTest(properties = {
		"spring.rabbitmq.listener.simple.auto-startup=false",
		"spring.datasource.url=jdbc:h2:mem:refugio360-teste;DB_CLOSE_DELAY=-1"
})
class GsFinalApplicationTests {

	@Test
	void contextLoads() {
	}

}
