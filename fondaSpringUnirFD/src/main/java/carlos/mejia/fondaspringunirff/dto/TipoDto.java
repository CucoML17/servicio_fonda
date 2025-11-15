package carlos.mejia.fondaspringunirff.dto;

import java.util.List;

import carlos.mejia.fondaspringunirff.entity.Producto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TipoDto {

	private Integer idtipo;
	private String tipo;
	private String descripcion;	
    //private List<Producto> productos;

}
