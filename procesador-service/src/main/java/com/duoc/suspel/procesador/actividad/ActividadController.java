package com.duoc.suspel.procesador.actividad;

import com.duoc.suspel.procesador.auditoria.AuditoriaService;
import com.duoc.suspel.procesador.auditoria.RegistroAuditoria;
import com.duoc.suspel.procesador.documento.FichaAlmacenamiento;
import com.duoc.suspel.procesador.documento.FichaService;
import com.duoc.suspel.procesador.notificacion.Notificacion;
import com.duoc.suspel.procesador.notificacion.NotificacionService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/actividad")
public class ActividadController {

	private final AuditoriaService auditoriaService;
	private final NotificacionService notificacionService;
	private final FichaService fichaService;

	public ActividadController(AuditoriaService auditoriaService, NotificacionService notificacionService,
			FichaService fichaService) {
		this.auditoriaService = auditoriaService;
		this.notificacionService = notificacionService;
		this.fichaService = fichaService;
	}

	@GetMapping("/auditoria")
	public List<RegistroAuditoria> auditoria() {
		return auditoriaService.listar();
	}

	@GetMapping("/notificaciones")
	public List<Notificacion> notificaciones() {
		return notificacionService.listar();
	}

	@GetMapping("/fichas")
	public List<FichaAlmacenamiento> fichas() {
		return fichaService.listar();
	}
}
