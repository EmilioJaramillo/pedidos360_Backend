package com.duoc.suspel.messaging;

import com.duoc.suspel.config.RabbitProperties;
import com.duoc.suspel.config.UsuarioActual;
import com.duoc.suspel.entity.ProductoSuspel;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitInventarioEventPublisher implements InventarioEventPublisher {

	private static final Logger log = LoggerFactory.getLogger(RabbitInventarioEventPublisher.class);

	private final RabbitTemplate rabbitTemplate;
	private final RabbitProperties props;

	public RabbitInventarioEventPublisher(RabbitTemplate rabbitTemplate, RabbitProperties props) {
		this.rabbitTemplate = rabbitTemplate;
		this.props = props;
	}

	@Override
	public void publicar(TipoEvento tipo, ProductoSuspel producto) {
		ProductoEvento evento = new ProductoEvento(UUID.randomUUID().toString(), tipo, producto.getId(),
				producto.getNombre(), producto.getImo(), producto.getBodega(), producto.getCantidad(),
				UsuarioActual.nombre(), Instant.now());
		String exchange = exchangePara(tipo);
		String routingKey = routingKeyPara(tipo);
		try {
			rabbitTemplate.convertAndSend(exchange, routingKey, evento, mensaje -> {
				mensaje.getMessageProperties().setMessageId(evento.eventoId());
				return mensaje;
			});
			log.info("Evento publicado {} -> {} [{}] producto={}", tipo, exchange, routingKey, producto.getId());
		}
		catch (AmqpException ex) {
			// La operacion de inventario ya se guardo; la mensajeria no debe revertirla.
			log.error("No se pudo publicar {} del producto {}: {}", tipo, producto.getId(), ex.getMessage());
		}
	}

	private String exchangePara(TipoEvento tipo) {
		return switch (tipo) {
			case PRODUCTO_CREADO, PRODUCTO_ACTUALIZADO, PRODUCTO_ELIMINADO -> props.exchanges().inventario();
			case STOCK_BAJO, FICHA_SOLICITADA -> props.exchanges().operaciones();
		};
	}

	private String routingKeyPara(TipoEvento tipo) {
		RabbitProperties.RoutingKeys keys = props.routingKeys();
		return switch (tipo) {
			case PRODUCTO_CREADO -> keys.productoCreado();
			case PRODUCTO_ACTUALIZADO -> keys.productoActualizado();
			case PRODUCTO_ELIMINADO -> keys.productoEliminado();
			case STOCK_BAJO -> keys.stockBajo();
			case FICHA_SOLICITADA -> keys.documentoFicha();
		};
	}
}
