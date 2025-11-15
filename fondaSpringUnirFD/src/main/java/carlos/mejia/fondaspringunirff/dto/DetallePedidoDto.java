package carlos.mejia.fondaspringunirff.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoDto {
    
    private Integer idProducto;
    private Integer cantidad;
}