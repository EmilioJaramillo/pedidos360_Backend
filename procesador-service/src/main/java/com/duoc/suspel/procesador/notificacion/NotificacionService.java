package com.duoc.suspel.procesador.notificacion;

import com.duoc.suspel.procesador.messaging.ErrorRecuperableException;
import com.duoc.suspel.procesador.messaging.ProductoEvento;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

	private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

	private final NotificacionRepository repository;
	private final boolean canalDisponible;

	public NotificacionService(NotificacionRepository repository,
			@Value("${suspel.notificaciones.canal-disponible}") boolean canalDisponible) {
		this.repository = repository;
		this.canalDisponible = canalDisponible;
	}

	public void notificarStockBajo(ProductoEvento evento) {
		if (!canalDisponible) {
			throw new ErrorRecuperableException("canal de notificaciones no disponible");
		}
		if (repository.existsByEventoId(evento.eventoId())) {
			log.info("Notificacion del evento {} ya enviada, se omite", evento.eventoId());
			return;
		}
		String mensaje = "Stock bajo: %s tiene %d unidades en %s".formatted(evento.nombre(), evento.cantidad(),
				evento.bodega());
		repository.save(new Notificacion(evento.eventoId(), evento.productoId(), evento.nombre(), evento.bodega(),
				evento.cantidad(), mensaje, Instant.now()));
		log.warn(mensaje);
	}

	public List<Notificacion> listar() {
		return repository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}
}
