package carlos.mejia.fondaspringunirff.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequestDto {
    //Este DTO sí obtiene los detalles de la ventas
    private Integer idCliente;
    private List<DetallePedidoDto> productos;
    
    
    private Integer idReserva;    
}