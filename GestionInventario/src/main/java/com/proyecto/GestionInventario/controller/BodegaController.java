package com.proyecto.GestionInventario.controller;

import com.proyecto.GestionInventario.domain.Bodega;
import com.proyecto.GestionInventario.service.BodegaService;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/bodega")
public class BodegaController {

    private final BodegaService bodegaService;
    private final MessageSource messageSource;

    public BodegaController(BodegaService bodegaService, MessageSource messageSource) {
        this.bodegaService = bodegaService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var bodegas = bodegaService.getBodegas();
        model.addAttribute("bodegas", bodegas);
        model.addAttribute("totalBodegas", bodegas.size());
        model.addAttribute("bodega", new Bodega());
        return "bodega/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Bodega bodega, RedirectAttributes redirectAttributes) {
        try {
            bodegaService.save(bodega);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("bodega.error03", null, Locale.getDefault()));
        }
        return "redirect:/bodega/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            bodegaService.delete(id);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("mensaje.eliminado", null, Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("bodega.error01", null, Locale.getDefault()));
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("bodega.error02", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("bodega.error03", null, Locale.getDefault()));
        }
        return "redirect:/bodega/listado";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        var opt = bodegaService.getBodega(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("bodega.error01", null, Locale.getDefault()));
            return "redirect:/bodega/listado";
        }
        model.addAttribute("bodegas", bodegaService.getBodegas());
        model.addAttribute("totalBodegas", bodegaService.getBodegas().size());
        model.addAttribute("bodega", opt.get());
        return "bodega/modificar";
    }
}
