package com.proyecto.GestionInventario.repository;

import com.proyecto.GestionInventario.domain.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {


    @Query("SELECT i FROM Item i WHERE " +
           "(:idCategoria IS NULL OR i.categoria.id = :idCategoria) AND " +
           "(:idProveedor IS NULL OR i.proveedor.id = :idProveedor) AND " +
           "(:activo IS NULL OR i.activo = :activo) " +
           "ORDER BY i.nombre ASC")
    List<Item> filtrar(
            @Param("idCategoria") Long idCategoria,
            @Param("idProveedor") Integer idProveedor,
            @Param("activo") Boolean activo
    );
}
