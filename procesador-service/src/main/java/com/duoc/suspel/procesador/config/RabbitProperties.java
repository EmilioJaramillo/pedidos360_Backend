package com.duoc.suspel.procesador.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "suspel.rabbit")
public record RabbitProperties(
		int maxIntentos,
		int maxPrioridad,
		Exchanges exchanges,
		Queues queues,
		RoutingKeys routingKeys) {

	public record Exchanges(String inventario, String operaciones, String emergencias, String dlx) {
	}

	public record Queues(
			String auditoria,
			String notificaciones,
			String documentos,
			String emergencias,
			String auditoriaDlq,
			String notificacionesDlq,
			String documentosDlq,
			String emergenciasDlq) {
	}

	public record RoutingKeys(
			String auditoria,
			String stockBajo,
			String documentoFicha,
			String emergencias,
			String auditoriaDlq,
			String notificacionesDlq,
			String documentosDlq,
			String emergenciasDlq) {
	}
}
