package com.proyecto.GestionInventario.service;

import com.proyecto.GestionInventario.domain.Item;
import com.proyecto.GestionInventario.repository.ItemRepository;
import com.proyecto.GestionInventario.repository.LoteRepository;
import com.proyecto.GestionInventario.repository.MovimientoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final LoteRepository loteRepository;
    private final MovimientoRepository movimientoRepository;

    public ItemService(ItemRepository itemRepository, LoteRepository loteRepository,
                       MovimientoRepository movimientoRepository) {
        this.itemRepository = itemRepository;
        this.loteRepository = loteRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional(readOnly = true)
    public List<Item> getItems() {
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Item> filtrar(Long idCategoria, Integer idProveedor, Boolean activo) {
        return itemRepository.filtrar(idCategoria, idProveedor, activo);
    }

    @Transactional(readOnly = true)
    public Optional<Item> getItem(Long id) {
        return itemRepository.findById(id);
    }

    @Transactional
    public void save(Item item) {
        if (item.getId() != null) {
            itemRepository.findById(item.getId()).ifPresent(existing -> {
                item.setFechaCreacion(existing.getFechaCreacion());
                item.setStock(existing.getStock());

                // Solo bloquear si el usuario está desactivando (cambia de true → false)
                if (existing.isActivo() && !item.isActivo()) {
                    boolean tieneLotes = !loteRepository.findByItemIdOrderByFechaIngresoDesc(item.getId()).isEmpty();
                    if (tieneLotes) {
                        throw new IllegalStateException("lotes");
                    }
                }
            });
        }
        itemRepository.save(item);
    }

    @Transactional
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("El ítem con ID " + id + " no existe.");
        }
        movimientoRepository.deleteByItemId(id);
        loteRepository.deleteByItemId(id);
        itemRepository.deleteById(id);
    }
}
