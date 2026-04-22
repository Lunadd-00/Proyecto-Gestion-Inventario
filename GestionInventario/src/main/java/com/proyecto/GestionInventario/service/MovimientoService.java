package com.proyecto.GestionInventario.service;

import com.proyecto.GestionInventario.domain.Bodega;
import com.proyecto.GestionInventario.domain.Item;
import com.proyecto.GestionInventario.domain.Lote;
import com.proyecto.GestionInventario.domain.Movimiento;
import com.proyecto.GestionInventario.domain.TipoMovimiento;
import com.proyecto.GestionInventario.domain.Usuario;
import com.proyecto.GestionInventario.repository.BodegaRepository;
import com.proyecto.GestionInventario.repository.ItemRepository;
import com.proyecto.GestionInventario.repository.LoteRepository;
import com.proyecto.GestionInventario.repository.MovimientoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final ItemRepository itemRepository;
    private final LoteRepository loteRepository;
    private final BodegaRepository bodegaRepository;

    public MovimientoService(MovimientoRepository movimientoRepository,
                              ItemRepository itemRepository,
                              LoteRepository loteRepository,
                              BodegaRepository bodegaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.itemRepository = itemRepository;
        this.loteRepository = loteRepository;
        this.bodegaRepository = bodegaRepository;
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

    @Transactional(readOnly = true)
    public List<Movimiento> getMovimientosPorItem(Long itemId) {
        return movimientoRepository.findByItemIdOrderByFechaDesc(itemId);
    }

    @Transactional
    public void registrarEntrada(Long itemId, Long bodegaId, Integer cantidad,
                                  String numeroLote, LocalDate fechaCaducidad,
                                  String motivo, String observaciones, Usuario usuario) {
        if (cantidad == null || cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("El ítem no existe."));
        Bodega bodega = bodegaRepository.findById(bodegaId)
                .orElseThrow(() -> new IllegalArgumentException("La bodega no existe."));

        String numLote = (numeroLote != null && !numeroLote.isBlank()) ? numeroLote.trim() : null;
        if (numLote != null && loteRepository.existsByNumeroLote(numLote)) {
            throw new IllegalArgumentException(
                    "El número de lote '" + numLote + "' ya existe. Use un número diferente.");
        }

        Lote lote = new Lote();
        lote.setItem(item);
        lote.setBodega(bodega);
        lote.setCantidad(cantidad);
        lote.setNumeroLote(numLote);
        lote.setFechaCaducidad(fechaCaducidad);
        lote.setActivo(true);
        loteRepository.save(lote);

        item.setStock(item.getStock() + cantidad);
        if (!item.isActivo()) item.setActivo(true);
        itemRepository.save(item);

        Movimiento mov = new Movimiento();
        mov.setItem(item);
        mov.setUsuario(usuario);
        mov.setTipo(TipoMovimiento.ENTRADA);
        mov.setCantidad(cantidad);
        mov.setBodegaDestino(bodega);
        mov.setMotivo(motivo);
        mov.setObservaciones(observaciones);
        movimientoRepository.save(mov);
    }

    @Transactional
    public void registrarSalida(Long loteId, Integer cantidad,
                                 String motivo, String observaciones, Usuario usuario) {
        if (cantidad == null || cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new IllegalArgumentException("El lote no existe."));

        if (cantidad > lote.getCantidad()) {
            throw new IllegalStateException(
                    "Stock insuficiente en el lote. Disponible: " + lote.getCantidad());
        }

        Item item = lote.getItem();
        Bodega bodega = lote.getBodega();

        lote.setCantidad(lote.getCantidad() - cantidad);
        if (lote.getCantidad() == 0) {
            loteRepository.deleteById(lote.getId());
        } else {
            loteRepository.save(lote);
        }

        int nuevoStock = Math.max(0, item.getStock() - cantidad);
        item.setStock(nuevoStock);
        if (nuevoStock == 0) item.setActivo(false);
        itemRepository.save(item);

        Movimiento mov = new Movimiento();
        mov.setItem(item);
        mov.setUsuario(usuario);
        mov.setTipo(TipoMovimiento.SALIDA);
        mov.setCantidad(cantidad);
        mov.setBodegaOrigen(bodega);
        mov.setMotivo(motivo);
        mov.setObservaciones(observaciones);
        movimientoRepository.save(mov);
    }

    @Transactional
    public void registrarTransferencia(Long loteId, Long bodegaDestinoId,
                                        Integer cantidadSolicitada, String motivo,
                                        String observaciones, Usuario usuario) {
        Lote loteOrigen = loteRepository.findById(loteId)
                .orElseThrow(() -> new IllegalArgumentException("El lote no existe."));

        Bodega bodegaDestino = bodegaRepository.findById(bodegaDestinoId)
                .orElseThrow(() -> new IllegalArgumentException("La bodega de destino no existe."));

        if (loteOrigen.getBodega().getId().equals(bodegaDestinoId)) {
            throw new IllegalArgumentException("La bodega de origen y destino deben ser diferentes.");
        }

        Item item = loteOrigen.getItem();
        Bodega bodegaOrigen = loteOrigen.getBodega();

        int cantidad;
        if (loteOrigen.getFechaCaducidad() != null) {
            // Lote con fecha de vencimiento → se transfiere completo
            cantidad = loteOrigen.getCantidad();
        } else {
            if (cantidadSolicitada == null || cantidadSolicitada < 1) {
                throw new IllegalArgumentException("Debe indicar una cantidad válida.");
            }
            cantidad = cantidadSolicitada;
        }

        if (cantidad > loteOrigen.getCantidad()) {
            throw new IllegalStateException(
                    "Stock insuficiente en el lote. Disponible: " + loteOrigen.getCantidad());
        }

        if (cantidad == loteOrigen.getCantidad()) {
            // Mover lote entero: sólo cambia bodega
            loteOrigen.setBodega(bodegaDestino);
            loteRepository.save(loteOrigen);
        } else {
            // Transferencia parcial: reducir origen, crear nuevo lote en destino
            loteOrigen.setCantidad(loteOrigen.getCantidad() - cantidad);
            loteRepository.save(loteOrigen);

            Lote loteDestino = new Lote();
            loteDestino.setItem(item);
            loteDestino.setBodega(bodegaDestino);
            loteDestino.setCantidad(cantidad);
            loteDestino.setActivo(true);
            loteRepository.save(loteDestino);
        }

        Movimiento mov = new Movimiento();
        mov.setItem(item);
        mov.setUsuario(usuario);
        mov.setTipo(TipoMovimiento.TRANSFERENCIA);
        mov.setCantidad(cantidad);
        mov.setBodegaOrigen(bodegaOrigen);
        mov.setBodegaDestino(bodegaDestino);
        mov.setMotivo(motivo);
        mov.setObservaciones(observaciones);
        movimientoRepository.save(mov);
    }
}
