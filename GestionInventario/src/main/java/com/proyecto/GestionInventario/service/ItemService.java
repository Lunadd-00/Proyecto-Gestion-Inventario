package com.proyecto.GestionInventario.service;

import com.proyecto.GestionInventario.domain.Item;
import com.proyecto.GestionInventario.repository.ItemRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de Item.
 * Cubre HU-06 (registro con/sin caducidad), HU-07 (edición) y HU-08 (listado y filtros).
 */
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // HU-08: listado completo sin filtros
    @Transactional(readOnly = true)
    public List<Item> getItems() {
        return itemRepository.findAll();
    }

    // HU-08: listado con filtros opcionales por categoría, proveedor y estado
    @Transactional(readOnly = true)
    public List<Item> filtrar(Long idCategoria, Integer idProveedor, Boolean activo) {
        return itemRepository.filtrar(idCategoria, idProveedor, activo);
    }

    // HU-07: obtener un ítem por ID para edición
    @Transactional(readOnly = true)
    public Optional<Item> getItem(Long id) {
        return itemRepository.findById(id);
    }

    // HU-06 y HU-07: guardar (crear o actualizar)
    // La lógica de caducidad se maneja con @PrePersist/@PreUpdate en la entidad
    @Transactional
    public void save(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("El ítem con ID " + id + " no existe.");
        }
        try {
            itemRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el ítem porque tiene movimientos asociados.", e);
        }
    }
}
