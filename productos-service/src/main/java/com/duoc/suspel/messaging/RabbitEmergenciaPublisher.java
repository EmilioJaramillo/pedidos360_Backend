package com.duoc.suspel.messaging;

import com.duoc.suspel.config.RabbitProperties;
import com.duoc.suspel.emergencia.EmergenciaEvento;
import com.duoc.suspel.emergencia.EmergenciaPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitEmergenciaPublisher implements EmergenciaPublisher {

	private static final Logger log = LoggerFactory.getLogger(RabbitEmergenciaPublisher.class);

	private final RabbitTemplate rabbitTemplate;
	private final RabbitProperties props;

	public RabbitEmergenciaPublisher(RabbitTemplate rabbitTemplate, RabbitProperties props) {
		this.rabbitTemplate = rabbitTemplate;
		this.props = props;
	}

	@Override
	public void publicar(EmergenciaEvento evento) {
		String exchange = props.exchanges().emergencias();
		String routingKey = props.routingKeysEmergencia().get(evento.tipo());
		rabbitTemplate.convertAndSend(exchange, routingKey, evento, mensaje -> {
			mensaje.getMessageProperties().setMessageId(evento.eventoId());
			mensaje.getMessageProperties().setPriority(evento.prioridad());
			return mensaje;
		});
		log.warn("Emergencia publicada {} -> {} [{}] bodega={} prioridad={}", evento.tipo(), exchange, routingKey,
				evento.bodega(), evento.prioridad());
	}
}
