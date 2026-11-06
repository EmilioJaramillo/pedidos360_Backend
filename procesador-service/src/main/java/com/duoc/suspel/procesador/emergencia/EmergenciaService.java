package com.duoc.suspel.procesador.emergencia;

import com.duoc.suspel.procesador.messaging.EmergenciaEvento;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Despacho simulado del aviso a Bomberos: registra el aviso con folio y deja en el log los datos que
 * necesita la central (bodega, tipo y sustancias peligrosas presentes).
 */
@Service
public class EmergenciaService {

	private static final Logger log = LoggerFactory.getLogger(EmergenciaService.class);

	private final AvisoEmergenciaRepository repository;
	private final String destino;

	public EmergenciaService(AvisoEmergenciaRepository repository,
			@Value("${suspel.emergencias.destino}") String destino) {
		this.repository = repository;
		this.destino = destino;
	}

	@Transactional
	public void despachar(EmergenciaEvento evento) {
		if (repository.existsByEventoId(evento.eventoId())) {
			log.info("Emergencia {} ya despachada, se omite", evento.eventoId());
			return;
		}
		List<AvisoEmergencia.SustanciaAviso> sustancias = evento.sustancias() == null ? List.of()
				: evento.sustancias().stream()
						.map(s -> new AvisoEmergencia.SustanciaAviso(s.nombre(), s.imo(), s.cantidad()))
						.toList();
		AvisoEmergencia aviso = repository.save(new AvisoEmergencia(evento.eventoId(), evento.tipo(),
				evento.prioridad(), evento.bodega(), evento.detalle(), evento.usuario(), destino, evento.fecha(),
				sustancias));
		aviso.asignarFolio("EM-%06d".formatted(aviso.getId()));
		log.warn("AVISO A {} {} | {} en {} | prioridad {} | {} sustancias peligrosas", destino, aviso.getFolio(),
				evento.tipo(), evento.bodega(), evento.prioridad(), sustancias.size());
	}

	public List<AvisoEmergencia> listar() {
		return repository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}
}
