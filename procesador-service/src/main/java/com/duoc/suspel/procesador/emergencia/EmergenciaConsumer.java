package com.duoc.suspel.procesador.emergencia;

import com.duoc.suspel.procesador.messaging.EmergenciaEvento;
import com.duoc.suspel.procesador.messaging.ManejadorMensajes;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Consume q.emergencias (suspel.emergencias.topic, emergencia.#); la cola entrega primero los de mayor prioridad. */
@Component
public class EmergenciaConsumer {

	private final ManejadorMensajes manejador;
	private final EmergenciaService service;

	public EmergenciaConsumer(ManejadorMensajes manejador, EmergenciaService service) {
		this.manejador = manejador;
		this.service = service;
	}

	@RabbitListener(queues = "${suspel.rabbit.queues.emergencias}")
	public void recibir(Message message, Channel channel) throws IOException {
		manejador.procesar(message, channel, EmergenciaEvento.class, service::despachar);
	}
}
