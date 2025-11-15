package carlos.mejia.fondaspringunirff.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import carlos.mejia.fondaspringunirff.dto.VentaAtendidaDto;
import carlos.mejia.fondaspringunirff.dto.VentaDetalleCompletoDto;
import carlos.mejia.fondaspringunirff.dto.VentaDto;
import carlos.mejia.fondaspringunirff.dto.VentaListDto;
import carlos.mejia.fondaspringunirff.dto.VentaRequestDto;
import carlos.mejia.fondaspringunirff.dto.VentaUpdateDto;
import carlos.mejia.fondaspringunirff.services.VentaService;
import lombok.AllArgsConstructor;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/ventas")
@AllArgsConstructor
public class VentaController {

    private final VentaService ventaService;
    
    //Hacer una venta
    @PostMapping("/guardar")
    public ResponseEntity<VentaDto> createVenta(@RequestBody VentaRequestDto ventaRequestDto) {
        VentaDto savedVenta = ventaService.createVenta(ventaRequestDto);
        return new ResponseEntity<>(savedVenta, HttpStatus.CREATED);
    }
    
	@GetMapping("/listat")
	public ResponseEntity<List<VentaListDto>> getAllTipo(){
		
		List<VentaListDto> listaTipos = ventaService.getAllVentas();
		return ResponseEntity.ok(listaTipos);
		
	}
	

    // OBTENER TODAS LAS VENTAS (Y ahora permite filtrar por fecha)
    @GetMapping("/listaf")
    public ResponseEntity<List<VentaListDto>> getAllVentas(
            @RequestParam(name = "fecha", required = false) LocalDate fecha) { // NUEVO PARÁMETRO
        
        List<VentaListDto> ventas;

        if (fecha != null) {
            // Si la fecha está presente, filtramos
            ventas = ventaService.getVentasByFecha(fecha);
        } else {
            // Si la fecha es null, devolvemos todas las ventas (comportamiento original)
            ventas = ventaService.getAllVentas();
        }

        return ResponseEntity.ok(ventas);
    }
    
    
    
    
    
    //Obtener una venta y su detalleventa
    @GetMapping("/buscaid/{id}")
    public ResponseEntity<VentaDto> getVentaById(@PathVariable("id") Integer id) {
        VentaDto venta = ventaService.getVentaById(id);
        return ResponseEntity.ok(venta);
    }
    
    //Borrando una venta (no debería pero la puse por cualquier cosa)
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> deleteVenta(@PathVariable("id") Integer id) {
        ventaService.deleteVenta(id);
        
        return ResponseEntity.ok("Venta eliminada exitosamente"); 
    } 
    
    
    //Para el pdf
    @GetMapping("/ticket/{id}")
    public ResponseEntity<byte[]> getTicketPdf(@PathVariable("id") Integer id) {
        
        // 1. Obtener los bytes del PDF
        byte[] pdfBytes = ventaService.generateTicketPdf(id);

        // 2. Configurar los Headers para el navegador
        HttpHeaders headers = new HttpHeaders();
        // Content-Type: indica que es un PDF
        headers.setContentType(MediaType.APPLICATION_PDF); 
        // Content-Disposition: indica que el navegador debe mostrar el archivo inline 
        // (dentro del navegador) y le da un nombre para la descarga.
        String filename = "ticket_venta_" + id + ".pdf";
        headers.setContentDispositionFormData("attachment", filename); // Usa "inline" o "attachment"

        // 3. Devolver los bytes con los headers
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
    
    
    
    @GetMapping("/detallecompleto/{idVenta}")
    public ResponseEntity<VentaDetalleCompletoDto> getDetalleCompleto(@PathVariable Integer idVenta) {
        
        // La lógica está centralizada en VentaServiceImpl
        VentaDetalleCompletoDto dto = ventaService.getDetalleCompleto(idVenta);
        
        return ResponseEntity.ok(dto);
    }
    
    
    @GetMapping("/ventareserva/{idReserva}")
    public ResponseEntity<VentaListDto> getVentaByReservaId(@PathVariable("idReserva") Integer idReserva) {
        
        VentaListDto venta = ventaService.getVentaByReservaId(idReserva);
        
        return ResponseEntity.ok(venta);
    }
    
    @GetMapping("/atendidas/{idEmpleado}")
    public ResponseEntity<List<VentaAtendidaDto>> getVentasAtendidas(
            @PathVariable Integer idEmpleado,
            @RequestParam(name = "fecha", required = false) LocalDate fecha) {
        
        List<VentaAtendidaDto> ventas = ventaService.getVentasAtendidasByEmpleado(idEmpleado, fecha);
        
        if (ventas.isEmpty()) {
             return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(ventas);
    }    
    
    
    
    
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<VentaListDto>> getVentasByClienteId(
            @PathVariable Integer idCliente,
            @RequestParam(name = "fecha", required = false) LocalDate fecha) {
        
        List<VentaListDto> ventas = ventaService.getVentasByClienteIdAndFecha(idCliente, fecha);
        
        if (ventas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(ventas);
    }    
    
    
    
    //---------------------------------------------------------------
    // 1. ACTUALIZACIÓN SIMPLE (Solo atributos de Venta principal)
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<VentaDto> updateVentaSimple(
            @PathVariable("id") Integer id,
            @RequestBody VentaUpdateDto ventaUpdateDto) {
        
        VentaDto updatedVenta = ventaService.updateVenta(id, ventaUpdateDto);
        return ResponseEntity.ok(updatedVenta);
    }

    // 2. ACTUALIZACIÓN COMPLETA (Venta y reemplazo total de DetalleVenta)
    @PutMapping("/actualizar/completa/{id}")
    public ResponseEntity<VentaDto> updateVentaCompleta(
            @PathVariable("id") Integer id,
            @RequestBody VentaRequestDto ventaRequestDto) {
        
        VentaDto updatedVenta = ventaService.updateVentaCompleta(id, ventaRequestDto);
        return ResponseEntity.ok(updatedVenta);
    }    
}