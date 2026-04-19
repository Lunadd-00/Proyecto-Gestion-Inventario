package com.proyecto.GestionInventario.repository;

import com.proyecto.GestionInventario.domain.Lote;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    List<Lote> findByItemIdOrderByFechaIngresoDesc(Long itemId);

    List<Lote> findByItemIdAndBodegaIdAndActivoTrueOrderByFechaIngresoAsc(Long itemId, Long bodegaId);

    @Query("SELECT l FROM Lote l WHERE l.activo = true AND l.fechaCaducidad IS NOT NULL " +
           "AND l.fechaCaducidad BETWEEN :hoy AND :limite ORDER BY l.fechaCaducidad ASC")
    List<Lote> findLotesPorVencer(@Param("hoy") LocalDate hoy, @Param("limite") LocalDate limite);
}
