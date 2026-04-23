package com.proyecto.GestionInventario.repository;

import com.proyecto.GestionInventario.domain.Movimiento;
import com.proyecto.GestionInventario.domain.TipoMovimiento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByItemIdOrderByFechaDesc(Long itemId);

    List<Movimiento> findByTipoOrderByFechaDesc(TipoMovimiento tipo);

    @Query("SELECT m FROM Movimiento m WHERE " +
           "(:itemId IS NULL OR m.item.id = :itemId) AND " +
           "(:tipo IS NULL OR m.tipo = :tipo) " +
           "ORDER BY m.fecha DESC")
    List<Movimiento> filtrar(
            @Param("itemId") Long itemId,
            @Param("tipo") TipoMovimiento tipo
    );

    List<Movimiento> findAllByOrderByFechaDesc();

    @Modifying
    @Query(value = "DELETE FROM movimiento WHERE item_id = :itemId", nativeQuery = true)
    void deleteByItemId(@Param("itemId") Long itemId);
}
