package com.proyecto.GestionInventario.service;

import com.proyecto.GestionInventario.domain.Item;
import com.proyecto.GestionInventario.domain.Lote;
import com.proyecto.GestionInventario.domain.Movimiento;
import com.proyecto.GestionInventario.repository.ItemRepository;
import com.proyecto.GestionInventario.repository.LoteRepository;
import com.proyecto.GestionInventario.repository.MovimientoRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final ItemRepository itemRepository;
    private final MovimientoRepository movimientoRepository;
    private final LoteRepository loteRepository;

    public DashboardService(ItemRepository itemRepository,
            MovimientoRepository movimientoRepository,
            LoteRepository loteRepository) {
        this.itemRepository = itemRepository;
        this.movimientoRepository = movimientoRepository;
        this.loteRepository = loteRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> obtenerStockPorProducto() {
        List<Item> items = itemRepository.findAll();

        Map<String, Integer> data = new LinkedHashMap<>();

        for (Item item : items) {
            data.put(item.getNombre(), item.getStock());
        }

        return data;
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> obtenerEstadoVencimientos() {
        List<Lote> lotes = loteRepository.findAll().stream()
                .filter(l -> l.getItem() != null && l.isActivo() && l.getFechaCaducidad() != null)
                .toList();

        int vencidos = 0;
        int porVencer = 0;
        int ok = 0;
        LocalDate hoy = LocalDate.now();

        for (Lote lote : lotes) {
            LocalDate fecha = lote.getFechaCaducidad();
            if (fecha.isBefore(hoy)) {
                vencidos++;
            } else if (fecha.isBefore(hoy.plusDays(7))) {
                porVencer++;
            } else {
                ok++;
            }
        }

        Map<String, Integer> data = new HashMap<>();
        data.put("Vencidos", vencidos);
        data.put("Por vencer", porVencer);
        data.put("En buen estado", ok);

        return data;
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> obtenerMovimientos() {
        List<Movimiento> movimientos = movimientoRepository.findAll();

        int entradas = 0;
        int salidas = 0;

        for (Movimiento m : movimientos) {
            if (m.getTipo().name().equals("ENTRADA")) {
                entradas += m.getCantidad();
            } else if (m.getTipo().name().equals("SALIDA")) {
                salidas += m.getCantidad();
            }
        }

        Map<String, Integer> data = new HashMap<>();
        data.put("Entradas", entradas);
        data.put("Salidas", salidas);

        return data;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerMetricas() {

        List<Item> items = itemRepository.findAll();

        int totalItems = items.size();
        int activos = 0;
        int agotados = 0;
        int stockTotal = 0;
        int stockBajo = 0;

        LocalDate hoy = LocalDate.now();

        for (Item item : items) {
            if (item.isActivo())                      activos++;
            if (item.getStock() == 0)                 agotados++;
            stockTotal += item.getStock();
            if (item.getStock() <= item.getStockMinimo()) stockBajo++;
        }

        long lotesPorVencer = loteRepository.findAll().stream()
                .filter(l -> l.getItem() != null && l.isActivo() && l.getFechaCaducidad() != null)
                .filter(l -> {
                    long dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, l.getFechaCaducidad());
                    return dias >= 0 && dias <= 30;
                })
                .map(l -> l.getItem().getId())
                .distinct()
                .count();

        Map<String, Object> data = new HashMap<>();
        data.put("totalItems", totalItems);
        data.put("activos", activos);
        data.put("agotados", agotados);
        data.put("stockTotal", stockTotal);
        data.put("stockBajo", stockBajo);
        data.put("itemsPorVencer", (int) lotesPorVencer);

        return data;
    }

    @Transactional(readOnly = true)
    public List<Lote> obtenerLotesPorVencer() {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(30);
        return loteRepository.findAll().stream()
                .filter(l -> l.getItem() != null && l.isActivo() && l.getFechaCaducidad() != null
                        && !l.getFechaCaducidad().isBefore(hoy)
                        && l.getFechaCaducidad().isBefore(limite))
                .sorted(java.util.Comparator.comparing(Lote::getFechaCaducidad))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Item> obtenerStockBajo() {
        return itemRepository.findAll().stream()
                .filter(i -> i.getStock() <= i.getStockMinimo())
                .toList();
    }
}
