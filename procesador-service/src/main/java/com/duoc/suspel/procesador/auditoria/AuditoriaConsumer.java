package com.duoc.suspel.procesador.auditoria;

import com.duoc.suspel.procesador.messaging.ManejadorMensajes;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Consume q.auditoria (suspel.inventario.topic, producto.#) y guarda la bitacora de cambios. */
@Component
public class AuditoriaConsumer {

	private final ManejadorMensajes manejador;
	private final AuditoriaService service;

	public AuditoriaConsumer(ManejadorMensajes manejador, AuditoriaService service) {
		this.manejador = manejador;
		this.service = service;
	}

	@RabbitListener(queues = "${suspel.rabbit.queues.auditoria}")
	public void recibir(Message message, Channel channel) throws IOException {
		manejador.procesar(message, channel, service::registrar);
	}
}
