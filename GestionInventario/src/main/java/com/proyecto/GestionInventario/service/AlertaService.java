package com.proyecto.GestionInventario.service;

import com.proyecto.GestionInventario.domain.ConfiguracionAlerta;
import com.proyecto.GestionInventario.domain.Item;
import com.proyecto.GestionInventario.repository.ItemRepository;
import jakarta.mail.MessagingException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertaService {

    private static final Logger log = LoggerFactory.getLogger(AlertaService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ItemRepository itemRepository;
    private final CorreoService correoService;
    private final ConfiguracionAlertaService configuracionService;

    @Value("${alerta.correo.admin}")
    private String correoAdmin;

    public AlertaService(ItemRepository itemRepository,
            CorreoService correoService,
            ConfiguracionAlertaService configuracionService) {
        this.itemRepository = itemRepository;
        this.correoService = correoService;
        this.configuracionService = configuracionService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void verificarYEnviarAlertas() {
        ConfiguracionAlerta config = configuracionService.getConfiguracion();
        LocalTime ahora = LocalTime.now();
        int hora   = ahora.getHour();
        int minuto = ahora.getMinute();

        if (hora == config.getHoraVencimiento() && minuto == config.getMinutoVencimiento()) {
            alertarVencimientoProximo();
        }
        if (hora == config.getHoraStock() && minuto == config.getMinutoStock()) {
            alertarStockMinimo();
        }
    }

    @Transactional(readOnly = true)
    public void alertarVencimientoProximo() {
        LocalDate hoy    = LocalDate.now();
        LocalDate limite = hoy.plusDays(30);

        List<Item> items = itemRepository.findAll().stream()
                .filter(i -> i.isTieneCaducidad()
                        && i.getFechaCaducidad() != null
                        && !i.getFechaCaducidad().isBefore(hoy)
                        && i.getFechaCaducidad().isBefore(limite))
                .toList();

        if (items.isEmpty()) return;

        try {
            correoService.enviarCorreoHtml(correoAdmin,
                    "⚠️ Alerta: Ítems próximos a vencer — Inventario Escazú",
                    construirHtmlVencimiento(items, hoy));
            log.info("Alerta de vencimiento enviada: {} ítems", items.size());
        } catch (MessagingException e) {
            log.error("Error al enviar alerta de vencimiento: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public void alertarStockMinimo() {
        List<Item> items = itemRepository.findAll().stream()
                .filter(i -> i.isActivo() && i.getStock() <= i.getStockMinimo())
                .toList();

        if (items.isEmpty()) return;

        try {
            correoService.enviarCorreoHtml(correoAdmin,
                    "🔴 Alerta: Ítems con stock mínimo — Inventario Escazú",
                    construirHtmlStockBajo(items));
            log.info("Alerta de stock mínimo enviada: {} ítems", items.size());
        } catch (MessagingException e) {
            log.error("Error al enviar alerta de stock mínimo: {}", e.getMessage());
        }
    }

    private String construirHtmlVencimiento(List<Item> items, LocalDate hoy) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:Arial,sans-serif;'>");
        sb.append("<h2 style='color:#1cb6b8;'>⚠️ Ítems próximos a vencer</h2>");
        sb.append("<p>Los siguientes ítems vencerán en los próximos <strong>30 días</strong>:</p>");
        sb.append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse:collapse;width:100%;'>");
        sb.append("<thead style='background:#f5a623;color:white;'>");
        sb.append("<tr><th>Ítem</th><th>Categoría</th><th>Stock</th><th>Fecha Vencimiento</th><th>Días Restantes</th></tr>");
        sb.append("</thead><tbody>");
        for (Item i : items) {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, i.getFechaCaducidad());
            String color = dias <= 7 ? "#ffd6d6" : "#fff9e6";
            sb.append("<tr style='background:").append(color).append(";'>");
            sb.append("<td>").append(i.getNombre()).append("</td>");
            sb.append("<td>").append(i.getCategoria() != null ? i.getCategoria().getNombre() : "—").append("</td>");
            sb.append("<td>").append(i.getStock()).append(" ").append(i.getUnidadMedida()).append("</td>");
            sb.append("<td>").append(i.getFechaCaducidad().format(FMT)).append("</td>");
            sb.append("<td><strong>").append(dias).append(" días</strong></td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");
        sb.append("<br><p style='color:#999;font-size:12px;'>Sistema de Inventario — Municipalidad de Escazú</p>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private String construirHtmlStockBajo(List<Item> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:Arial,sans-serif;'>");
        sb.append("<h2 style='color:#f86828;'>🔴 Ítems con stock mínimo o agotado</h2>");
        sb.append("<p>Los siguientes ítems requieren reabastecimiento:</p>");
        sb.append("<table border='0' cellpadding='8' cellspacing='0' style='border-collapse:collapse;width:100%;'>");
        sb.append("<thead><tr style='background:#f86828;color:white;'>");
        sb.append("<th>Ítem</th><th>Categoría</th><th>Proveedor</th><th>Stock Actual</th><th>Stock Mínimo</th>");
        sb.append("</tr></thead><tbody>");
        for (Item i : items) {
            String color = i.getStock() == 0 ? "#ffd6d6" : "#fff3cd";
            sb.append("<tr style='background:").append(color).append(";'>");
            sb.append("<td>").append(i.getNombre()).append("</td>");
            sb.append("<td>").append(i.getCategoria() != null ? i.getCategoria().getNombre() : "—").append("</td>");
            sb.append("<td>").append(i.getProveedor() != null ? i.getProveedor().getNombre() : "—").append("</td>");
            sb.append("<td><strong>").append(i.getStock()).append("</strong></td>");
            sb.append("<td>").append(i.getStockMinimo()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");
        sb.append("<br><p style='color:#999;font-size:12px;'>Sistema de Inventario — Municipalidad de Escazú</p>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
