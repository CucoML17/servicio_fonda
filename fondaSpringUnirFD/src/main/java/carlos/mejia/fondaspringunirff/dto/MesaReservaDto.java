package carlos.mejia.fondaspringunirff.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MesaReservaDto {
    private Integer idMesa;
    private Integer numero;
    private Integer capacidad;
    private String ubicacion;
}
