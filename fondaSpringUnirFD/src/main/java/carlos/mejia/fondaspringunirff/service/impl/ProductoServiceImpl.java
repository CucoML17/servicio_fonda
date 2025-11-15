package carlos.mejia.fondaspringunirff.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import carlos.mejia.fondaspringunirff.dto.ProductoDto;
import carlos.mejia.fondaspringunirff.entity.Producto;
import carlos.mejia.fondaspringunirff.entity.Tipo;
import carlos.mejia.fondaspringunirff.mapper.ProductoMapper;
import carlos.mejia.fondaspringunirff.repositoy.ProductoRepository;
import carlos.mejia.fondaspringunirff.repositoy.TipoRepository;
import carlos.mejia.fondaspringunirff.services.ProductoService;
import carlos.mejia.fondaspringunirff.util.FileStorageService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductoServiceImpl implements ProductoService{
	
	private ProductoRepository productoRepository;
	private TipoRepository tipoRepository;
	
	private final FileStorageService fileStorageService;
	
	//CREATE
	@Override
	public ProductoDto createProducto(ProductoDto productoDto) {
		//Buscamos la entidad Tipo en la base de datos por su id (que viene en el DTO)
		Tipo tipo = tipoRepository.findById(productoDto.getIdTipo()) 
				.orElseThrow(() -> new RuntimeException("Tipo no encontrado"));
		
		//Convierte el DTO a la entidad Producto
		Producto producto = ProductoMapper.mapToProducto(productoDto);
		
		//Asigna la entidad Tipo al producto con base a la id
		producto.setIdTipo(tipo);
		
		//Guarda a nivel de base de datos el producto
		Producto savedProducto = productoRepository.save(producto);
		
		//Retorna el producto en forma de DTO
		return ProductoMapper.mapToProductoDto(savedProducto);
	}
	
	//Consulta por id
	@Override
	public ProductoDto getProductoById(Integer idProducto) {
		Producto producto = productoRepository.findById(idProducto).orElse(null);

		return ProductoMapper.mapToProductoDto(producto);	
	}

	//Consulta todo
	@Override
	public List<ProductoDto> getAllProducto() {
		List<Producto>  productos = productoRepository.findAll();		
		
		return productos.stream().map((producto) -> ProductoMapper.mapToProductoDto(producto)) 
				.collect(Collectors.toList());
	}

	//UPDATE actualiza
	@Override
	public ProductoDto updateProducto(Integer idProducto, ProductoDto updateProducto) {
		
		Producto producto = productoRepository.findById(idProducto).orElse(null);
		
		//Modificar los atributos
		producto.setNombre(updateProducto.getNombre());
		producto.setDescripcion(updateProducto.getDescripcion());
		producto.setPrecio(updateProducto.getPrecio());
		producto.setEstatus(updateProducto.getEstatus());
		
		// ACTUALIZAR IMAGEN: Solo si el DTO trae un nombre de imagen, lo actualizamos.
	    // Si viene null o vacío, conservamos el valor anterior de la DB.
	    if (updateProducto.getImgProducto() != null && !updateProducto.getImgProducto().isEmpty()) {
	        producto.setImgProducto(updateProducto.getImgProducto());
	    }
	    
		
		//Busca y asigna la entidad Tipo persistente para la actualización
		Tipo tipo = tipoRepository.findById(updateProducto.getIdTipo()) 
				.orElseThrow(() -> new RuntimeException("Tipo no encontrado"));
		producto.setIdTipo(tipo);
		
		//Guarda y retorna
		Producto updatedProducto = productoRepository.save(producto);
		return ProductoMapper.mapToProductoDto(updatedProducto);
	}


	//DELETE Borra
	@Override
	public void deleteProducto(Integer idProducto) {
		Producto producto = productoRepository.findById(idProducto).orElse(null);
		
		productoRepository.deleteById(idProducto);
		
	}
	
	//Para estatus y todo lo relacionado a este:
	@Override
	public ProductoDto cambioProductoEstatus(Integer idProducto) {
		
		//1. Busca el producto, si no existe, lanzar excepción
		Producto producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> new RuntimeException("Producto con ID: " + idProducto + " no encontrado."));
		
		Integer estatusActual = producto.getEstatus();
        
        //2. Determina el nuevo estatus, lo invierte vaya, si era 0 a 1 y viciversa
        Integer nuevoEstatus = (estatusActual != null && estatusActual == 1) ? 0 : 1; 
        
        //3. Guarda el nuevo estatus
		producto.setEstatus(nuevoEstatus);
		
		//4. Guardar el producto actualizado en la base de datos
		Producto updatedProducto = productoRepository.save(producto);
		
		//5. Retornar el DTO
		return ProductoMapper.mapToProductoDto(updatedProducto);
	}
	
	//Busquedas:
    //1. Obtener todos los productos con estatus = 1
    @Override
    public List<ProductoDto> getAllActiveProductos() {
       
        List<Producto> productos = productoRepository.findByEstatus(1); 

        return productos.stream()
                .map(ProductoMapper::mapToProductoDto)
                .collect(Collectors.toList());
    }

    //2. Obtener productos por nombre
    @Override
    public List<ProductoDto> getProductosByNombreAndEstatus(String nombre) {
        
        List<Producto> productos = productoRepository.findByNombreContainingAndEstatus(nombre, 1);

        return productos.stream()
                .map(ProductoMapper::mapToProductoDto)
                .collect(Collectors.toList());
    }

    //3. Obtener productos por tipo producto
    @Override
    public List<ProductoDto> getProductosByTipoAndEstatus(Integer idTipo) {
        
        List<Producto> productos = productoRepository.findByIdTipo_IdtipoAndEstatus(idTipo, 1);

        return productos.stream()
                .map(ProductoMapper::mapToProductoDto)
                .collect(Collectors.toList());
    }

    //4. Obtener productos por nombre y tipo 
    @Override
    public List<ProductoDto> getProductosByNombreAndTipoAndEstatus(String nombre, Integer idTipo) {
        
        List<Producto> productos = productoRepository.findByNombreContainingAndIdTipo_IdtipoAndEstatus(nombre, idTipo, 1);

        return productos.stream()
                .map(ProductoMapper::mapToProductoDto)
                .collect(Collectors.toList());
    }
    
 // --------- NUEVOS MÉTODOS CON RANGO DE PRECIO ---------

    //Productos por rango de precio y estatus
 	@Override
 	public List<ProductoDto> getProductosByRangoPrecioAndEstatus(Double precioMin, Double precioMax) {
 		List<Producto> productos = productoRepository.findByPrecioBetweenAndEstatus(precioMin, precioMax, 1);
 		return productos.stream().map(ProductoMapper::mapToProductoDto).collect(Collectors.toList());
 	}

 	//Productos por rango de precio, nombre y estatus
 	@Override
 	public List<ProductoDto> getProductosByNombreAndRangoPrecioAndEstatus(String nombre, Double precioMin, Double precioMax) {
 		List<Producto> productos = productoRepository.findByNombreContainingAndPrecioBetweenAndEstatus(nombre, precioMin, precioMax, 1);
 		return productos.stream().map(ProductoMapper::mapToProductoDto).collect(Collectors.toList());
 	}

 	//Productos por rango de precio, tipo y estatus
 	@Override
 	public List<ProductoDto> getProductosByTipoAndRangoPrecioAndEstatus(Integer idTipo, Double precioMin, Double precioMax) {
 		List<Producto> productos = productoRepository.findByIdTipo_IdtipoAndPrecioBetweenAndEstatus(idTipo, precioMin, precioMax, 1);
 		return productos.stream().map(ProductoMapper::mapToProductoDto).collect(Collectors.toList());
 	}

 	//Productos por rango de precio, nombre, tipo y estatus
 	@Override
 	public List<ProductoDto> getProductosByNombreAndTipoAndRangoPrecioAndEstatus(String nombre, Integer idTipo, Double precioMin, Double precioMax) {
 		List<Producto> productos = productoRepository.findByNombreContainingAndIdTipo_IdtipoAndPrecioBetweenAndEstatus(nombre, idTipo, precioMin, precioMax, 1);
 		return productos.stream().map(ProductoMapper::mapToProductoDto).collect(Collectors.toList());
 	}    
    
}
