package com.duoc.suspel.procesador.auditoria;

import com.duoc.suspel.procesador.messaging.ProductoEvento;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

	private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);

	private final RegistroAuditoriaRepository repository;

	public AuditoriaService(RegistroAuditoriaRepository repository) {
		this.repository = repository;
	}

	public void registrar(ProductoEvento evento) {
		if (repository.existsByEventoId(evento.eventoId())) {
			log.info("Evento {} ya registrado, se omite", evento.eventoId());
			return;
		}
		repository.save(new RegistroAuditoria(evento.eventoId(), evento.tipo().name(), evento.productoId(),
				evento.nombre(), evento.usuario(), evento.fecha()));
	}

	public List<RegistroAuditoria> listar() {
		return repository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}
}
