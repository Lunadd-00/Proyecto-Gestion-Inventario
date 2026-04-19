package com.proyecto.GestionInventario.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;

import com.proyecto.GestionInventario.domain.Item;
import com.proyecto.GestionInventario.domain.Movimiento;
import com.proyecto.GestionInventario.repository.ItemRepository;
import com.proyecto.GestionInventario.repository.MovimientoRepository;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ItemRepository itemRepository;
    private final MovimientoRepository movimientoRepository;

    public ReporteService(ItemRepository itemRepository, MovimientoRepository movimientoRepository) {
        this.itemRepository = itemRepository;
        this.movimientoRepository = movimientoRepository;
    }

    // ================= PDF =================

    @Transactional(readOnly = true)
    public byte[] generarInventarioPdf(String usuario) {
        List<Item> items = itemRepository.findAll();

        Document doc = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            doc.add(new Paragraph("Reporte Inventario - " + usuario));
            doc.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(5);
            tabla.addCell("ID");
            tabla.addCell("Nombre");
            tabla.addCell("Categoría");
            tabla.addCell("Proveedor");
            tabla.addCell("Stock");

            for (Item i : items) {
                tabla.addCell(String.valueOf(i.getId()));
                tabla.addCell(i.getNombre());
                tabla.addCell(i.getCategoria().getNombre());
                tabla.addCell(i.getProveedor().getNombre());
                tabla.addCell(String.valueOf(i.getStock()));
            }

            doc.add(tabla);
            doc.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return out.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] generarMovimientosPdf(LocalDate inicio, LocalDate fin, String usuario) {
        List<Movimiento> movimientos = movimientoRepository.findAll().stream()
                .filter(m -> {
                    LocalDate f = m.getFecha().toLocalDate();
                    return !f.isBefore(inicio) && !f.isAfter(fin);
                })
                .toList();

        Document doc = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            doc.add(new Paragraph("Reporte Movimientos - " + usuario));
            doc.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(4);
            tabla.addCell("Fecha");
            tabla.addCell("Tipo");
            tabla.addCell("Item");
            tabla.addCell("Cantidad");

            for (Movimiento m : movimientos) {
                tabla.addCell(m.getFecha().format(FMT));
                tabla.addCell(m.getTipo().name());
                tabla.addCell(m.getItem().getNombre());
                tabla.addCell(String.valueOf(m.getCantidad()));
            }

            doc.add(tabla);
            doc.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return out.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] generarPorVencerPdf(int dias, String usuario) {
        LocalDate hoy = LocalDate.now();

        List<Item> items = itemRepository.findAll().stream()
                .filter(i -> i.isTieneCaducidad() && i.getFechaCaducidad() != null
                        && i.getFechaCaducidad().isBefore(hoy.plusDays(dias)))
                .toList();

        Document doc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            doc.add(new Paragraph("Items por vencer"));
            doc.add(new Paragraph(" "));

            for (Item i : items) {
                doc.add(new Paragraph(i.getNombre() + " - " + i.getFechaCaducidad()));
            }

            doc.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return out.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] generarStockBajoPdf(String usuario) {
        List<Item> items = itemRepository.findAll().stream()
                .filter(i -> i.getStock() <= i.getStockMinimo())
                .toList();

        Document doc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            doc.add(new Paragraph("Stock bajo"));
            doc.add(new Paragraph(" "));

            for (Item i : items) {
                doc.add(new Paragraph(i.getNombre() + " - " + i.getStock()));
            }

            doc.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return out.toByteArray();
    }

    // ================= EXCEL =================

    @Transactional(readOnly = true)
    public byte[] generarInventarioExcel() {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            Sheet hoja = wb.createSheet("Inventario");
            List<Item> items = itemRepository.findAll();

            int fila = 0;
            for (Item i : items) {
                Row row = hoja.createRow(fila++);
                row.createCell(0).setCellValue(i.getId());
                row.createCell(1).setCellValue(i.getNombre());
                row.createCell(2).setCellValue(i.getCategoria().getNombre());
                row.createCell(3).setCellValue(i.getProveedor().getNombre());
                row.createCell(4).setCellValue(i.getStock());
            }

            return toBytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generarMovimientosExcel(LocalDate inicio, LocalDate fin) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            Sheet hoja = wb.createSheet("Movimientos");

            List<Movimiento> movimientos = movimientoRepository.findAll();

            int fila = 0;
            for (Movimiento m : movimientos) {
                Row row = hoja.createRow(fila++);
                row.createCell(0).setCellValue(m.getFecha().format(FMT));
                row.createCell(1).setCellValue(m.getTipo().name());
                row.createCell(2).setCellValue(m.getItem().getNombre());
                row.createCell(3).setCellValue(m.getCantidad());
            }

            return toBytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteCompletoExcel() {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            Sheet hoja = wb.createSheet("Resumen");

            Row row = hoja.createRow(0);
            row.createCell(0).setCellValue("Reporte completo generado");

            return toBytes(wb);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ================= HELPER =================

    private byte[] toBytes(Workbook wb) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        return out.toByteArray();
    }
}