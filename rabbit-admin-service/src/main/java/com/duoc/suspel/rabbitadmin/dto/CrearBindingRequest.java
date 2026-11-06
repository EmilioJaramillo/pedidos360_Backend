package com.duoc.suspel.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CrearBindingRequest(
		@NotBlank(message = "es obligatorio")
		@Pattern(regexp = ReglasValidacion.NOMBRE, message = ReglasValidacion.NOMBRE_MENSAJE)
		String exchange,

		@NotBlank(message = "es obligatorio")
		@Pattern(regexp = ReglasValidacion.NOMBRE, message = ReglasValidacion.NOMBRE_MENSAJE)
		String cola,

		@NotNull(message = "es obligatorio (puede ser vacio en exchanges fanout)")
		@Pattern(regexp = ReglasValidacion.ROUTING_KEY, message = ReglasValidacion.ROUTING_KEY_MENSAJE)
		String routingKey) {
}
