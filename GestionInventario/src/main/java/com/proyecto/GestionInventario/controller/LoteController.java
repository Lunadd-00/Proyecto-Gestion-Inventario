package com.proyecto.GestionInventario.controller;

import com.proyecto.GestionInventario.service.ItemService;
import com.proyecto.GestionInventario.service.LoteService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/lote")
public class LoteController {

    private final LoteService loteService;
    private final ItemService itemService;

    public LoteController(LoteService loteService, ItemService itemService) {
        this.loteService = loteService;
        this.itemService = itemService;
    }

    @GetMapping("/listado/{itemId}")
    public String listado(@PathVariable Long itemId, Model model, RedirectAttributes redirectAttributes) {
        var itemOpt = itemService.getItem(itemId);
        if (itemOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El ítem no existe.");
            return "redirect:/item/listado";
        }
        var lotes = loteService.getLotesPorItem(itemId);
        model.addAttribute("item", itemOpt.get());
        model.addAttribute("lotes", lotes);
        model.addAttribute("totalLotes", lotes.size());
        return "/lote/listado";
    }

    @GetMapping("/api/por-bodega")
    @ResponseBody
    public List<Map<String, Object>> getLotesPorBodega(
            @RequestParam Long itemId, @RequestParam Long bodegaId) {
        return loteService.getLotesPorItemYBodega(itemId, bodegaId).stream()
                .map(l -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", l.getId());
                    m.put("label", l.getNumeroLote() != null ? l.getNumeroLote() : "Lote #" + l.getId());
                    m.put("cantidad", l.getCantidad());
                    m.put("fechaCaducidad", l.getFechaCaducidad() != null ? l.getFechaCaducidad().toString() : "");
                    return m;
                })
                .toList();
    }
}
