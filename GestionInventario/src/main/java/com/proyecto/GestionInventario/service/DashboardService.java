package com.proyecto.GestionInventario.service;

import com.proyecto.GestionInventario.domain.Item;
import com.proyecto.GestionInventario.domain.Movimiento;
import com.proyecto.GestionInventario.repository.ItemRepository;
import com.proyecto.GestionInventario.repository.MovimientoRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lunad
 */
@Service
public class DashboardService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    public Map<String, Integer> obtenerStockPorProducto() {
        List<Item> items = itemRepository.findAll();

        Map<String, Integer> data = new LinkedHashMap<>();

        for (Item item : items) {
            data.put(item.getNombre(), item.getStock());
        }

        return data;
    }

    public Map<String, Integer> obtenerEstadoVencimientos() {
        List<Item> items = itemRepository.findAll();

        int vencidos = 0;
        int porVencer = 0;
        int ok = 0;

        LocalDate hoy = LocalDate.now();

        for (Item item : items) {
            if (item.isTieneCaducidad() && item.getFechaCaducidad() != null) {

                if (item.getFechaCaducidad().isBefore(hoy)) {
                    vencidos++;
                } else if (item.getFechaCaducidad().isBefore(hoy.plusDays(7))) {
                    porVencer++;
                } else {
                    ok++;
                }

            }
        }

        Map<String, Integer> data = new HashMap<>();
        data.put("Vencidos", vencidos);
        data.put("Por vencer", porVencer);
        data.put("En buen estado", ok);

        return data;
    }

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

    public Map<String, Object> obtenerMetricas() {

        List<Item> items = itemRepository.findAll();

        int totalItems = items.size();
        int activos = 0;
        int agotados = 0;
        int stockTotal = 0;
        int stockBajo = 0;
        int itemsPorVencer = 0;

        LocalDate hoy = LocalDate.now();

        for (Item item : items) {

            if (item.isActivo()) {
                activos++;
            }

            if (item.getStock() == 0) {
                agotados++;
            }

            stockTotal += item.getStock();

            if (item.getStock() <= item.getStockMinimo()) {
                stockBajo++;
            }

            if (item.isTieneCaducidad() && item.getFechaCaducidad() != null) {
                long dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, item.getFechaCaducidad());

                if (dias >= 0 && dias <= 30) {
                    itemsPorVencer++;
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("totalItems", totalItems);
        data.put("activos", activos);
        data.put("agotados", agotados);
        data.put("stockTotal", stockTotal);
        data.put("stockBajo", stockBajo);
        data.put("itemsPorVencer", itemsPorVencer);

        return data;
    }

    public List<Item> obtenerItemsPorVencer() {
        List<Item> items = itemRepository.findAll();
        LocalDate hoy = LocalDate.now();

        return items.stream()
                .filter(i -> i.isTieneCaducidad()
                && i.getFechaCaducidad() != null
                && !i.getFechaCaducidad().isBefore(hoy)
                && i.getFechaCaducidad().isBefore(hoy.plusDays(30)))
                .sorted((a, b) -> a.getFechaCaducidad().compareTo(b.getFechaCaducidad()))
                .toList();
    }

    public List<Item> obtenerStockBajo() {
        return itemRepository.findAll().stream()
                .filter(i -> i.getStock() <= i.getStockMinimo())
                .toList();
    }
}
