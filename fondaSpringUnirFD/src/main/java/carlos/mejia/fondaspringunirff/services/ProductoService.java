package carlos.mejia.fondaspringunirff.services;

import java.util.List;

import carlos.mejia.fondaspringunirff.dto.ProductoDto;




public interface ProductoService {
	
	//CRUD COMPLETO-----
	
	//Registro
	ProductoDto createProducto(ProductoDto productoDto);

	//Buscar por id
	ProductoDto getProductoById(Integer idProducto);
	
	//Buscar todos
	List<ProductoDto> getAllProducto();
	
	//Editar/actualizar datos
	ProductoDto updateProducto(Integer idProducto, ProductoDto updateProducto);
	
	//Eliminar - delete
	void deleteProducto(Integer idProducto);	

	//FIN CRUD
	
	//El estatus y todo lo relacionado a este:

	ProductoDto cambioProductoEstatus(Integer idProducto);	
	
	
	//Busquedas:
    //1. Todos (activos)
    List<ProductoDto> getAllActiveProductos();

    //2. Por Nombre
    List<ProductoDto> getProductosByNombreAndEstatus(String nombre);

    //3. Por tipo del producto 
    List<ProductoDto> getProductosByTipoAndEstatus(Integer idTipo);

    //4. Por nombre y tipo del producto
    List<ProductoDto> getProductosByNombreAndTipoAndEstatus(String nombre, Integer idTipo);
    
    
    // --------- NUEVOS MÉTODOS CON RANGO DE PRECIO ---------

    //5. Solo Rango de Precio
    List<ProductoDto> getProductosByRangoPrecioAndEstatus(Double precioMin, Double precioMax);
  	
    //6. Nombre Y Rango de Precio
    List<ProductoDto> getProductosByNombreAndRangoPrecioAndEstatus(String nombre, Double precioMin, Double precioMax);
      
    //7. Tipo Y Rango de Precio
    List<ProductoDto> getProductosByTipoAndRangoPrecioAndEstatus(Integer idTipo, Double precioMin, Double precioMax);

    //8. Nombre Y Tipo Y Rango de Precio
    List<ProductoDto> getProductosByNombreAndTipoAndRangoPrecioAndEstatus(String nombre, Integer idTipo, Double precioMin, Double precioMax);
	

}
