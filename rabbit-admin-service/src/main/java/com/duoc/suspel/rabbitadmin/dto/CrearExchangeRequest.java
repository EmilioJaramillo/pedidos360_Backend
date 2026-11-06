package com.duoc.suspel.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CrearExchangeRequest(
		@NotBlank(message = "es obligatorio")
		@Pattern(regexp = ReglasValidacion.NOMBRE, message = ReglasValidacion.NOMBRE_MENSAJE)
		String nombre,

		@NotNull(message = "es obligatorio (DIRECT, TOPIC o FANOUT)")
		TipoExchange tipo,

		Boolean durable) {

	public boolean esDurable() {
		return durable == null || durable;
	}
}
