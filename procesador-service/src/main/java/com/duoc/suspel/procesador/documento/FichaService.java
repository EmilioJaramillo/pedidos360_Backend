package com.duoc.suspel.procesador.documento;

import com.duoc.suspel.procesador.messaging.MensajeInvalidoException;
import com.duoc.suspel.procesador.messaging.ProductoEvento;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FichaService {

	private static final Logger log = LoggerFactory.getLogger(FichaService.class);

	private static final Pattern CLASE_IMO = Pattern.compile("Clase\\s+(\\d)(\\.\\d)?", Pattern.CASE_INSENSITIVE);

	private static final Map<String, String> INDICACIONES = Map.of(
			"2", "Cilindros asegurados en posicion vertical, zona ventilada y lejos de fuentes de calor.",
			"3", "Bodega con ventilacion y sin fuentes de ignicion; usar bandejas antiderrame.",
			"4", "Mantener seco y separado de oxidantes; evitar contacto con agua y calor.",
			"5", "Separar de combustibles e inflamables; almacenar en zona fresca.",
			"6", "Acceso restringido, envases cerrados y separados de alimentos.",
			"8", "Estanterias resistentes a corrosion, bandejas antiderrame y ducha de emergencia cercana.",
			"9", "Almacenamiento general segun hoja de datos de seguridad del fabricante.");

	private final FichaAlmacenamientoRepository repository;

	public FichaService(FichaAlmacenamientoRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public void generarFicha(ProductoEvento evento) {
		if (repository.existsByEventoId(evento.eventoId())) {
			log.info("Ficha del evento {} ya generada, se omite", evento.eventoId());
			return;
		}
		Matcher matcher = CLASE_IMO.matcher(evento.imo() == null ? "" : evento.imo());
		if (!matcher.find() || !INDICACIONES.containsKey(matcher.group(1))) {
			throw new MensajeInvalidoException("clase IMO no reconocida: " + evento.imo());
		}
		String claseImo = matcher.group(1) + (matcher.group(2) == null ? "" : matcher.group(2));
		FichaAlmacenamiento ficha = repository.save(new FichaAlmacenamiento(evento.eventoId(), evento.productoId(),
				evento.nombre(), claseImo, evento.bodega(), INDICACIONES.get(matcher.group(1)), Instant.now()));
		ficha.asignarFolio("FS-%06d".formatted(ficha.getId()));
		log.info("Ficha {} generada para {} (clase {})", ficha.getFolio(), evento.nombre(), claseImo);
	}

	public List<FichaAlmacenamiento> listar() {
		return repository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}
}
