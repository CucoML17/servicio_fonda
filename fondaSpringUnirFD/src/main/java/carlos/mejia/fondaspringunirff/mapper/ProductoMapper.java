package carlos.mejia.fondaspringunirff.mapper;

import carlos.mejia.fondaspringunirff.dto.ProductoDto;
import carlos.mejia.fondaspringunirff.entity.Producto;


public class ProductoMapper {

	//De Entidad a DTO
	public static ProductoDto mapToProductoDto(Producto producto) {
		
		
        Integer idTipo = producto.getIdTipo() != null ? producto.getIdTipo().getIdtipo() : null;
		
		return new ProductoDto(
				producto.getId_producto(),
				producto.getNombre(),
				producto.getDescripcion(),
				producto.getPrecio(),
				producto.getEstatus(),
				producto.getImgProducto(),
				idTipo 
				);
		
	}
	
	//De DTO a Entidad
	public static Producto mapToProducto(ProductoDto productoDto) {
		
		
        //El Service será el encargado de buscar la entidad Tipo completa y asignarla.
		return new Producto(
				productoDto.getId_producto(),
				productoDto.getNombre(),
				productoDto.getDescripcion(),
				productoDto.getPrecio(),
				productoDto.getEstatus(), 
				productoDto.getImgProducto(),
				null
			);
	}		
	
}
