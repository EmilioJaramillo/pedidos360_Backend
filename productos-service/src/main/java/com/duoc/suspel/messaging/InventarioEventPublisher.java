package com.duoc.suspel.messaging;

import com.duoc.suspel.entity.ProductoSuspel;

public interface InventarioEventPublisher {

	void publicar(TipoEvento tipo, ProductoSuspel producto);
}
