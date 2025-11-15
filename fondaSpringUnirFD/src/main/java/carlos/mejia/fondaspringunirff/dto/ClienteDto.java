package carlos.mejia.fondaspringunirff.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDto {
	private Integer idcliente;

    private String nombrecliente;
	
    private String telefono;		
	
    private String correo;
    
    
    
}