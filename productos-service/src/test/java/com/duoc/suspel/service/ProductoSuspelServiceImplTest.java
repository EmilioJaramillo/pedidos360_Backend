package com.duoc.suspel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.duoc.suspel.entity.ProductoSuspel;
import com.duoc.suspel.messaging.InventarioEventPublisher;
import com.duoc.suspel.messaging.TipoEvento;
import com.duoc.suspel.repository.ProductoSuspelRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductoSuspelServiceImplTest {

	@Mock
	private ProductoSuspelRepository repository;

	@Mock
	private InventarioEventPublisher eventos;

	private ProductoSuspelServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new ProductoSuspelServiceImpl(repository, eventos, 300);
	}

	@Test
	void crearPublicaCreadoYFicha() {
		ProductoSuspel producto = producto(1L, 500);
		when(repository.save(any())).thenReturn(producto);

		service.guardar(producto);

		verify(eventos).publicar(TipoEvento.PRODUCTO_CREADO, producto);
		verify(eventos).publicar(TipoEvento.FICHA_SOLICITADA, producto);
		verify(eventos, never()).publicar(eq(TipoEvento.STOCK_BAJO), any());
	}

	@Test
	void actualizarBajoUmbralPublicaStockBajo() {
		ProductoSuspel existente = producto(2L, 800);
		when(repository.findById(2L)).thenReturn(Optional.of(existente));
		when(repository.save(existente)).thenReturn(existente);

		service.actualizar(2L, producto(null, 120));

		verify(eventos).publicar(TipoEvento.PRODUCTO_ACTUALIZADO, existente);
		verify(eventos).publicar(TipoEvento.STOCK_BAJO, existente);
	}

	@Test
	void eliminarInexistenteNoPublica() {
		when(repository.findById(9L)).thenReturn(Optional.empty());

		assertThat(service.eliminar(9L)).isFalse();
		verify(eventos, never()).publicar(any(), any());
	}

	private ProductoSuspel producto(Long id, int cantidad) {
		ProductoSuspel p = new ProductoSuspel();
		p.setId(id);
		p.setNombre("Propano");
		p.setImo("Clase 2.1 - Gas Inflamable");
		p.setBodega("Bodega A-01");
		p.setCantidad(cantidad);
		return p;
	}
}
