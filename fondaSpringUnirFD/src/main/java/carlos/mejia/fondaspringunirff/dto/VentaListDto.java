package carlos.mejia.fondaspringunirff.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaListDto {
	//Este dto está para cuando se consulta todas las ventas, solo se obtienen los datos de la venta simple
	//sin sus relaciones.
	
	private Integer idventa;
	private LocalDate fechaventa;
	private double totalventa;
	private Integer idCliente;
	

    private Integer idReserva;
    
    
	
}