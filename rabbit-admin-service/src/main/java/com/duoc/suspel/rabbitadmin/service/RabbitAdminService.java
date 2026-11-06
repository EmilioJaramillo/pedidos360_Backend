package com.duoc.suspel.rabbitadmin.service;

import com.duoc.suspel.rabbitadmin.config.RabbitAdminProperties;
import com.duoc.suspel.rabbitadmin.dto.BindingResponse;
import com.duoc.suspel.rabbitadmin.dto.ColaResponse;
import com.duoc.suspel.rabbitadmin.dto.CrearBindingRequest;
import com.duoc.suspel.rabbitadmin.dto.CrearColaRequest;
import com.duoc.suspel.rabbitadmin.dto.CrearExchangeRequest;
import com.duoc.suspel.rabbitadmin.dto.ExchangeResponse;
import com.duoc.suspel.rabbitadmin.dto.PurgaResponse;
import com.duoc.suspel.rabbitadmin.exception.RecursoEnConflictoException;
import com.duoc.suspel.rabbitadmin.exception.RecursoNoEncontradoException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.stereotype.Service;

/** Creacion, consulta y eliminacion de colas, exchanges y bindings en RabbitMQ. */
@Service
public class RabbitAdminService {

	private static final Logger log = LoggerFactory.getLogger(RabbitAdminService.class);

	private final AmqpAdmin amqpAdmin;
	private final RabbitManagementClient management;
	private final RabbitAdminProperties props;

	RabbitAdminService(AmqpAdmin amqpAdmin, RabbitManagementClient management, RabbitAdminProperties props) {
		this.amqpAdmin = amqpAdmin;
		this.management = management;
		this.props = props;
	}

	// Colas

	public List<ColaResponse> listarColas() {
		return management.colas().stream().map(this::aColaResponse).toList();
	}

	public ColaResponse obtenerCola(String nombre) {
		return management.cola(nombre).map(this::aColaResponse)
				.orElseThrow(() -> new RecursoNoEncontradoException("La cola '" + nombre + "' no existe"));
	}

	public ColaResponse crearCola(CrearColaRequest request) {
		String nombre = request.nombre();
		validarColaInexistente(nombre);

		QueueBuilder builder = request.esDurable() ? QueueBuilder.durable(nombre) : QueueBuilder.nonDurable(nombre);
		if (request.ttlMs() != null) {
			builder.ttl(request.ttlMs());
		}
		if (request.maxMensajes() != null) {
			builder.maxLength(request.maxMensajes());
		}
		if (request.requiereDlq()) {
			String dlq = nombre + props.sufijoDlq();
			validarColaInexistente(dlq);
			DirectExchange dlx = ExchangeBuilder.directExchange(props.dlx()).durable(true).build();
			Queue colaDlq = QueueBuilder.durable(dlq).build();
			amqpAdmin.declareExchange(dlx);
			amqpAdmin.declareQueue(colaDlq);
			amqpAdmin.declareBinding(BindingBuilder.bind(colaDlq).to(dlx).with(dlq));
			builder.deadLetterExchange(props.dlx()).deadLetterRoutingKey(dlq);
		}

		Queue cola = builder.build();
		amqpAdmin.declareQueue(cola);
		log.info("Cola creada: {} argumentos={}", nombre, cola.getArguments());
		return new ColaResponse(nombre, cola.isDurable(), 0, 0, cola.getArguments());
	}

	public void eliminarCola(String nombre) {
		validarNoProtegido(nombre);
		obtenerCola(nombre);
		amqpAdmin.deleteQueue(nombre);
		log.info("Cola eliminada: {}", nombre);
	}

	public PurgaResponse purgarCola(String nombre) {
		obtenerCola(nombre);
		int eliminados = amqpAdmin.purgeQueue(nombre);
		log.info("Cola purgada: {} ({} mensajes)", nombre, eliminados);
		return new PurgaResponse(nombre, eliminados);
	}

	// Exchanges

