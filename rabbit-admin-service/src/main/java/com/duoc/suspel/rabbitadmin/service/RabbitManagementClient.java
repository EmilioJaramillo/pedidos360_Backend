package com.duoc.suspel.rabbitadmin.service;

import com.duoc.suspel.rabbitadmin.config.RabbitAdminProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/** Lecturas sobre la API HTTP de administracion de RabbitMQ (AmqpAdmin no permite listar recursos). */
@Component
class RabbitManagementClient {

	record ColaJson(String name, boolean durable, Integer messages, Integer consumers, Map<String, Object> arguments) {
	}

	record ExchangeJson(String name, String type, boolean durable) {
	}

	record BindingJson(
			String source,
			String destination,
			@JsonProperty("destination_type") String destinationType,
			@JsonProperty("routing_key") String routingKey) {
	}

	private final RestClient restClient;
	private final String vhost;

	RabbitManagementClient(RestClient rabbitManagementRestClient, RabbitAdminProperties props) {
		this.restClient = rabbitManagementRestClient;
		this.vhost = props.vhost();
	}

	List<ColaJson> colas() {
		return restClient.get().uri("/queues/{vhost}", vhost).retrieve()
				.body(new ParameterizedTypeReference<List<ColaJson>>() {
				});
	}

	Optional<ColaJson> cola(String nombre) {
		try {
			return Optional.ofNullable(
					restClient.get().uri("/queues/{vhost}/{nombre}", vhost, nombre).retrieve().body(ColaJson.class));
		}
		catch (HttpClientErrorException.NotFound ex) {
			return Optional.empty();
		}
	}

	List<ExchangeJson> exchanges() {
		return restClient.get().uri("/exchanges/{vhost}", vhost).retrieve()
				.body(new ParameterizedTypeReference<List<ExchangeJson>>() {
				});
	}

	Optional<ExchangeJson> exchange(String nombre) {
		try {
			return Optional.ofNullable(restClient.get().uri("/exchanges/{vhost}/{nombre}", vhost, nombre).retrieve()
					.body(ExchangeJson.class));
		}
		catch (HttpClientErrorException.NotFound ex) {
			return Optional.empty();
		}
	}

	List<BindingJson> bindings() {
		return restClient.get().uri("/bindings/{vhost}", vhost).retrieve()
				.body(new ParameterizedTypeReference<List<BindingJson>>() {
				});
	}
}
