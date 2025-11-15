package carlos.mejia.fondaspringunirff.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class ReservaFeignDto {
    
    private Integer idReserva;
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Mexico_City")
    private Date fecha; 
    
    @JsonFormat(pattern = "HH:mm:ss", timezone = "America/Mexico_City")
    private Date hora; 
    
    private Integer idMesa; 
    private Integer idCliente;
}