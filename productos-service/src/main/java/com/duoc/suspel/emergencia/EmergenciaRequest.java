package com.duoc.suspel.emergencia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmergenciaRequest(
		@NotBlank(message = "es obligatoria")
		String bodega,

		@NotNull(message = "es obligatorio (INCENDIO, FUGA_GAS o DERRAME)")
		TipoEmergencia tipo,

		@Size(max = 500, message = "no puede superar 500 caracteres")
		String detalle) {
}
