package com.duoc.suspel.rabbitadmin.dto;

import org.springframework.amqp.core.ExchangeTypes;

public enum TipoExchange {

	DIRECT(ExchangeTypes.DIRECT),
	TOPIC(ExchangeTypes.TOPIC),
	FANOUT(ExchangeTypes.FANOUT);

	private final String amqp;

	TipoExchange(String amqp) {
		this.amqp = amqp;
	}

	public String amqp() {
		return amqp;
	}
}
