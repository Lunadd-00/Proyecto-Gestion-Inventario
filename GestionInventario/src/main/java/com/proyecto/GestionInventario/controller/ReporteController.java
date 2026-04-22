package com.proyecto.GestionInventario.controller;

import com.proyecto.GestionInventario.service.ReporteService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reporte")
public class ReporteController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("hoy", LocalDate.now().format(FMT));
        return "/reporte/listado";
    }

    @GetMapping("/pdf/inventario")
    public ResponseEntity<byte[]> pdfInventario(@AuthenticationPrincipal UserDetails user) {
        byte[] bytes = reporteService.generarInventarioPdf(user.getUsername());
        return descargar(bytes, "Reporte_Inventario_General_" + hoy() + ".pdf", "application/pdf");
    }

    @GetMapping("/pdf/movimientos")
    public ResponseEntity<byte[]> pdfMovimientos(
            @RequestParam String inicio,
            @RequestParam String fin,
            @AuthenticationPrincipal UserDetails user) {
        byte[] bytes = reporteService.generarMovimientosPdf(
                LocalDate.parse(inicio, FMT), LocalDate.parse(fin, FMT), user.getUsername());
        return descargar(bytes, "Reporte_Movimientos_" + inicio + "_" + fin + ".pdf", "application/pdf");
    }

    @GetMapping("/pdf/por-vencer")
    public ResponseEntity<byte[]> pdfPorVencer(
            @RequestParam(defaultValue = "30") int dias,
            @AuthenticationPrincipal UserDetails user) {
        byte[] bytes = reporteService.generarPorVencerPdf(dias, user.getUsername());
        return descargar(bytes, "Reporte_Por_Vencer_" + dias + "dias_" + hoy() + ".pdf", "application/pdf");
    }

    @GetMapping("/pdf/stock-bajo")
    public ResponseEntity<byte[]> pdfStockBajo(@AuthenticationPrincipal UserDetails user) {
        byte[] bytes = reporteService.generarStockBajoPdf(user.getUsername());
        return descargar(bytes, "Reporte_Stock_Bajo_" + hoy() + ".pdf", "application/pdf");
    }

    @GetMapping("/excel/inventario")
    public ResponseEntity<byte[]> excelInventario() {
        byte[] bytes = reporteService.generarInventarioExcel();
        return descargar(bytes, "Reporte_Inventario_" + hoy() + ".xlsx", excel());
    }

    @GetMapping("/excel/movimientos")
    public ResponseEntity<byte[]> excelMovimientos(
            @RequestParam String inicio,
            @RequestParam String fin) {
        byte[] bytes = reporteService.generarMovimientosExcel(
                LocalDate.parse(inicio, FMT), LocalDate.parse(fin, FMT));
        return descargar(bytes, "Reporte_Movimientos_" + inicio + "_" + fin + ".xlsx", excel());
    }

    @GetMapping("/excel/completo")
    public ResponseEntity<byte[]> excelCompleto() {
        byte[] bytes = reporteService.generarReporteCompletoExcel();
        return descargar(bytes, "Reporte_Completo_" + hoy() + ".xlsx", excel());
    }

    private String hoy() {
        return LocalDate.now().format(FMT);
    }

    private String excel() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    private ResponseEntity<byte[]> descargar(byte[] bytes, String nombre, String tipo) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .contentType(MediaType.parseMediaType(tipo))
                .body(bytes);
    }
}
