package com.duoc.suspel.procesador.auditoria;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {

	boolean existsByEventoId(String eventoId);
}
