package com.duoc.suspel.rabbitadmin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.duoc.suspel.rabbitadmin.config.CorsConfig;
import com.duoc.suspel.rabbitadmin.config.SecurityConfig;
import com.duoc.suspel.rabbitadmin.dto.ColaResponse;
import com.duoc.suspel.rabbitadmin.exception.RecursoEnConflictoException;
import com.duoc.suspel.rabbitadmin.service.RabbitAdminService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(ColaController.class)
@Import({ SecurityConfig.class, CorsConfig.class })
class ColaControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private RabbitAdminService service;

	@MockitoBean
	private JwtDecoder jwtDecoder;

	private RequestPostProcessor token() {
		return jwt().authorities(new SimpleGrantedAuthority("SCOPE_suspel-api/productos-access"));
	}

	@Test
	void sinTokenResponde401() throws Exception {
		mvc.perform(get("/api/admin/queues")).andExpect(status().isUnauthorized());
	}

	@Test
	void crearColaValidaResponde201() throws Exception {
		when(service.crearCola(any())).thenReturn(new ColaResponse("q.reportes", true, 0, 0, Map.of()));

		mvc.perform(post("/api/admin/queues").with(token()).contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"q.reportes\",\"conDlq\":true}"))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "/api/admin/queues/q.reportes"));
	}

	@Test
	void nombreVacioResponde400() throws Exception {
		mvc.perform(post("/api/admin/queues").with(token()).contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.campos.nombre").exists());
		verify(service, never()).crearCola(any());
	}

	@Test
	void nombreConFormatoInvalidoYTtlNegativoResponde400() throws Exception {
		mvc.perform(post("/api/admin/queues").with(token()).contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"Mi Cola\",\"ttlMs\":-5}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.campos.nombre").exists())
				.andExpect(jsonPath("$.campos.ttlMs").value("debe ser mayor a 0"));
	}

	@Test
	void nombreReservadoAmqResponde400() throws Exception {
		mvc.perform(post("/api/admin/queues").with(token()).contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"amq.interna\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void eliminarColaDelSistemaResponde409() throws Exception {
		doThrow(new RecursoEnConflictoException("'q.auditoria' es un recurso del sistema y no se puede eliminar"))
				.when(service).eliminarCola("q.auditoria");

		mvc.perform(delete("/api/admin/queues/q.auditoria").with(token()))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.mensaje").value("'q.auditoria' es un recurso del sistema y no se puede eliminar"));
	}

	@Test
	void eliminarConNombreInvalidoResponde400() throws Exception {
		mvc.perform(delete("/api/admin/queues/X!").with(token())).andExpect(status().isBadRequest());
		verify(service, never()).eliminarCola(any());
	}
}
