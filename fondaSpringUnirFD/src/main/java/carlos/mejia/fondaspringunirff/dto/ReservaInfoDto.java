package carlos.mejia.fondaspringunirff.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservaInfoDto {
    private Integer idReserva;
    private Date fecha;
    private Date hora;
    private Integer idMesa; 
    private Integer idCliente;
}