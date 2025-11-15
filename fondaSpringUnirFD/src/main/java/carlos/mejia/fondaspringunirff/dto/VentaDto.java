package carlos.mejia.fondaspringunirff.dto;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaDto {

    private Integer idventa;
    private LocalDate fechaventa;
    private double totalventa;
    private Integer idCliente; 
    
    
    private Integer idReserva;    
    
    private Set<DetalleVentaDto> detallesVenta;
}