package com.duoc.suspel.config;

import com.duoc.suspel.emergencia.TipoEmergencia;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "suspel.rabbit")
public record RabbitProperties(
		String tipoMensaje,
		String tipoMensajeEmergencia,
		Exchanges exchanges,
		RoutingKeys routingKeys,
		Map<TipoEmergencia, String> routingKeysEmergencia) {

	public record Exchanges(String inventario, String operaciones, String emergencias) {
	}

	public record RoutingKeys(
			String productoCreado,
			String productoActualizado,
			String productoEliminado,
			String stockBajo,
			String documentoFicha) {
	}
}
