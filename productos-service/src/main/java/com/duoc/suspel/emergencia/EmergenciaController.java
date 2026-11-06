package com.duoc.suspel.emergencia;

import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emergencias")
public class EmergenciaController {

	private static final Logger log = LoggerFactory.getLogger(EmergenciaController.class);

	private final EmergenciaService service;

	public EmergenciaController(EmergenciaService service) {
		this.service = service;
	}

	/** 202: el aviso quedo en la cola; procesador-service lo despacha de forma asincrona. */
	@PostMapping
	public ResponseEntity<EmergenciaResponse> reportar(@Valid @RequestBody EmergenciaRequest request) {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.reportar(request));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<Map<String, Object>> datosInvalidos(MethodArgumentNotValidException ex) {
		Map<String, String> campos = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> campos.putIfAbsent(error.getField(), error.getDefaultMessage()));
		return ResponseEntity.badRequest().body(Map.of("mensaje", "Datos de entrada invalidos", "campos", campos));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	ResponseEntity<Map<String, Object>> cuerpoInvalido(HttpMessageNotReadableException ex) {
		return ResponseEntity.badRequest()
				.body(Map.of("mensaje", "Cuerpo invalido; tipo debe ser INCENDIO, FUGA_GAS o DERRAME"));
	}

	@ExceptionHandler(BodegaInvalidaException.class)
	ResponseEntity<Map<String, Object>> bodegaInvalida(BodegaInvalidaException ex) {
		return ResponseEntity.badRequest().body(Map.of("mensaje", ex.getMessage()));
	}

	@ExceptionHandler(AmqpException.class)
	ResponseEntity<Map<String, Object>> brokerNoDisponible(AmqpException ex) {
		log.error("No se pudo publicar la emergencia: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(Map.of("mensaje", "No se pudo enviar el aviso. Llame directamente al 132 (Bomberos)."));
	}
}
