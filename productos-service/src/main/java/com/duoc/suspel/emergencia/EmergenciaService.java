package com.duoc.suspel.emergencia;

import com.duoc.suspel.config.UsuarioActual;
import com.duoc.suspel.repository.ProductoSuspelRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmergenciaService {

	private final ProductoSuspelRepository repository;
	private final EmergenciaPublisher publisher;
	private final List<String> bodegas;

	public EmergenciaService(ProductoSuspelRepository repository, EmergenciaPublisher publisher,
			@Value("${suspel.inventario.bodegas}") List<String> bodegas) {
		this.repository = repository;
		this.publisher = publisher;
		this.bodegas = bodegas;
	}

	public EmergenciaResponse reportar(EmergenciaRequest request) {
		String bodega = request.bodega().trim();
		if (!bodegas.contains(bodega)) {
			throw new BodegaInvalidaException("La bodega '" + bodega + "' no existe. Bodegas habilitadas: " + bodegas);
		}

		List<EmergenciaEvento.Sustancia> sustancias = repository.findByBodegaOrderByNombre(bodega).stream()
				.map(p -> new EmergenciaEvento.Sustancia(p.getNombre(), p.getImo(), p.getCantidad()))
				.toList();

		TipoEmergencia tipo = request.tipo();
		EmergenciaEvento evento = new EmergenciaEvento(UUID.randomUUID().toString(), tipo, tipo.prioridad(), bodega,
				request.detalle(), sustancias, UsuarioActual.nombre(), Instant.now());
		publisher.publicar(evento);

		return new EmergenciaResponse(evento.eventoId(), bodega, tipo, tipo.prioridad(), sustancias.size(),
				"Aviso enviado a la cola de emergencias");
	}
}
