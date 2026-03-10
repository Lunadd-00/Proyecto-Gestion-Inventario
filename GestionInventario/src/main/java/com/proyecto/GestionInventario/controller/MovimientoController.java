package com.proyecto.GestionInventario.controller;

import com.proyecto.GestionInventario.domain.Movimiento;
import com.proyecto.GestionInventario.domain.TipoMovimiento;
import com.proyecto.GestionInventario.service.ItemService;
import com.proyecto.GestionInventario.service.MovimientoService;
import com.proyecto.GestionInventario.service.UsuarioService;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/movimiento")
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final ItemService itemService;
    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public MovimientoController(MovimientoService movimientoService,
                                 ItemService itemService,
                                 UsuarioService usuarioService,
                                 MessageSource messageSource) {
        this.movimientoService = movimientoService;
        this.itemService = itemService;
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    private void cargarListas(Model model) {
        model.addAttribute("items", itemService.getItems());
        model.addAttribute("usuarios", usuarioService.getUsuarios(true));
        model.addAttribute("tiposMovimiento", TipoMovimiento.values());
    }

    @GetMapping("/listado")
    public String listado(
            @RequestParam(required = false) String itemId,
            @RequestParam(required = false) String tipo,
            Model model) {

        Long itemIdLong = (itemId != null && !itemId.isEmpty()) ? Long.parseLong(itemId) : null;
        TipoMovimiento tipoEnum = (tipo != null && !tipo.isEmpty()) ? TipoMovimiento.valueOf(tipo) : null;

        var movimientos = movimientoService.filtrar(itemIdLong, tipoEnum);
        model.addAttribute("movimientos", movimientos);
        model.addAttribute("totalMovimientos", movimientos.size());
        model.addAttribute("itemIdSeleccionado", itemIdLong);
        model.addAttribute("tipoSeleccionado", tipoEnum);

        model.addAttribute("movimientoNuevo", new Movimiento());
        cargarListas(model);

        return "/movimiento/listado";
    }

    @PostMapping("/entrada")
    public String registrarEntrada(@ModelAttribute("movimientoNuevo") Movimiento movimiento,
                                    RedirectAttributes redirectAttributes) {

        if (movimiento.getItem() == null || movimiento.getItem().getId() == null) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar un ítem.");
            return "redirect:/movimiento/listado";
        }
        if (movimiento.getUsuario() == null || movimiento.getUsuario().getId() == null) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar un usuario.");
            return "redirect:/movimiento/listado";
        }
        if (movimiento.getCantidad() == null || movimiento.getCantidad() < 1) {
            redirectAttributes.addFlashAttribute("error", "La cantidad debe ser mayor a 0.");
            return "redirect:/movimiento/listado";
        }

        try {
            movimientoService.registrarEntrada(movimiento);
            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    messageSource.getMessage("movimiento.entrada.exitoso", null, Locale.getDefault())
            );
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("movimiento.error.general", null, Locale.getDefault())
            );
        }

        return "redirect:/movimiento/listado";
    }

    @PostMapping("/salida")
    public String registrarSalida(@ModelAttribute("movimientoNuevo") Movimiento movimiento,
                                   RedirectAttributes redirectAttributes) {

        if (movimiento.getItem() == null || movimiento.getItem().getId() == null) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar un ítem.");
            return "redirect:/movimiento/listado";
        }
        if (movimiento.getUsuario() == null || movimiento.getUsuario().getId() == null) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar un usuario.");
            return "redirect:/movimiento/listado";
        }
        if (movimiento.getCantidad() == null || movimiento.getCantidad() < 1) {
            redirectAttributes.addFlashAttribute("error", "La cantidad debe ser mayor a 0.");
            return "redirect:/movimiento/listado";
        }

        try {
            movimientoService.registrarSalida(movimiento);
            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    messageSource.getMessage("movimiento.salida.exitoso", null, Locale.getDefault())
            );
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("movimiento.error.general", null, Locale.getDefault())
            );
        }

        return "redirect:/movimiento/listado";
    }

    @GetMapping("/historial/{itemId}")
    public String historialPorItem(@PathVariable Long itemId, Model model) {
        var movimientos = movimientoService.getMovimientosPorItem(itemId);
        var item = itemService.getItem(itemId);

        model.addAttribute("movimientos", movimientos);
        model.addAttribute("item", item.orElse(null));
        model.addAttribute("totalMovimientos", movimientos.size());

        return "/movimiento/historial";
    }
}
