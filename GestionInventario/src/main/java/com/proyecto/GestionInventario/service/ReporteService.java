package com.proyecto.GestionInventario.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.proyecto.GestionInventario.domain.Item;
import com.proyecto.GestionInventario.domain.Lote;
import com.proyecto.GestionInventario.domain.Movimiento;
import com.proyecto.GestionInventario.repository.ItemRepository;
import com.proyecto.GestionInventario.repository.LoteRepository;
import com.proyecto.GestionInventario.repository.MovimientoRepository;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteService {

    
    private static final Color TURQUESA   = new Color(28,  182, 184);
    private static final Color MORADO     = new Color(107,  63, 209);
    private static final Color FILA_CLARA = new Color(240, 253, 253);
    private static final Color BLANCO     = Color.WHITE;

    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_D  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ItemRepository       itemRepository;
    private final MovimientoRepository movimientoRepository;
    private final LoteRepository       loteRepository;

    public ReporteService(ItemRepository itemRepository,
            MovimientoRepository movimientoRepository,
            LoteRepository loteRepository) {
        this.itemRepository       = itemRepository;
        this.movimientoRepository = movimientoRepository;
        this.loteRepository       = loteRepository;
    }


    private Font fontTitulo() {
        Font f = new Font(Font.HELVETICA, 16, Font.BOLD);
        f.setColor(MORADO);
        return f;
    }

    private Font fontSubtitulo() {
        Font f = new Font(Font.HELVETICA, 9, Font.ITALIC);
        f.setColor(Color.GRAY);
        return f;
    }

    private Font fontHeader() {
        Font f = new Font(Font.HELVETICA, 10, Font.BOLD);
        f.setColor(BLANCO);
        return f;
    }

    private Font fontBody() {
        return new Font(Font.HELVETICA, 9, Font.NORMAL);
    }

    private PdfPCell celdaHeader(String texto) {
        PdfPCell c = new PdfPCell(new Phrase(texto, fontHeader()));
        c.setBackgroundColor(TURQUESA);
        c.setPadding(6);
        c.setBorderColor(BLANCO);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        return c;
    }

    private PdfPCell celdaDato(String texto, int fila) {
        return celdaDatoColor(texto, fila % 2 == 0 ? BLANCO : FILA_CLARA);
    }

    private PdfPCell celdaDatoColor(String texto, Color bg) {
        PdfPCell c = new PdfPCell(new Phrase(texto != null ? texto : "—", fontBody()));
        c.setBackgroundColor(bg);
        c.setPadding(5);
        c.setBorderColor(new Color(210, 210, 210));
        return c;
    }

    private void addEncabezado(Document doc, String titulo, String subtitulo)
            throws DocumentException {
        doc.add(new Paragraph(titulo, fontTitulo()));
        if (subtitulo != null && !subtitulo.isBlank()) {
            doc.add(new Paragraph(subtitulo, fontSubtitulo()));
        }
        doc.add(new Paragraph(" "));
    }


    private XSSFCellStyle estiloHeader(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{28, (byte)182, (byte)184}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        setBordes(s);
        XSSFFont f = wb.createFont();
        f.setBold(true);
        f.setColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null));
        f.setFontHeightInPoints((short) 11);
        s.setFont(f);
        return s;
    }

    private XSSFCellStyle estiloFila(XSSFWorkbook wb, boolean striped) {
        XSSFCellStyle s = wb.createCellStyle();
        if (striped) {
            s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)240, (byte)253, (byte)253}, null));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        setBordes(s);
        return s;
    }

    private void setBordes(XSSFCellStyle s) {
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
    }

    private void crearHeaderRow(Sheet hoja, XSSFCellStyle estilo, String... cols) {
        Row row = hoja.createRow(0);
        row.setHeight((short) 450);
        for (int i = 0; i < cols.length; i++) {
            Cell c = row.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(estilo);
        }
    }

    private void setCell(Row row, int col, String valor, CellStyle estilo) {
        Cell c = row.createCell(col);
        c.setCellValue(valor != null ? valor : "—");
        c.setCellStyle(estilo);
    }

    private void autoancho(Sheet hoja, int cols) {
        for (int i = 0; i < cols; i++) hoja.autoSizeColumn(i);
    }

    private byte[] toBytes(Workbook wb) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        return out.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] generarInventarioPdf(String usuario) {
        List<Item> items = itemRepository.findAll();
        Document doc = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(doc, out);
            doc.open();
            addEncabezado(doc, "Reporte de Inventario General",
                    "Generado por: " + usuario + "  |  " + LocalDate.now().format(FMT_D)
                    + "  |  Total ítems: " + items.size());

            PdfPTable tabla = new PdfPTable(6);
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(4);
            tabla.addCell(celdaHeader("ID"));
            tabla.addCell(celdaHeader("Nombre"));
            tabla.addCell(celdaHeader("Categoría"));
            tabla.addCell(celdaHeader("Proveedor"));
            tabla.addCell(celdaHeader("Stock"));
            tabla.addCell(celdaHeader("Stock Mínimo"));

            int fila = 0;
            for (Item i : items) {
                tabla.addCell(celdaDato(String.valueOf(i.getId()), fila));
                tabla.addCell(celdaDato(i.getNombre(), fila));
                tabla.addCell(celdaDato(i.getCategoria() != null ? i.getCategoria().getNombre() : "—", fila));
                tabla.addCell(celdaDato(i.getProveedor() != null ? i.getProveedor().getNombre() : "—", fila));
                tabla.addCell(celdaDato(String.valueOf(i.getStock()), fila));
                tabla.addCell(celdaDato(String.valueOf(i.getStockMinimo()), fila));
                fila++;
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
        List<Movimiento> movs = movimientoRepository.findAll().stream()
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
            addEncabezado(doc, "Reporte de Movimientos",
                    "Período: " + inicio.format(FMT_D) + " — " + fin.format(FMT_D)
                    + "  |  Generado por: " + usuario + "  |  Total: " + movs.size());

            PdfPTable tabla = new PdfPTable(6);
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(4);
            tabla.addCell(celdaHeader("Fecha"));
            tabla.addCell(celdaHeader("Tipo"));
            tabla.addCell(celdaHeader("Ítem"));
            tabla.addCell(celdaHeader("Cantidad"));
            tabla.addCell(celdaHeader("Motivo"));
            tabla.addCell(celdaHeader("Usuario"));

            int fila = 0;
            for (Movimiento m : movs) {
                Color bg = switch (m.getTipo().name()) {
                    case "ENTRADA" -> new Color(220, 255, 220);
                    case "SALIDA"  -> new Color(255, 220, 220);
                    default        -> fila % 2 == 0 ? BLANCO : FILA_CLARA;
                };
                tabla.addCell(celdaDatoColor(m.getFecha().format(FMT_DT), bg));
                tabla.addCell(celdaDatoColor(m.getTipo().name(), bg));
                tabla.addCell(celdaDatoColor(m.getItem() != null ? m.getItem().getNombre() : "(eliminado)", bg));
                tabla.addCell(celdaDatoColor(String.valueOf(m.getCantidad()), bg));
                tabla.addCell(celdaDatoColor(m.getMotivo(), bg));
                tabla.addCell(celdaDatoColor(m.getUsuario() != null ? m.getUsuario().getNombre() : "—", bg));
                fila++;
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
        LocalDate hoy    = LocalDate.now();
        LocalDate limite = hoy.plusDays(dias);
        List<Lote> lotes = loteRepository.findLotesPorVencer(hoy, limite).stream()
                .filter(l -> l.getItem() != null)
                .toList();

        Document doc = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(doc, out);
            doc.open();
            addEncabezado(doc, "Lotes por Vencer — Próximos " + dias + " días",
                    "Generado por: " + usuario + "  |  " + hoy.format(FMT_D)
                    + "  |  Total lotes: " + lotes.size());

            if (lotes.isEmpty()) {
                doc.add(new Paragraph("No hay lotes próximos a vencer en el período indicado.",
                        fontSubtitulo()));
            } else {
                PdfPTable tabla = new PdfPTable(6);
                tabla.setWidthPercentage(100);
                tabla.setSpacingBefore(4);
                tabla.addCell(celdaHeader("Ítem"));
                tabla.addCell(celdaHeader("Lote"));
                tabla.addCell(celdaHeader("Bodega"));
                tabla.addCell(celdaHeader("Cantidad"));
                tabla.addCell(celdaHeader("Vencimiento"));
                tabla.addCell(celdaHeader("Días Restantes"));

                int fila = 0;
                for (Lote l : lotes) {
                    long diasRest = ChronoUnit.DAYS.between(hoy, l.getFechaCaducidad());
                    Color bg = diasRest <= 7
                            ? new Color(255, 214, 214)
                            : new Color(255, 249, 230);
                    tabla.addCell(celdaDatoColor(l.getItem().getNombre(), bg));
                    tabla.addCell(celdaDatoColor(
                            l.getNumeroLote() != null ? l.getNumeroLote() : "Lote #" + l.getId(), bg));
                    tabla.addCell(celdaDatoColor(
                            l.getBodega() != null ? l.getBodega().getNombre() : "—", bg));
                    tabla.addCell(celdaDatoColor(String.valueOf(l.getCantidad()), bg));
                    tabla.addCell(celdaDatoColor(l.getFechaCaducidad().format(FMT_D), bg));
                    tabla.addCell(celdaDatoColor(diasRest + " días", bg));
                    fila++;
                }
                doc.add(tabla);
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
                .filter(i -> i.isActivo() && i.getStock() <= i.getStockMinimo())
                .toList();

        Document doc = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(doc, out);
            doc.open();
            addEncabezado(doc, "Reporte de Stock Bajo",
                    "Generado por: " + usuario + "  |  " + LocalDate.now().format(FMT_D)
                    + "  |  Total: " + items.size());

            if (items.isEmpty()) {
                doc.add(new Paragraph("No hay ítems con stock bajo actualmente.", fontSubtitulo()));
            } else {
                PdfPTable tabla = new PdfPTable(5);
                tabla.setWidthPercentage(100);
                tabla.setSpacingBefore(4);
                tabla.addCell(celdaHeader("Ítem"));
                tabla.addCell(celdaHeader("Categoría"));
                tabla.addCell(celdaHeader("Stock Actual"));
                tabla.addCell(celdaHeader("Stock Mínimo"));
                tabla.addCell(celdaHeader("Estado"));

                int fila = 0;
                for (Item i : items) {
                    Color bg = i.getStock() == 0
                            ? new Color(255, 214, 214)
                            : new Color(255, 243, 205);
                    String estado = i.getStock() == 0 ? "AGOTADO" : "BAJO";
                    tabla.addCell(celdaDatoColor(i.getNombre(), bg));
                    tabla.addCell(celdaDatoColor(
                            i.getCategoria() != null ? i.getCategoria().getNombre() : "—", bg));
                    tabla.addCell(celdaDatoColor(String.valueOf(i.getStock()), bg));
                    tabla.addCell(celdaDatoColor(String.valueOf(i.getStockMinimo()), bg));
                    tabla.addCell(celdaDatoColor(estado, bg));
                    fila++;
                }
                doc.add(tabla);
            }
            doc.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] generarInventarioExcel() {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet hoja = wb.createSheet("Inventario");
            XSSFCellStyle hdr  = estiloHeader(wb);
            XSSFCellStyle even = estiloFila(wb, false);
            XSSFCellStyle odd  = estiloFila(wb, true);

            crearHeaderRow(hoja, hdr, "ID", "Nombre", "Categoría", "Proveedor", "Stock", "Stock Mínimo");

            List<Item> items = itemRepository.findAll();
            int fila = 1;
            for (Item i : items) {
                Row row = hoja.createRow(fila);
                CellStyle s = fila % 2 == 0 ? even : odd;
                setCell(row, 0, String.valueOf(i.getId()), s);
                setCell(row, 1, i.getNombre(), s);
                setCell(row, 2, i.getCategoria() != null ? i.getCategoria().getNombre() : "—", s);
                setCell(row, 3, i.getProveedor() != null ? i.getProveedor().getNombre() : "—", s);
                setCell(row, 4, String.valueOf(i.getStock()), s);
                setCell(row, 5, String.valueOf(i.getStockMinimo()), s);
                fila++;
            }
            autoancho(hoja, 6);
            return toBytes(wb);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generarMovimientosExcel(LocalDate inicio, LocalDate fin) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet hoja = wb.createSheet("Movimientos");
            XSSFCellStyle hdr  = estiloHeader(wb);
            XSSFCellStyle even = estiloFila(wb, false);
            XSSFCellStyle odd  = estiloFila(wb, true);

            crearHeaderRow(hoja, hdr,
                    "Fecha", "Tipo", "Ítem", "Cantidad",
                    "Motivo", "Bodega Origen", "Bodega Destino", "Usuario");

            List<Movimiento> movs = movimientoRepository.findAll().stream()
                    .filter(m -> {
                        LocalDate f = m.getFecha().toLocalDate();
                        return !f.isBefore(inicio) && !f.isAfter(fin);
                    })
                    .toList();

            int fila = 1;
            for (Movimiento m : movs) {
                Row row = hoja.createRow(fila);
                CellStyle s = fila % 2 == 0 ? even : odd;
                setCell(row, 0, m.getFecha().format(FMT_DT), s);
                setCell(row, 1, m.getTipo().name(), s);
                setCell(row, 2, m.getItem() != null ? m.getItem().getNombre() : "(eliminado)", s);
                setCell(row, 3, String.valueOf(m.getCantidad()), s);
                setCell(row, 4, m.getMotivo(), s);
                setCell(row, 5, m.getBodegaOrigen()  != null ? m.getBodegaOrigen().getNombre()  : "—", s);
                setCell(row, 6, m.getBodegaDestino() != null ? m.getBodegaDestino().getNombre() : "—", s);
                setCell(row, 7, m.getUsuario() != null ? m.getUsuario().getNombre() : "—", s);
                fila++;
            }
            autoancho(hoja, 8);
            return toBytes(wb);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteCompletoExcel() {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFCellStyle hdr  = estiloHeader(wb);
            XSSFCellStyle even = estiloFila(wb, false);
            XSSFCellStyle odd  = estiloFila(wb, true);

            Sheet h1 = wb.createSheet("Inventario");
            crearHeaderRow(h1, hdr, "ID", "Nombre", "Categoría", "Proveedor", "Stock", "Stock Mínimo");
            List<Item> items = itemRepository.findAll();
            int f = 1;
            for (Item i : items) {
                Row row = h1.createRow(f);
                CellStyle s = f % 2 == 0 ? even : odd;
                setCell(row, 0, String.valueOf(i.getId()), s);
                setCell(row, 1, i.getNombre(), s);
                setCell(row, 2, i.getCategoria() != null ? i.getCategoria().getNombre() : "—", s);
                setCell(row, 3, i.getProveedor() != null ? i.getProveedor().getNombre() : "—", s);
                setCell(row, 4, String.valueOf(i.getStock()), s);
                setCell(row, 5, String.valueOf(i.getStockMinimo()), s);
                f++;
            }
            autoancho(h1, 6);

            Sheet h2 = wb.createSheet("Movimientos");
            crearHeaderRow(h2, hdr, "Fecha", "Tipo", "Ítem", "Cantidad",
                    "Motivo", "Bodega Origen", "Bodega Destino", "Usuario");
            List<Movimiento> movs = movimientoRepository.findAll();
            f = 1;
            for (Movimiento m : movs) {
                Row row = h2.createRow(f);
                CellStyle s = f % 2 == 0 ? even : odd;
                setCell(row, 0, m.getFecha().format(FMT_DT), s);
                setCell(row, 1, m.getTipo().name(), s);
                setCell(row, 2, m.getItem() != null ? m.getItem().getNombre() : "(eliminado)", s);
                setCell(row, 3, String.valueOf(m.getCantidad()), s);
                setCell(row, 4, m.getMotivo(), s);
                setCell(row, 5, m.getBodegaOrigen()  != null ? m.getBodegaOrigen().getNombre()  : "—", s);
                setCell(row, 6, m.getBodegaDestino() != null ? m.getBodegaDestino().getNombre() : "—", s);
                setCell(row, 7, m.getUsuario() != null ? m.getUsuario().getNombre() : "—", s);
                f++;
            }
            autoancho(h2, 8);

            Sheet h3 = wb.createSheet("Por Vencer (30 días)");
            crearHeaderRow(h3, hdr, "Ítem", "Lote", "Bodega", "Cantidad", "Vencimiento", "Días Restantes");
            LocalDate hoy   = LocalDate.now();
            List<Lote> lotes = loteRepository.findLotesPorVencer(hoy, hoy.plusDays(30)).stream()
                    .filter(l -> l.getItem() != null)
                    .toList();
            f = 1;
            for (Lote l : lotes) {
                Row row = h3.createRow(f);
                CellStyle s = f % 2 == 0 ? even : odd;
                long diasRest = ChronoUnit.DAYS.between(hoy, l.getFechaCaducidad());
                setCell(row, 0, l.getItem().getNombre(), s);
                setCell(row, 1, l.getNumeroLote() != null ? l.getNumeroLote() : "Lote #" + l.getId(), s);
                setCell(row, 2, l.getBodega() != null ? l.getBodega().getNombre() : "—", s);
                setCell(row, 3, String.valueOf(l.getCantidad()), s);
                setCell(row, 4, l.getFechaCaducidad().format(FMT_D), s);
                setCell(row, 5, diasRest + " días", s);
                f++;
            }
            autoancho(h3, 6);

            Sheet h4 = wb.createSheet("Stock Bajo");
            crearHeaderRow(h4, hdr, "Ítem", "Categoría", "Stock Actual", "Stock Mínimo", "Estado");
            List<Item> stockBajo = itemRepository.findAll().stream()
                    .filter(i -> i.isActivo() && i.getStock() <= i.getStockMinimo())
                    .toList();
            f = 1;
            for (Item i : stockBajo) {
                Row row = h4.createRow(f);
                CellStyle s = f % 2 == 0 ? even : odd;
                setCell(row, 0, i.getNombre(), s);
                setCell(row, 1, i.getCategoria() != null ? i.getCategoria().getNombre() : "—", s);
                setCell(row, 2, String.valueOf(i.getStock()), s);
                setCell(row, 3, String.valueOf(i.getStockMinimo()), s);
                setCell(row, 4, i.getStock() == 0 ? "AGOTADO" : "BAJO", s);
                f++;
            }
            autoancho(h4, 5);

            return toBytes(wb);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