	public List<ExchangeResponse> listarExchanges() {
		return management.exchanges().stream()
				.filter(e -> !e.name().isBlank() && !e.name().startsWith("amq."))
				.map(e -> new ExchangeResponse(e.name(), e.type(), e.durable()))
				.toList();
	}

	public ExchangeResponse crearExchange(CrearExchangeRequest request) {
		if (management.exchange(request.nombre()).isPresent()) {
			throw new RecursoEnConflictoException("El exchange '" + request.nombre() + "' ya existe");
		}
		Exchange exchange = new ExchangeBuilder(request.nombre(), request.tipo().amqp())
				.durable(request.esDurable())
				.build();
		amqpAdmin.declareExchange(exchange);
		log.info("Exchange creado: {} ({})", exchange.getName(), exchange.getType());
		return new ExchangeResponse(exchange.getName(), exchange.getType(), exchange.isDurable());
	}

	public void eliminarExchange(String nombre) {
		validarNoProtegido(nombre);
		validarExchangeExiste(nombre);
		amqpAdmin.deleteExchange(nombre);
		log.info("Exchange eliminado: {}", nombre);
	}

	// Bindings

	public List<BindingResponse> listarBindings() {
		return management.bindings().stream()
				.filter(b -> !b.source().isBlank() && "queue".equals(b.destinationType()))
				.map(b -> new BindingResponse(b.source(), b.destination(), b.routingKey()))
				.toList();
	}

	public BindingResponse crearBinding(CrearBindingRequest request) {
		validarExchangeExiste(request.exchange());
		obtenerCola(request.cola());
		if (existeBinding(request.exchange(), request.cola(), request.routingKey())) {
			throw new RecursoEnConflictoException("El binding ya existe");
		}
		amqpAdmin.declareBinding(new Binding(request.cola(), Binding.DestinationType.QUEUE, request.exchange(),
				request.routingKey(), Map.of()));
		log.info("Binding creado: {} -[{}]-> {}", request.exchange(), request.routingKey(), request.cola());
		return new BindingResponse(request.exchange(), request.cola(), request.routingKey());
	}

	public void eliminarBinding(String exchange, String cola, String routingKey) {
		if (props.esProtegido(cola) && props.esProtegido(exchange)) {
			throw new RecursoEnConflictoException("El binding entre recursos del sistema no se puede eliminar");
		}
		if (!existeBinding(exchange, cola, routingKey)) {
			throw new RecursoNoEncontradoException(
					"No existe el binding " + exchange + " -[" + routingKey + "]-> " + cola);
		}
		amqpAdmin.removeBinding(new Binding(cola, Binding.DestinationType.QUEUE, exchange, routingKey, Map.of()));
		log.info("Binding eliminado: {} -[{}]-> {}", exchange, routingKey, cola);
	}

	private boolean existeBinding(String exchange, String cola, String routingKey) {
		return listarBindings().stream().anyMatch(b -> b.exchange().equals(exchange) && b.cola().equals(cola)
				&& b.routingKey().equals(routingKey));
	}

	private void validarColaInexistente(String nombre) {
		if (management.cola(nombre).isPresent()) {
			throw new RecursoEnConflictoException("La cola '" + nombre + "' ya existe");
		}
	}

	private void validarExchangeExiste(String nombre) {
		if (management.exchange(nombre).isEmpty()) {
			throw new RecursoNoEncontradoException("El exchange '" + nombre + "' no existe");
		}
	}

	private void validarNoProtegido(String nombre) {
		if (props.esProtegido(nombre)) {
			throw new RecursoEnConflictoException("'" + nombre + "' es un recurso del sistema y no se puede eliminar");
		}
	}

	private ColaResponse aColaResponse(RabbitManagementClient.ColaJson cola) {
		return new ColaResponse(cola.name(), cola.durable(), valor(cola.messages()), valor(cola.consumers()),
				cola.arguments() == null ? Map.of() : cola.arguments());
	}

	private int valor(Integer numero) {
		return numero == null ? 0 : numero;
	}
}
