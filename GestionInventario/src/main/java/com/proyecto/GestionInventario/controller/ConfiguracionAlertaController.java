package com.proyecto.GestionInventario.controller;

import com.proyecto.GestionInventario.domain.ConfiguracionAlerta;
import com.proyecto.GestionInventario.service.ConfiguracionAlertaService;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/configuracion-alerta")
public class ConfiguracionAlertaController {

    private final ConfiguracionAlertaService service;
    private final MessageSource messageSource;

    public ConfiguracionAlertaController(ConfiguracionAlertaService service,
            MessageSource messageSource) {
        this.service = service;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String ver(Model model) {
        model.addAttribute("config", service.getConfiguracion());
        return "configuracion-alerta/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ConfiguracionAlerta config,
            RedirectAttributes redirectAttributes) {
        try {
            service.guardar(config);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("alerta.config.error", null, Locale.getDefault()));
        }
        return "redirect:/configuracion-alerta";
    }
}
