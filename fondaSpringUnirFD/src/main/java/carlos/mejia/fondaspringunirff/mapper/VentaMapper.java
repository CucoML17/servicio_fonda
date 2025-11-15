package carlos.mejia.fondaspringunirff.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import carlos.mejia.fondaspringunirff.dto.DetalleVentaDto;
import carlos.mejia.fondaspringunirff.dto.VentaAtendidaDto;
import carlos.mejia.fondaspringunirff.dto.VentaDto;
import carlos.mejia.fondaspringunirff.dto.VentaListDto;
import carlos.mejia.fondaspringunirff.dto.VentaRequestDto;
import carlos.mejia.fondaspringunirff.entity.Venta;

public class VentaMapper {

	//Entidad a DTO.
    public static VentaDto mapToVentaDto(Venta venta) {
        
    	//Pasa los detalle venta también
        Set<DetalleVentaDto> detallesVentaDto = (venta.getDetallesVenta() != null)
                ? venta.getDetallesVenta().stream()
                    .map(DetalleVentaMapper::mapToDetalleVentaDto)
                    .collect(Collectors.toSet())
                : null;
        
        return new VentaDto(
            venta.getIdventa(),
            venta.getFechaventa(),
            venta.getTotalventa(),
            venta.getIdCliente(),
            venta.getIdReserva(),
            detallesVentaDto
        );
    }

    public static VentaListDto mapToVentaListDto(Venta venta) {
        if (venta == null) return null;
        
        return new VentaListDto(
            venta.getIdventa(),
            venta.getFechaventa(),
            venta.getTotalventa(),
            venta.getIdCliente(),
            venta.getIdReserva()
        );
    }
    
    //DTO a Entidad:
    public static Venta mapToVenta(VentaRequestDto ventaRequestDto) {
        if (ventaRequestDto == null) return null;
        
        Venta venta = new Venta();
        
        venta.setIdCliente(ventaRequestDto.getIdCliente());
        venta.setIdReserva(ventaRequestDto.getIdReserva());
        
        return venta;
    }    
    
    
    
    public static VentaAtendidaDto mapToVentaAtendidaDto(Venta venta) {
        if (venta == null) return null;
        
        return new VentaAtendidaDto(
            venta.getIdventa(),
            venta.getFechaventa(),
            venta.getTotalventa(),
            venta.getIdCliente(),
            venta.getEstado(), //Se incluye el nuevo campo estado
            venta.getIdReserva()
        );
    }    
}