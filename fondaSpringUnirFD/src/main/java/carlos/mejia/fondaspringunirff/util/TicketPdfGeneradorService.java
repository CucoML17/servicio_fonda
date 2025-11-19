package carlos.mejia.fondaspringunirff.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import carlos.mejia.fondaspringunirff.dto.DetalleVentaDto;
import carlos.mejia.fondaspringunirff.dto.MesaReservaDto;
import carlos.mejia.fondaspringunirff.dto.ProductoDto;
import carlos.mejia.fondaspringunirff.dto.ReservaFeignDto;
import carlos.mejia.fondaspringunirff.dto.VentaDto;
import carlos.mejia.fondaspringunirff.feign.ReservarFeignClient;
import carlos.mejia.fondaspringunirff.services.ProductoService;
import lombok.RequiredArgsConstructor;

@Service("ticketPdfGeneratorService")
@RequiredArgsConstructor
public class TicketPdfGeneradorService {
 
	private final ProductoService productoService;
	private final ReservarFeignClient reservarFeignClient;
	
	private static final TimeZone MEXICO_CITY_TIMEZONE = TimeZone.getTimeZone("America/Mexico_City");
	
	// Formateadores para java.util.Date de los Feign DTOs
	private  SimpleDateFormat dateFormat;
    private  SimpleDateFormat timeFormat;
    
    {
        // 1. Inicializa y establece la Zona Horaria para la fecha
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setTimeZone(MEXICO_CITY_TIMEZONE); // <--- APLICACIÓN CLAVE
        
        // 2. Inicializa y establece la Zona Horaria para la hora
        timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setTimeZone(MEXICO_CITY_TIMEZONE); // <--- APLICACIÓN CLAVE
    }    

