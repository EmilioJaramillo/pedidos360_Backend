package com.duoc.suspel.procesador.messaging;

import com.duoc.suspel.procesador.config.RabbitProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.stereotype.Component;

/**
 * Politica de confirmacion comun a todos los consumidores (acknowledge-mode: manual).
 *
 * <ul>
 * <li>Procesado correctamente: ACK.</li>
 * <li>Mensaje ilegible o invalido ({@link MensajeInvalidoException}): NACK sin requeue, va a la DLQ.</li>
 * <li>Error recuperable ({@link ErrorRecuperableException}, {@link TransientDataAccessException}): se republica
 * en la misma cola con el header x-intentos incrementado y se hace ACK del original. Al llegar a
 * max-intentos: NACK sin requeue, va a la DLQ.</li>
 * <li>Cualquier otro error: NACK sin requeue, va a la DLQ.</li>
 * </ul>
 */
@Component
public class ManejadorMensajes {

	public static final String HEADER_INTENTOS = "x-intentos";

	private static final Logger log = LoggerFactory.getLogger(ManejadorMensajes.class);

	private final ObjectMapper objectMapper;
	private final RabbitTemplate rabbitTemplate;
	private final int maxIntentos;

	public ManejadorMensajes(ObjectMapper objectMapper, RabbitTemplate rabbitTemplate, RabbitProperties props) {
		this.objectMapper = objectMapper;
		this.rabbitTemplate = rabbitTemplate;
		this.maxIntentos = props.maxIntentos();
	}

	public void procesar(Message message, Channel channel, Consumer<ProductoEvento> accion) throws IOException {
		MessageProperties propiedades = message.getMessageProperties();
		long tag = propiedades.getDeliveryTag();
		String cola = propiedades.getConsumerQueue();
		int intento = intentoActual(propiedades);

		try {
			ProductoEvento evento = leer(message);
			accion.accept(evento);
			channel.basicAck(tag, false);
			log.info("[{}] ACK evento={} tipo={} intento={}", cola, evento.eventoId(), evento.tipo(), intento);
		}
		catch (MensajeInvalidoException ex) {
			enviarADlq(channel, tag, cola, propiedades, "mensaje invalido: " + ex.getMessage());
		}
		catch (ErrorRecuperableException | TransientDataAccessException ex) {
			if (intento < maxIntentos) {
				propiedades.setHeader(HEADER_INTENTOS, intento + 1);
				rabbitTemplate.send("", cola, message);
				channel.basicAck(tag, false);
				log.warn("[{}] Error recuperable, reintento {}/{} id={}: {}", cola, intento + 1, maxIntentos,
						propiedades.getMessageId(), ex.getMessage());
			}
			else {
				enviarADlq(channel, tag, cola, propiedades,
						"reintentos agotados (" + maxIntentos + "): " + ex.getMessage());
			}
		}
		catch (RuntimeException ex) {
			enviarADlq(channel, tag, cola, propiedades, "error no controlado: " + ex.getMessage());
		}
	}

	private ProductoEvento leer(Message message) {
		ProductoEvento evento;
		try {
			evento = objectMapper.readValue(message.getBody(), ProductoEvento.class);
		}
		catch (IOException ex) {
			throw new MensajeInvalidoException("JSON no valido");
		}
		if (evento.eventoId() == null || evento.tipo() == null || evento.productoId() == null
				|| evento.nombre() == null || evento.nombre().isBlank()) {
			throw new MensajeInvalidoException("faltan campos obligatorios (eventoId, tipo, productoId, nombre)");
		}
		return evento;
	}

	private void enviarADlq(Channel channel, long tag, String cola, MessageProperties propiedades, String motivo)
			throws IOException {
		channel.basicNack(tag, false, false);
		log.error("[{}] NACK -> DLQ id={} motivo={}", cola, propiedades.getMessageId(), motivo);
	}

	private int intentoActual(MessageProperties propiedades) {
		Object valor = propiedades.getHeaders().get(HEADER_INTENTOS);
		return valor instanceof Number n ? n.intValue() : 0;
	}
}
