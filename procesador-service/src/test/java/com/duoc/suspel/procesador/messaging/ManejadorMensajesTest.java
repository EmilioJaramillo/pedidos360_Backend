package com.duoc.suspel.procesador.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.duoc.suspel.procesador.config.RabbitProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

class ManejadorMensajesTest {

	private static final String EVENTO = """
			{"eventoId":"e-1","tipo":"STOCK_BAJO","productoId":1,"nombre":"Propano",
			 "imo":"Clase 2.1 - Gas Inflamable","bodega":"Bodega A-01","cantidad":120,
			 "usuario":"demo","fecha":"2026-11-06T12:00:00Z"}
			""";

	private Channel channel;
	private RabbitTemplate rabbitTemplate;
	private ManejadorMensajes manejador;

	@BeforeEach
	void setUp() {
		channel = mock(Channel.class);
		rabbitTemplate = mock(RabbitTemplate.class);
		RabbitProperties props = new RabbitProperties(3, 10, null, null, null);
		manejador = new ManejadorMensajes(new ObjectMapper().registerModule(new JavaTimeModule()), rabbitTemplate,
				props);
	}

	@Test
	void procesadoCorrectamenteHaceAck() throws Exception {
		manejador.procesar(mensaje(EVENTO, 0), channel, evento -> assertThat(evento.nombre()).isEqualTo("Propano"));

		verify(channel).basicAck(7L, false);
		verify(channel, never()).basicNack(anyLong(), eq(false), eq(false));
	}

	@Test
	void jsonInvalidoVaDirectoADlq() throws Exception {
		manejador.procesar(mensaje("no es json", 0), channel, evento -> {
		});

		verify(channel).basicNack(7L, false, false);
		verify(rabbitTemplate, never()).send(any(), any(), any(Message.class));
	}

	@Test
	void errorRecuperableSeReintentaConIntentoIncrementado() throws Exception {
		Message mensaje = mensaje(EVENTO, 1);

		manejador.procesar(mensaje, channel, evento -> {
			throw new ErrorRecuperableException("canal caido");
		});

		verify(rabbitTemplate).send("", "q.notificaciones", mensaje);
		verify(channel).basicAck(7L, false);
		assertThat(mensaje.getMessageProperties().<Integer>getHeader(ManejadorMensajes.HEADER_INTENTOS)).isEqualTo(2);
	}

	@Test
	void reintentosAgotadosVaADlq() throws Exception {
		manejador.procesar(mensaje(EVENTO, 3), channel, evento -> {
			throw new ErrorRecuperableException("canal caido");
		});

		verify(channel).basicNack(7L, false, false);
		verify(rabbitTemplate, never()).send(any(), any(), any(Message.class));
	}

	private Message mensaje(String cuerpo, int intentos) {
		MessageProperties props = new MessageProperties();
		props.setDeliveryTag(7L);
		props.setConsumerQueue("q.notificaciones");
		props.setHeader(ManejadorMensajes.HEADER_INTENTOS, intentos);
		return new Message(cuerpo.getBytes(StandardCharsets.UTF_8), props);
	}
}
