package carlos.mejia.fondaspringunirff.mapper;

import carlos.mejia.fondaspringunirff.dto.DetalleVentaDto;
import carlos.mejia.fondaspringunirff.entity.DetalleVenta;

public class DetalleVentaMapper {

    public static DetalleVentaDto mapToDetalleVentaDto(DetalleVenta detalleVenta) {
        return new DetalleVentaDto(
            detalleVenta.getId().getIdProducto(),
            detalleVenta.getCantidad(),
            detalleVenta.getSubtotal()
        );
    }

    
}