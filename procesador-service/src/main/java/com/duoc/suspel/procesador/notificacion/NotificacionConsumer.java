package com.duoc.suspel.procesador.notificacion;

import com.duoc.suspel.procesador.messaging.ManejadorMensajes;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Consume q.notificaciones (suspel.operaciones.direct, stock.bajo) y genera alertas de stock. */
@Component
public class NotificacionConsumer {

	private final ManejadorMensajes manejador;
	private final NotificacionService service;

	public NotificacionConsumer(ManejadorMensajes manejador, NotificacionService service) {
		this.manejador = manejador;
		this.service = service;
	}

	@RabbitListener(queues = "${suspel.rabbit.queues.notificaciones}")
	public void recibir(Message message, Channel channel) throws IOException {
		manejador.procesar(message, channel, service::notificarStockBajo);
	}
}
