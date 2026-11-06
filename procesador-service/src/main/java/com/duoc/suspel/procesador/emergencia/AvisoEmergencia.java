package com.duoc.suspel.procesador.emergencia;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AvisoEmergencia {

	@Embeddable
	public static class SustanciaAviso {

		private String nombre;
		private String imo;
		private Integer cantidad;

		protected SustanciaAviso() {
		}

		public SustanciaAviso(String nombre, String imo, Integer cantidad) {
			this.nombre = nombre;
			this.imo = imo;
			this.cantidad = cantidad;
		}

		public String getNombre() {
			return nombre;
		}

		public String getImo() {
			return imo;
		}

		public Integer getCantidad() {
			return cantidad;
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String eventoId;

	private String folio;
	private String tipo;
	private int prioridad;
	private String bodega;
	private String detalle;
	private String usuario;
	private String destino;
	private Instant fechaReporte;
	private Instant fechaDespacho;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "aviso_emergencia_sustancia")
	private List<SustanciaAviso> sustancias = new ArrayList<>();

	protected AvisoEmergencia() {
	}

	public AvisoEmergencia(String eventoId, String tipo, int prioridad, String bodega, String detalle, String usuario,
			String destino, Instant fechaReporte, List<SustanciaAviso> sustancias) {
		this.eventoId = eventoId;
		this.tipo = tipo;
		this.prioridad = prioridad;
		this.bodega = bodega;
		this.detalle = detalle;
		this.usuario = usuario;
		this.destino = destino;
		this.fechaReporte = fechaReporte;
		this.fechaDespacho = Instant.now();
		this.sustancias = new ArrayList<>(sustancias);
	}

	public void asignarFolio(String folio) {
		this.folio = folio;
	}

	public Long getId() {
		return id;
	}

	public String getEventoId() {
		return eventoId;
	}

	public String getFolio() {
		return folio;
	}

	public String getTipo() {
		return tipo;
	}

	public int getPrioridad() {
		return prioridad;
	}

	public String getBodega() {
		return bodega;
	}

	public String getDetalle() {
		return detalle;
	}

	public String getUsuario() {
		return usuario;
	}

	public String getDestino() {
		return destino;
	}

	public Instant getFechaReporte() {
		return fechaReporte;
	}

	public Instant getFechaDespacho() {
		return fechaDespacho;
	}

	public List<SustanciaAviso> getSustancias() {
		return sustancias;
	}
}
