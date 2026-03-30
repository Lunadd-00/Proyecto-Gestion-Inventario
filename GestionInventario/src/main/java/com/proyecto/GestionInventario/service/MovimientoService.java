package com.proyecto.GestionInventario.service;

import com.proyecto.GestionInventario.domain.Item;
import com.proyecto.GestionInventario.domain.Movimiento;
import com.proyecto.GestionInventario.domain.TipoMovimiento;
import com.proyecto.GestionInventario.repository.ItemRepository;
import com.proyecto.GestionInventario.repository.MovimientoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final ItemRepository itemRepository;

    public MovimientoService(MovimientoRepository movimientoRepository,
                              ItemRepository itemRepository) {
        this.movimientoRepository = movimientoRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public List<Movimiento> getMovimientos() {
        return movimientoRepository.findAllByOrderByFechaDesc();
    }

    @Transactional(readOnly = true)
    public List<Movimiento> filtrar(Long itemId, TipoMovimiento tipo) {
        return movimientoRepository.filtrar(itemId, tipo);
    }

    @Transactional(readOnly = true)
    public Optional<Movimiento> getMovimiento(Long id) {
        return movimientoRepository.findById(id);
    }

    @Transactional
    public void registrarEntrada(Movimiento movimiento) {
        movimiento.setTipo(TipoMovimiento.ENTRADA);
        
        Item item = movimiento.getItem();
        if (item == null || item.getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un ítem válido.");
        }
        
        Item itemActual = itemRepository.findById(item.getId())
                .orElseThrow(() -> new IllegalArgumentException("El ítem no existe."));
        
        int nuevoStock = itemActual.getStock() + movimiento.getCantidad();
        itemActual.setStock(nuevoStock);
        
        if (!itemActual.isActivo() && nuevoStock > 0) {
            itemActual.setActivo(true);
        }
        
        itemRepository.save(itemActual);
        
        movimiento.setItem(itemActual);
        movimientoRepository.save(movimiento);
    }

    @Transactional
    public void registrarSalida(Movimiento movimiento) {
        movimiento.setTipo(TipoMovimiento.SALIDA);
        
        Item item = movimiento.getItem();
        if (item == null || item.getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un ítem válido.");
        }
        
        Item itemActual = itemRepository.findById(item.getId())
                .orElseThrow(() -> new IllegalArgumentException("El ítem no existe."));
        
        if (movimiento.getCantidad() > itemActual.getStock()) {
            throw new IllegalStateException(
                    "No hay stock suficiente. Stock disponible: " + itemActual.getStock());
        }
        
        int nuevoStock = itemActual.getStock() - movimiento.getCantidad();
        itemActual.setStock(nuevoStock);
        
        if (nuevoStock == 0) {
            itemActual.setActivo(false);
        }
        
        itemRepository.save(itemActual);
        
        movimiento.setItem(itemActual);
        movimientoRepository.save(movimiento);
    }

    @Transactional
    public void registrarTransferencia(Movimiento movimiento) {
        movimiento.setTipo(TipoMovimiento.TRANSFERENCIA);

        Item item = movimiento.getItem();
        if (item == null || item.getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un ítem válido.");
        }

        Item itemActual = itemRepository.findById(item.getId())
                .orElseThrow(() -> new IllegalArgumentException("El ítem no existe."));

        if (movimiento.getCantidad() > itemActual.getStock()) {
            throw new IllegalStateException(
                    "No hay stock suficiente. Stock disponible: " + itemActual.getStock());
        }

        if (movimiento.getBodegaOrigen() == null || movimiento.getBodegaOrigen().getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar la bodega de origen.");
        }
        if (movimiento.getBodegaDestino() == null || movimiento.getBodegaDestino().getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar la bodega de destino.");
        }
        if (movimiento.getBodegaOrigen().getId().equals(movimiento.getBodegaDestino().getId())) {
            throw new IllegalArgumentException("La bodega de origen y destino deben ser diferentes.");
        }

        movimiento.setItem(itemActual);
        movimientoRepository.save(movimiento);
    }

    @Transactional(readOnly = true)
    public List<Movimiento> getMovimientosPorItem(Long itemId) {
        return movimientoRepository.findByItemIdOrderByFechaDesc(itemId);
    }
}
