package com.duoc.suspel.rabbitadmin.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ErrorResponse> cuerpoInvalido(MethodArgumentNotValidException ex) {
		Map<String, String> campos = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> campos.putIfAbsent(error.getField(), error.getDefaultMessage()));
		return responder(HttpStatus.BAD_REQUEST, "Datos de entrada invalidos", campos);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	ResponseEntity<ErrorResponse> parametroInvalido(HandlerMethodValidationException ex) {
		Map<String, String> campos = new LinkedHashMap<>();
		ex.getParameterValidationResults().forEach(resultado -> campos.putIfAbsent(
				resultado.getMethodParameter().getParameterName(),
				resultado.getResolvableErrors().get(0).getDefaultMessage()));
		return responder(HttpStatus.BAD_REQUEST, "Parametros invalidos", campos);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	ResponseEntity<ErrorResponse> parametroFaltante(MissingServletRequestParameterException ex) {
		return responder(HttpStatus.BAD_REQUEST, "Parametros invalidos", Map.of(ex.getParameterName(), "es obligatorio"));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	ResponseEntity<ErrorResponse> jsonInvalido(HttpMessageNotReadableException ex) {
		return responder(HttpStatus.BAD_REQUEST,
				"El cuerpo no es un JSON valido o contiene valores no permitidos (tipo: DIRECT, TOPIC o FANOUT)",
				Map.of());
	}

	@ExceptionHandler(RecursoNoEncontradoException.class)
	ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException ex) {
		return responder(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
	}

	@ExceptionHandler(RecursoEnConflictoException.class)
	ResponseEntity<ErrorResponse> conflicto(RecursoEnConflictoException ex) {
		return responder(HttpStatus.CONFLICT, ex.getMessage(), Map.of());
	}

	@ExceptionHandler({ AmqpConnectException.class, ResourceAccessException.class })
	ResponseEntity<ErrorResponse> brokerNoDisponible(RuntimeException ex) {
		log.error("RabbitMQ no disponible: {}", ex.getMessage());
		return responder(HttpStatus.SERVICE_UNAVAILABLE, "RabbitMQ no esta disponible", Map.of());
	}

	@ExceptionHandler(AmqpException.class)
	ResponseEntity<ErrorResponse> rechazadoPorBroker(AmqpException ex) {
		log.error("RabbitMQ rechazo la operacion: {}", ex.getMessage());
		return responder(HttpStatus.CONFLICT, "RabbitMQ rechazo la operacion", Map.of());
	}

	private ResponseEntity<ErrorResponse> responder(HttpStatus status, String mensaje, Map<String, String> campos) {
		return ResponseEntity.status(status)
				.body(ErrorResponse.de(status.value(), status.getReasonPhrase(), mensaje, campos));
	}
}
