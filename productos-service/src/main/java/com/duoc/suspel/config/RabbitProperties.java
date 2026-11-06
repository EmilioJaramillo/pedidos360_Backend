package com.duoc.suspel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "suspel.rabbit")
public record RabbitProperties(String tipoMensaje, Exchanges exchanges, RoutingKeys routingKeys) {

	public record Exchanges(String inventario, String operaciones) {
	}

	public record RoutingKeys(
			String productoCreado,
			String productoActualizado,
			String productoEliminado,
			String stockBajo,
			String documentoFicha) {
	}
}
