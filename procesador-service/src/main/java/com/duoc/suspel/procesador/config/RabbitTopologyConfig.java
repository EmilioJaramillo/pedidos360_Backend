package com.duoc.suspel.procesador.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia de mensajeria del procesador.
 *
 * <pre>
 * suspel.inventario.topic   --producto.#-------> q.auditoria       --(dlx) auditoria.dlq------> q.auditoria.dlq
 * suspel.operaciones.direct --stock.bajo-------> q.notificaciones  --(dlx) notificaciones.dlq-> q.notificaciones.dlq
 * suspel.operaciones.direct --documento.ficha--> q.documentos      --(dlx) documentos.dlq-----> q.documentos.dlq
 * suspel.emergencias.topic  --emergencia.#-----> q.emergencias     --(dlx) emergencias.dlq----> q.emergencias.dlq
 * </pre>
 *
 * Los mensajes rechazados con NACK (requeue=false) se reenvian por suspel.dlx a la DLQ de su cola.
 */
@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitTopologyConfig {

	private final RabbitProperties props;

	public RabbitTopologyConfig(RabbitProperties props) {
		this.props = props;
	}

	@Bean
	TopicExchange inventarioExchange() {
		return ExchangeBuilder.topicExchange(props.exchanges().inventario()).durable(true).build();
	}

	@Bean
	DirectExchange operacionesExchange() {
		return ExchangeBuilder.directExchange(props.exchanges().operaciones()).durable(true).build();
	}

	@Bean
	DirectExchange dlxExchange() {
		return ExchangeBuilder.directExchange(props.exchanges().dlx()).durable(true).build();
	}

	// Auditoria: todos los eventos de ciclo de vida del producto.

	@Bean
	Queue auditoriaQueue() {
		return colaConDlq(props.queues().auditoria(), props.routingKeys().auditoriaDlq());
	}

	@Bean
	Queue auditoriaDlq() {
		return QueueBuilder.durable(props.queues().auditoriaDlq()).build();
	}

	@Bean
	Binding auditoriaBinding() {
		return BindingBuilder.bind(auditoriaQueue()).to(inventarioExchange()).with(props.routingKeys().auditoria());
	}

	@Bean
	Binding auditoriaDlqBinding() {
		return BindingBuilder.bind(auditoriaDlq()).to(dlxExchange()).with(props.routingKeys().auditoriaDlq());
	}

	// Notificaciones: alertas de stock bajo.

	@Bean
	Queue notificacionesQueue() {
		return colaConDlq(props.queues().notificaciones(), props.routingKeys().notificacionesDlq());
	}

	@Bean
	Queue notificacionesDlq() {
		return QueueBuilder.durable(props.queues().notificacionesDlq()).build();
	}

	@Bean
	Binding notificacionesBinding() {
		return BindingBuilder.bind(notificacionesQueue()).to(operacionesExchange())
				.with(props.routingKeys().stockBajo());
	}

	@Bean
	Binding notificacionesDlqBinding() {
		return BindingBuilder.bind(notificacionesDlq()).to(dlxExchange())
				.with(props.routingKeys().notificacionesDlq());
	}

	// Documentos: generacion de fichas de almacenamiento.

	@Bean
	Queue documentosQueue() {
		return colaConDlq(props.queues().documentos(), props.routingKeys().documentosDlq());
	}

	@Bean
	Queue documentosDlq() {
		return QueueBuilder.durable(props.queues().documentosDlq()).build();
	}

	@Bean
	Binding documentosBinding() {
		return BindingBuilder.bind(documentosQueue()).to(operacionesExchange())
				.with(props.routingKeys().documentoFicha());
	}

	@Bean
	Binding documentosDlqBinding() {
		return BindingBuilder.bind(documentosDlq()).to(dlxExchange()).with(props.routingKeys().documentosDlq());
	}

	// Emergencias: avisos a Bomberos con prioridad (incendio 9, fuga de gas 7, derrame 5).

	@Bean
	TopicExchange emergenciasExchange() {
		return ExchangeBuilder.topicExchange(props.exchanges().emergencias()).durable(true).build();
	}

	@Bean
	Queue emergenciasQueue() {
		return QueueBuilder.durable(props.queues().emergencias())
				.maxPriority(props.maxPrioridad())
				.deadLetterExchange(props.exchanges().dlx())
				.deadLetterRoutingKey(props.routingKeys().emergenciasDlq())
				.build();
	}

	@Bean
	Queue emergenciasDlq() {
		return QueueBuilder.durable(props.queues().emergenciasDlq()).build();
	}

	@Bean
	Binding emergenciasBinding() {
		return BindingBuilder.bind(emergenciasQueue()).to(emergenciasExchange())
				.with(props.routingKeys().emergencias());
	}

	@Bean
	Binding emergenciasDlqBinding() {
		return BindingBuilder.bind(emergenciasDlq()).to(dlxExchange()).with(props.routingKeys().emergenciasDlq());
	}

	private Queue colaConDlq(String nombre, String dlqRoutingKey) {
		return QueueBuilder.durable(nombre)
				.deadLetterExchange(props.exchanges().dlx())
				.deadLetterRoutingKey(dlqRoutingKey)
				.build();
	}
}
