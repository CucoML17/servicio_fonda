package carlos.mejia.fondaspringunirff.dto;

import carlos.mejia.fondaspringunirff.entity.Tipo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDto {
	
	private Integer id_producto;
	
	
	private String nombre;
	
	
	private String descripcion;
	
	
	private double precio;	
	
	//Nuevo lo del estatus
	private Integer estatus;
	
	private String imgProducto;
	
	
	private Integer idTipo;	
}
