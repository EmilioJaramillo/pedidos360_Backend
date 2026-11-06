package com.duoc.suspel.emergencia;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.duoc.suspel.entity.ProductoSuspel;
import com.duoc.suspel.repository.ProductoSuspelRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmergenciaServiceTest {

	@Mock
	private ProductoSuspelRepository repository;

	@Mock
	private EmergenciaPublisher publisher;

	private EmergenciaService service;

	@BeforeEach
	void setUp() {
		service = new EmergenciaService(repository, publisher, List.of("Bodega 5", "Bodega 6"));
	}

	@Test
	void incendioPublicaConPrioridadMaximaYSustanciasDeLaBodega() {
		ProductoSuspel propano = new ProductoSuspel();
		propano.setNombre("Propano");
		propano.setImo("Clase 2.1 - Gas Inflamable");
		propano.setCantidad(1200);
		when(repository.findByBodegaOrderByNombre("Bodega 5")).thenReturn(List.of(propano));

		EmergenciaResponse respuesta = service.reportar(new EmergenciaRequest("Bodega 5", TipoEmergencia.INCENDIO, null));

		ArgumentCaptor<EmergenciaEvento> captor = ArgumentCaptor.forClass(EmergenciaEvento.class);
		verify(publisher).publicar(captor.capture());
		EmergenciaEvento evento = captor.getValue();
		assertThat(evento.prioridad()).isEqualTo(9);
		assertThat(evento.sustancias()).extracting(EmergenciaEvento.Sustancia::nombre).containsExactly("Propano");
		assertThat(respuesta.eventoId()).isEqualTo(evento.eventoId());
	}

	@Test
	void bodegaInexistenteNoPublica() {
		assertThatThrownBy(() -> service.reportar(new EmergenciaRequest("Bodega 99", TipoEmergencia.DERRAME, null)))
				.isInstanceOf(BodegaInvalidaException.class);
		verify(publisher, never()).publicar(any());
	}
}
