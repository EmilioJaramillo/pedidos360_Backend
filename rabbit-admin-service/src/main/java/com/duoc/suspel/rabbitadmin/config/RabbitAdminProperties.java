package com.duoc.suspel.rabbitadmin.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param managementUrl      URL base de la API HTTP de administracion de RabbitMQ
 * @param vhost              virtual host administrado
 * @param dlx                exchange usado como dead letter exchange al crear colas con DLQ
 * @param sufijoDlq          sufijo del nombre de la DLQ generada (cola + sufijo)
 * @param recursosProtegidos colas y exchanges del sistema que no se pueden eliminar desde la API
 */
@ConfigurationProperties(prefix = "suspel.rabbit-admin")
public record RabbitAdminProperties(
		String managementUrl,
		String vhost,
		String dlx,
		String sufijoDlq,
		List<String> recursosProtegidos) {

	public boolean esProtegido(String nombre) {
		return recursosProtegidos != null && recursosProtegidos.contains(nombre);
	}
}
