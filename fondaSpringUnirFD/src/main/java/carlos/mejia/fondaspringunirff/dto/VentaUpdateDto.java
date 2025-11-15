package carlos.mejia.fondaspringunirff.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaUpdateDto {
    
    // LocalDate fechaventa; // Generalmente no se cambia en un UPDATE
    // double totalventa; // Se recalcula en la lógica si se cambian los detalles
    
    // Campos que el usuario puede querer modificar
    private Integer idCliente;
    private Integer idReserva;
    private int estado; // Campo clave a modificar
}
