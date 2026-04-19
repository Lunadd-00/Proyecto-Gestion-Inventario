package com.proyecto.GestionInventario.controller;

import com.proyecto.GestionInventario.service.ItemService;
import com.proyecto.GestionInventario.service.LoteService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/lote")
public class LoteController {

    private final LoteService loteService;
    private final ItemService itemService;
    private final MessageSource messageSource;

    public LoteController(LoteService loteService, ItemService itemService,
            MessageSource messageSource) {
        this.loteService = loteService;
        this.itemService = itemService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado/{itemId}")
    public String listado(@PathVariable Long itemId, Model model, RedirectAttributes redirectAttributes) {
        var itemOpt = itemService.getItem(itemId);
        if (itemOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("item.error01", null, Locale.getDefault()));
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

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long idLote, @RequestParam Long itemId,
            RedirectAttributes redirectAttributes) {
        try {
            loteService.delete(idLote);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("mensaje.eliminado", null, Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("lote.error01", null, Locale.getDefault()));
        }
        return "redirect:/lote/listado/" + itemId;
    }
}