    public byte[] generateTicketPdf(VentaDto venta, String nombreCliente, String nombreEmpleado) {
   
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
     
      Document document = new Document();
      document.setPageSize(new org.openpdf.text.Rectangle(280, 462));
      document.setMargins(10, 10, 10, 10); // Márgenes pequeños
     
      PdfWriter.getInstance(document, outputStream);
      document.open();

      // --- 1. CONFIGURACIÓN DE FUENTES Y ESTILOS ---
      Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD);
      Font fontEslogan = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7, Font.ITALIC);
      Font fontSeparador = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Font.NORMAL);
      Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Font.NORMAL);
      Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL);
      Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.NORMAL);
     
      String separador = "***********************************************************************************";

      // --- 2. CABECERA Y DATOS DE EMPRESA ---
      Paragraph titulo = new Paragraph("Restaurante Cuco Corporation", fontTitulo);
      titulo.setAlignment(Element.ALIGN_CENTER);
      document.add(titulo);

      Paragraph eslogan = new Paragraph("Enfrenta el hambre, construye la experiencia", fontEslogan);
      eslogan.setAlignment(Element.ALIGN_CENTER);
      document.add(eslogan);

      Paragraph direccion = new Paragraph("Col. los Manantiales, Calle Lago de Texcoco. Av Luperg", fontNormal);
      direccion.setAlignment(Element.ALIGN_CENTER);
      direccion.setSpacingAfter(5);
      document.add(direccion);
     
      document.add(new Paragraph(separador, fontSeparador));

      // --- 3. INFORMACIÓN DEL TICKET Y VENTA ---
      Paragraph ticketHeader = new Paragraph("TICKET DE COMPRA", fontHeader);
      ticketHeader.setAlignment(Element.ALIGN_CENTER);
      ticketHeader.setSpacingAfter(0f);
      ticketHeader.setSpacingBefore(-2f);
      document.add(ticketHeader);

      Paragraph sep2 = new Paragraph(separador, fontSeparador);
      sep2.setSpacingAfter(0f);
      document.add(sep2);

      // --- 4. INFORMACIÓN DE VENTA (Fecha, Cliente, Empleado) ---
      PdfPTable infoTable = new PdfPTable(1);
      infoTable.setWidthPercentage(90); 
      infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
      infoTable.setSpacingAfter(3f);
     
      // Formateador para LocalDate de VentaDto
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
     
      infoTable.addCell(createInfoCell("Fecha Venta:", venta.getFechaventa().format(formatter), fontHeader, fontNormal));
      infoTable.addCell(createInfoCell("Cliente:", nombreCliente, fontHeader, fontNormal));
      infoTable.addCell(createInfoCell("Atendido por:", nombreEmpleado, fontHeader, fontNormal));     
     
      document.add(infoTable);
            
            // --- LÓGICA DE RESERVACIÓN DETALLADA (Si existe idReserva) ---
            if (venta.getIdReserva() != null) {
                
                ReservaFeignDto reserva = null;
                MesaReservaDto mesa = null;
                
                try {
                    // 1. Obtener datos de la Reserva
                    reserva = reservarFeignClient.getReservaById(venta.getIdReserva());
                    
                    // 2. Obtener datos de la Mesa
                    if (reserva != null && reserva.getIdMesa() != null) {
                        mesa = reservarFeignClient.getMesaById(reserva.getIdMesa());
                    }

                } catch (Exception e) {
                     // Si falla Feign, se maneja imprimiendo el error y se añade un mensaje de fallo.
                     System.err.println("Error al obtener datos de reserva/mesa vía Feign: " + e.getMessage());
                }

                if (reserva != null) {
                    
                    // Separador y título de la sección de reserva
                    Paragraph sepReserva = new Paragraph(separador, fontSeparador);
                    sepReserva.setSpacingBefore(1f);
                    document.add(sepReserva);
                    
                    Paragraph reservaHeader = new Paragraph("DATOS DE RESERVACIÓN", fontHeader);
                    reservaHeader.setAlignment(Element.ALIGN_CENTER);
                    reservaHeader.setSpacingAfter(0f);
                    reservaHeader.setSpacingBefore(-2f);
                    document.add(reservaHeader);

                    Paragraph sepReserva2 = new Paragraph(separador, fontSeparador);
                    sepReserva2.setSpacingAfter(3f);
                    document.add(sepReserva2);

                    // MODIFICACIÓN CLAVE AQUÍ: Usaremos una tabla de 2 columnas para Fecha/Hora
                    PdfPTable reservaInfoTable = new PdfPTable(2); // CAMBIO: De 1 a 2 columnas
                    reservaInfoTable.setWidthPercentage(90);
                    // Distribuimos el ancho de las 2 columnas: 50% y 50%
                    reservaInfoTable.setWidths(new float[]{1f, 2f}); //----------------------------------------------------
                    reservaInfoTable.setHorizontalAlignment(Element.ALIGN_CENTER);

                    // Formateamos las java.util.Date
                    String fechaReserva = dateFormat.format(reserva.getFecha());
                    String horaReserva = timeFormat.format(reserva.getHora());
                    
                    // 1. ID de Reserva (Sigue usando 2 celdas combinadas)
                    PdfPCell idReservaCell = new PdfPCell(createInfoPhrase("Reserva ID:", String.valueOf(reserva.getIdReserva()), fontHeader, fontNormal));
                    idReservaCell.setColspan(2); // Combina las 2 columnas en 1
                    idReservaCell.setPadding(3);
                    idReservaCell.setBorder(0);
                    idReservaCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    reservaInfoTable.addCell(idReservaCell);
                    
                    // 2. FECHA Y HORA (Separadas en la misma fila)
                    // Celda 1 (Fecha)
                    reservaInfoTable.addCell(createInfoCell("Fecha:", fechaReserva, fontHeader, fontNormal));
                    
                    // Celda 2 (Hora) - Usa una celda auxiliar con alineación a la derecha
                    reservaInfoTable.addCell(createRightAlignedInfoCell("Hora:", horaReserva, fontHeader, fontNormal)); // Nueva celda auxiliar
                    
                    
                    // 3. DATOS DE LA MESA (Siguen usando 2 celdas combinadas)
                    if (mesa != null) {
                        // Mesa Nro
                        PdfPCell nroMesaCell = new PdfPCell(createInfoPhrase("Mesa Nro:", String.valueOf(mesa.getNumero()), fontHeader, fontNormal));
                        nroMesaCell.setColspan(2); 
                        nroMesaCell.setPadding(3);
                        nroMesaCell.setBorder(0);
                        nroMesaCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        reservaInfoTable.addCell(nroMesaCell);
                        
                        // Capacidad y Ubicación (Siguen siendo de 2 celdas combinadas)
                        reservaInfoTable.addCell(createInfoCell("Capacidad:", String.valueOf(mesa.getCapacidad()) + " pers", fontHeader, fontNormal));
                        reservaInfoTable.addCell(createInfoCell("Ubicación:", mesa.getUbicacion(), fontHeader, fontNormal));
                        
                    } else {
                         PdfPCell mesaErrorCell = new PdfPCell(createInfoPhrase("Mesa Nro:", "N/A (Mesa no encontrada)", fontHeader, fontNormal));
                         mesaErrorCell.setColspan(2);
                         mesaErrorCell.setPadding(3);
                         mesaErrorCell.setBorder(0);
                         mesaErrorCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                         reservaInfoTable.addCell(mesaErrorCell);
                    }
                    
                    reservaInfoTable.setSpacingAfter(0f); 
                    document.add(reservaInfoTable);
                } else {
                     // Si falla la carga de la reserva
                    PdfPTable errorTable = new PdfPTable(1);
                    errorTable.setWidthPercentage(90);
                    errorTable.addCell(createInfoCell("Reserva ID:", String.valueOf(venta.getIdReserva()), fontHeader, fontNormal));
                    errorTable.addCell(createInfoCell("Detalle Reserva:", "ERROR DE CONEXIÓN (M/S Reservaciones)", fontHeader, fontNormal));
                    errorTable.setSpacingAfter(3f);
                    document.add(errorTable);
                }
            }
      

      // --- 5. TABLA DE DETALLES DE VENTA ---
            // Usamos un separador que también actúa como inicio de la sección de productos
      Paragraph sep3 = new Paragraph(separador, fontSeparador);
      sep3.setSpacingAfter(3f);
      document.add(sep3);
     

      // 5 columnas: #, Producto, P. Unitario, Cantidad, Subtotal
      PdfPTable table = new PdfPTable(5);
      table.setWidthPercentage(100);
      table.setWidths(new float[]{1f, 4f, 2.5f, 1.5f, 3f});
      table.setSpacingAfter(3f);
      table.setSpacingBefore(2f);
     
      // Encabezados de la tabla
      addTableHeader(table, "#", fontHeader, Element.ALIGN_CENTER);
      addTableHeader(table, "Producto", fontHeader, Element.ALIGN_LEFT);
      addTableHeader(table, "P. Unitario", fontHeader, Element.ALIGN_RIGHT);
      addTableHeader(table, "Cant.", fontHeader, Element.ALIGN_CENTER);
      addTableHeader(table, "Subtotal", fontHeader, Element.ALIGN_RIGHT);
     
      AtomicInteger itemNumber = new AtomicInteger(1);

      for (DetalleVentaDto detalle : venta.getDetallesVenta()) {
       
      	String nombreProducto = "Producto Desconocido";
        try {
          // Usamos el servicio local para obtener el DTO del producto
          ProductoDto producto = productoService.getProductoById(detalle.getIdProducto());
          if (producto != null) {
            nombreProducto = producto.getNombre();
          }
        } catch (Exception e) {
          nombreProducto = "Error al buscar P-ID: " + detalle.getIdProducto();
        }
       
        double precioUnitario = detalle.getSubtotal() / detalle.getCantidad();
       
        //Fila de producto
        table.addCell(createCell(String.valueOf(itemNumber.getAndIncrement()), fontNormal, Element.ALIGN_CENTER));
        table.addCell(createCell(nombreProducto, fontNormal, Element.ALIGN_LEFT));
        table.addCell(createCell(String.format("$%.2f", precioUnitario), fontNormal, Element.ALIGN_RIGHT));
        table.addCell(createCell(String.valueOf(detalle.getCantidad()), fontNormal, Element.ALIGN_CENTER));
        table.addCell(createCell(String.format("$%.2f", detalle.getSubtotal()), fontNormal, Element.ALIGN_RIGHT));
      }
      document.add(table);
     
      // --- 6. RESUMEN DEL TOTAL ---
      document.add(new Paragraph(separador, fontSeparador));
     
      Paragraph total = new Paragraph(new Phrase("TOTAL VENTA: ", fontTotal));
      total.add(new Phrase(String.format("$%.2f", venta.getTotalventa()), fontTotal));
     
      total.setAlignment(Element.ALIGN_RIGHT);
      total.setSpacingAfter(0f);
      total.setSpacingBefore(-2f);
      
      document.add(total);
     
      document.add(new Paragraph(separador, fontSeparador));

      // --- 7. PIE DE PÁGINA ---
      Paragraph gracias = new Paragraph("¡Gracias por su compra!", fontHeader);
      gracias.setAlignment(Element.ALIGN_CENTER);
      document.add(gracias);

      document.close();
     
      return outputStream.toByteArray();

    } catch (IOException | org.openpdf.text.DocumentException e) {
      throw new RuntimeException("Error al generar el ticket PDF: " + e.getMessage(), e);
    }
  }

  // Método auxiliar para crear celdas de encabezado
  private void addTableHeader(PdfPTable table, String headerText, Font font, int alignment) {
    PdfPCell header = new PdfPCell(new Phrase(headerText, font));
    header.setHorizontalAlignment(alignment);
    header.setPadding(3);
    table.addCell(header);
  }
 
  // Método auxiliar para crear celdas de contenido (Tabla de productos)
  private PdfPCell createCell(String content, Font font, int alignment) {
    PdfPCell cell = new PdfPCell(new Phrase(content, font));
    cell.setHorizontalAlignment(alignment);
    cell.setPadding(2);
    cell.setBorder(0); // Para un look de ticket más limpio
    return cell;
  }
 
  // Método auxiliar para crear celdas de información clave/valor (Parte central)
  private PdfPCell createInfoCell(String label, String value, Font labelFont, Font valueFont) {
    // Concatenamos el label en negrita con el valor normal
    Phrase phrase = new Phrase();
    phrase.add(new Phrase(label + " ", labelFont));
    phrase.add(new Phrase(value, valueFont));
    
   
    PdfPCell cell = new PdfPCell(phrase);
    cell.setPadding(3);
    cell.setBorder(0); // Quitamos el borde para un diseño limpio
    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
    return cell;
  }
  
//Método auxiliar para crear la frase combinada (label + value)
  private Phrase createInfoPhrase(String label, String value, Font labelFont, Font valueFont) {
      Phrase phrase = new Phrase();
      phrase.add(new Phrase(label + " ", labelFont));
      phrase.add(new Phrase(value, valueFont));
      return phrase;
  }


  
  // Método auxiliar para crear celdas de información CLAVE/VALOR
  // Alinea el valor a la DERECHA, manteniendo el label a la izquierda del contenido.
  private PdfPCell createRightAlignedInfoCell(String label, String value, Font labelFont, Font valueFont) {
      PdfPCell cell = new PdfPCell(createInfoPhrase(label, value, labelFont, valueFont));
      cell.setPadding(3);
      cell.setBorder(0);
      // Alineación del contenido de la celda a la derecha
      cell.setHorizontalAlignment(Element.ALIGN_LEFT); 
      return cell;
  }
}