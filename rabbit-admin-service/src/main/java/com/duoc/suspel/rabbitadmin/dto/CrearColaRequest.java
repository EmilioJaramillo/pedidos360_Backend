package com.duoc.suspel.rabbitadmin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * @param nombre      nombre de la cola
 * @param durable     sobrevive a reinicios del broker (por defecto true)
 * @param conDlq      crea ademas la DLQ (nombre + sufijo) enlazada al dead letter exchange
 * @param ttlMs       tiempo de vida de los mensajes en milisegundos (opcional)
 * @param maxMensajes cantidad maxima de mensajes en la cola (opcional)
 */
public record CrearColaRequest(
		@NotBlank(message = "es obligatorio")
		@Pattern(regexp = ReglasValidacion.NOMBRE, message = ReglasValidacion.NOMBRE_MENSAJE)
		String nombre,

		Boolean durable,

		Boolean conDlq,

		@Positive(message = "debe ser mayor a 0")
		@Max(value = 86_400_000, message = "no puede superar 86400000 (24 horas)")
		Integer ttlMs,

		@Positive(message = "debe ser mayor a 0")
		@Max(value = 1_000_000, message = "no puede superar 1000000")
		Integer maxMensajes) {

	public boolean esDurable() {
		return durable == null || durable;
	}

	public boolean requiereDlq() {
		return Boolean.TRUE.equals(conDlq);
	}
}
