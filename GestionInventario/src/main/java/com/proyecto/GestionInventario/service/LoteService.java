package com.proyecto.GestionInventario.service;

import com.proyecto.GestionInventario.domain.Item;
import com.proyecto.GestionInventario.domain.Lote;
import com.proyecto.GestionInventario.repository.ItemRepository;
import com.proyecto.GestionInventario.repository.LoteRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoteService {

    private final LoteRepository loteRepository;
    private final ItemRepository itemRepository;

    public LoteService(LoteRepository loteRepository, ItemRepository itemRepository) {
        this.loteRepository = loteRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public List<Lote> getLotesPorItem(Long itemId) {
        return loteRepository.findByItemIdOrderByFechaIngresoDesc(itemId);
    }

    @Transactional(readOnly = true)
    public Optional<Lote> getLote(Long id) {
        return loteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Lote> getLotesPorItemYBodega(Long itemId, Long bodegaId) {
        return loteRepository.findByItemIdAndBodegaIdAndActivoTrueOrderByFechaIngresoAsc(itemId, bodegaId);
    }

    @Transactional(readOnly = true)
    public List<Lote> getLotesPorVencer(int dias) {
        return loteRepository.findLotesPorVencer(LocalDate.now(), LocalDate.now().plusDays(dias));
    }

    @Transactional
    public void save(Lote lote) {
        boolean esNuevo = lote.getId() == null;
        loteRepository.save(lote);

        if (esNuevo) {
            Item item = itemRepository.findById(lote.getItem().getId())
                    .orElseThrow(() -> new IllegalArgumentException("El ítem no existe."));
            item.setStock(item.getStock() + lote.getCantidad());
            if (!item.isActivo() && item.getStock() > 0) {
                item.setActivo(true);
            }
            itemRepository.save(item);
        }
    }

    @Transactional
    public void delete(Long id) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El lote no existe."));

        Item item = itemRepository.findById(lote.getItem().getId())
                .orElseThrow(() -> new IllegalArgumentException("El ítem no existe."));

        int nuevoStock = Math.max(0, item.getStock() - lote.getCantidad());
        item.setStock(nuevoStock);
        if (nuevoStock == 0) {
            item.setActivo(false);
        }
        itemRepository.save(item);
        loteRepository.deleteById(id);
    }
}
