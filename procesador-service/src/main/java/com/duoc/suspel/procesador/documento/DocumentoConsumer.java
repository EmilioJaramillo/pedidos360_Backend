package com.duoc.suspel.procesador.documento;

import com.duoc.suspel.procesador.messaging.ManejadorMensajes;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Consume q.documentos (suspel.operaciones.direct, documento.ficha) y genera la ficha de almacenamiento. */
@Component
public class DocumentoConsumer {

	private final ManejadorMensajes manejador;
	private final FichaService service;

	public DocumentoConsumer(ManejadorMensajes manejador, FichaService service) {
		this.manejador = manejador;
		this.service = service;
	}

	@RabbitListener(queues = "${suspel.rabbit.queues.documentos}")
	public void recibir(Message message, Channel channel) throws IOException {
		manejador.procesar(message, channel, service::generarFicha);
	}
}
