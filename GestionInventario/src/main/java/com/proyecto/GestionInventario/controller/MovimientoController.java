package com.proyecto.GestionInventario.controller;

import com.proyecto.GestionInventario.domain.TipoMovimiento;
import com.proyecto.GestionInventario.domain.Usuario;
import com.proyecto.GestionInventario.service.BodegaService;
import com.proyecto.GestionInventario.service.ItemService;
import com.proyecto.GestionInventario.service.MovimientoService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/movimiento")
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final ItemService itemService;
    private final BodegaService bodegaService;
    private final MessageSource messageSource;

    public MovimientoController(MovimientoService movimientoService,
                                 ItemService itemService,
                                 BodegaService bodegaService,
                                 MessageSource messageSource) {
        this.movimientoService = movimientoService;
        this.itemService = itemService;
        this.bodegaService = bodegaService;
        this.messageSource = messageSource;
    }

    private void cargarListas(Model model) {
        model.addAttribute("items", itemService.getItems());
        model.addAttribute("bodegas", bodegaService.getBodegas());
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
        cargarListas(model);

        return "/movimiento/listado";
    }

    @PostMapping("/entrada")
    public String registrarEntrada(
            @RequestParam Long itemId,
            @RequestParam Long bodegaId,
            @RequestParam Integer cantidad,
            @RequestParam(required = false) String numeroLote,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCaducidad,
            @RequestParam(required = false) String motivo,
            @RequestParam(required = false) String observaciones,
            HttpSession session, RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        try {
            movimientoService.registrarEntrada(itemId, bodegaId, cantidad,
                    numeroLote, fechaCaducidad, motivo, observaciones, usuario);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("movimiento.entrada.exitoso", null, Locale.getDefault()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("movimiento.error.general", null, Locale.getDefault()));
        }
        return "redirect:/movimiento/listado";
    }

    @PostMapping("/salida")
    public String registrarSalida(
            @RequestParam Long loteId,
            @RequestParam Integer cantidad,
            @RequestParam(required = false) String motivo,
            @RequestParam(required = false) String observaciones,
            HttpSession session, RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        try {
            movimientoService.registrarSalida(loteId, cantidad, motivo, observaciones, usuario);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("movimiento.salida.exitoso", null, Locale.getDefault()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("movimiento.error.general", null, Locale.getDefault()));
        }
        return "redirect:/movimiento/listado";
    }

    @PostMapping("/transferencia")
    public String registrarTransferencia(
            @RequestParam Long loteId,
            @RequestParam Long bodegaDestinoId,
            @RequestParam(required = false) Integer cantidad,
            @RequestParam(required = false) String observaciones,
            HttpSession session, RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        try {
            movimientoService.registrarTransferencia(loteId, bodegaDestinoId, cantidad, observaciones, usuario);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("movimiento.transferencia.exitoso", null, Locale.getDefault()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("movimiento.error.general", null, Locale.getDefault()));
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
