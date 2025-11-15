package carlos.mejia.fondaspringunirff.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class EmpleadoDto {
    private Integer idEmpleado;
    private String nombre;
    private Integer idPuesto; 
}