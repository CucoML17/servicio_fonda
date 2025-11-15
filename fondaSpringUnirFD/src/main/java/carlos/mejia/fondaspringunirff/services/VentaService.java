package carlos.mejia.fondaspringunirff.services;

import java.time.LocalDate;
import java.util.List;

import carlos.mejia.fondaspringunirff.dto.VentaAtendidaDto;
import carlos.mejia.fondaspringunirff.dto.VentaDetalleCompletoDto;
import carlos.mejia.fondaspringunirff.dto.VentaDto;
import carlos.mejia.fondaspringunirff.dto.VentaListDto;
import carlos.mejia.fondaspringunirff.dto.VentaRequestDto;
import carlos.mejia.fondaspringunirff.dto.VentaUpdateDto;

public interface VentaService {
	
	//Insertare venta
    VentaDto createVenta(VentaRequestDto ventaRequestDto);
    
    //Venta específica con su detalle
    VentaDto getVentaById(Integer idVenta);
    
    //Lista de ventas
    List<VentaListDto> getAllVentas();
    
    //Eliminar venta
    void deleteVenta(Integer idVenta);
    
    
    //Para generar PDF:
    byte[] generateTicketPdf(Integer idVenta);
    
    // NUEVO: Obtener detalle completo consolidado para el JSON
    VentaDetalleCompletoDto getDetalleCompleto(Integer idVenta);  
    
    
    //Ventas por fecha
    List<VentaListDto> getVentasByFecha(LocalDate fecha);
    
    
    VentaListDto getVentaByReservaId(Integer idReserva);
    
    
    
    
    
    //Obtener ventas atendidas por un empleado (con filtro opcional de fecha)
    List<VentaAtendidaDto> getVentasAtendidasByEmpleado(Integer idEmpleado, LocalDate fecha);    
    
    
    
    
    List<VentaListDto> getVentasByClienteIdAndFecha(Integer idCliente, LocalDate fecha);    
    
    
    // NUEVO: 1. Actualización simple de los atributos de la Venta
    VentaDto updateVenta(Integer idVenta, VentaUpdateDto ventaUpdateDto);
    
    // NUEVO: 2. Actualización completa: Venta y sus Detalles (reutilizando VentaRequestDto)
    VentaDto updateVentaCompleta(Integer idVenta, VentaRequestDto ventaRequestDto);    
}