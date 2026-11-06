package com.duoc.suspel.rabbitadmin.config;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(RabbitAdminProperties.class)
public class RabbitManagementConfig {

	@Bean
	RestClient rabbitManagementRestClient(RestClient.Builder builder, RabbitAdminProperties adminProps,
			RabbitProperties rabbitProps) {
		return builder
				.baseUrl(adminProps.managementUrl())
				.defaultHeaders(headers -> headers.setBasicAuth(rabbitProps.getUsername(), rabbitProps.getPassword()))
				.build();
	}
}
