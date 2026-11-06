package com.duoc.suspel.procesador.documento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
public class FichaAlmacenamiento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String eventoId;

	private String folio;
	private Long productoId;
	private String producto;
	private String claseImo;
	private String bodega;
	private String indicaciones;
	private Instant fecha;

	protected FichaAlmacenamiento() {
	}

	public FichaAlmacenamiento(String eventoId, Long productoId, String producto, String claseImo, String bodega,
			String indicaciones, Instant fecha) {
		this.eventoId = eventoId;
		this.productoId = productoId;
		this.producto = producto;
		this.claseImo = claseImo;
		this.bodega = bodega;
		this.indicaciones = indicaciones;
		this.fecha = fecha;
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

	public Long getProductoId() {
		return productoId;
	}

	public String getProducto() {
		return producto;
	}

	public String getClaseImo() {
		return claseImo;
	}

	public String getBodega() {
		return bodega;
	}

	public String getIndicaciones() {
		return indicaciones;
	}

	public Instant getFecha() {
		return fecha;
	}
}
