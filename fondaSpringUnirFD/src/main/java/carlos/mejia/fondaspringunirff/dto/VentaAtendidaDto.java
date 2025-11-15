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
public class VentaAtendidaDto {
    
    private Integer idventa;
    private LocalDate fechaventa;
    private double totalventa;
    private Integer idCliente;
    
    // NUEVO CAMPO AGREGADO
    private int estado; 
    
    private Integer idReserva;
}
