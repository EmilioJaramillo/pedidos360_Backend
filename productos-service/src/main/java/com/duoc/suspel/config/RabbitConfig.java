package com.duoc.suspel.config;

import com.duoc.suspel.messaging.ProductoEvento;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exchanges en los que publica productos-service. Las colas y bindings los declara
 * procesador-service, que es quien las consume.
 */
@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitConfig {

	/** Eventos de ciclo de vida del producto: producto.creado | producto.actualizado | producto.eliminado. */
	@Bean
	TopicExchange inventarioExchange(RabbitProperties props) {
		return ExchangeBuilder.topicExchange(props.exchanges().inventario()).durable(true).build();
	}

	/** Tareas puntuales: stock.bajo (notificacion) y documento.ficha (generacion de ficha). */
	@Bean
	DirectExchange operacionesExchange(RabbitProperties props) {
		return ExchangeBuilder.directExchange(props.exchanges().operaciones()).durable(true).build();
	}

	@Bean
	MessageConverter messageConverter(ObjectMapper objectMapper, RabbitProperties props) {
		DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
		typeMapper.setIdClassMapping(Map.of(props.tipoMensaje(), ProductoEvento.class));
		Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
		converter.setJavaTypeMapper(typeMapper);
		return converter;
	}
}
