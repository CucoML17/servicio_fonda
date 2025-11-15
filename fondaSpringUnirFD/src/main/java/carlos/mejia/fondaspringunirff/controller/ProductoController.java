package carlos.mejia.fondaspringunirff.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import carlos.mejia.fondaspringunirff.dto.ProductoDto;
import carlos.mejia.fondaspringunirff.services.ProductoService;
import carlos.mejia.fondaspringunirff.util.FileStorageService;
import lombok.AllArgsConstructor;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/producto")
public class ProductoController {
	
	//Inyección de dependencias
	private ProductoService productoService;
	private final FileStorageService fileStorageService;
	
	
	//Construir el REST API para agregar un Producto
	// Construir el REST API para agregar un Producto
	// Cambiamos @RequestBody a Multipart y @RequestParam para la data
	@PostMapping(value = "/guardar", consumes = {"multipart/form-data"})
	public ResponseEntity<ProductoDto> crearProducto(
	        @RequestParam("nombre") String nombre,
	        @RequestParam("descripcion") String descripcion,
	        @RequestParam("precio") double precio,
	        @RequestParam("estatus") Integer estatus,
	        @RequestParam("idTipo") Integer idTipo,
	        @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile) {

	    // 1. Guardar la imagen (si se proporciona)
	    String nombreImagenGuardada = fileStorageService.storeFile(imagenFile);

	    // 2. Crear el DTO manualmente con todos los campos
	    ProductoDto productoDto = new ProductoDto(
	        null, 
	        nombre, 
	        descripcion, 
	        precio, 
	        estatus, 
	        nombreImagenGuardada, // Nombre de la imagen para la DB
	        idTipo
	    );

	    // 3. Llamar al servicio
	    ProductoDto guardarProducto = productoService.createProducto(productoDto);

	    return new ResponseEntity<> (guardarProducto, HttpStatus.CREATED);
	}
	
	@GetMapping("/buscaid/{id}")
	public ResponseEntity<ProductoDto> getProductoById(@PathVariable("id") Integer idProducto){
		ProductoDto productoEncontrado = productoService.getProductoById(idProducto);
		
		return ResponseEntity.ok(productoEncontrado);		
	}
	
	
	
	
	//Retorna lista
	@GetMapping("/listat")
	public ResponseEntity<List<ProductoDto>> getAllProducto(){
		
		List<ProductoDto> listaProductos = productoService.getAllProducto();
		return ResponseEntity.ok(listaProductos);
		
	}
	
	
	//Para el Update
	// Para el Update
	@PutMapping(value = "/actualizar/{id}", consumes = {"multipart/form-data"})
	public ResponseEntity<ProductoDto> updateProducto(
	        @PathVariable("id") Integer idProducto,
	        @RequestParam("nombre") String nombre,
	        @RequestParam("descripcion") String descripcion,
	        @RequestParam("precio") double precio,
	        @RequestParam("estatus") Integer estatus,
	        @RequestParam("idTipo") Integer idTipo,
	        @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile) {

	    // 1. Guardar la nueva imagen (si se proporciona un archivo nuevo)
	    String nombreImagenGuardada = fileStorageService.storeFile(imagenFile);

	    // 2. Crear el DTO con la nueva data
	    ProductoDto updateProducto = new ProductoDto(
	        idProducto, 
	        nombre, 
	        descripcion, 
	        precio, 
	        estatus, 
	        nombreImagenGuardada, // Si es null, el Service mantendrá el valor viejo
	        idTipo
	    );

	    // 3. Llamar al servicio
	    ProductoDto productoDto = productoService.updateProducto(idProducto, updateProducto);

	    return ResponseEntity.ok(productoDto);
	}

	
	
	//Para el delete
	@DeleteMapping("/eliminar/{id}")
	public ResponseEntity<String> updateCliente(@PathVariable("id") Integer idProducto){
		productoService.deleteProducto(idProducto);
		
		return ResponseEntity.ok("Producto eliminado");
	
	}	

	
    @PatchMapping("/cambiar/{id}/cambiaEstatus") 
    public ResponseEntity<ProductoDto> toggleProductoStatus(
            @PathVariable("id") Integer idProducto) {
        
        try {
            //Llama al servicio para invertir el estado
            ProductoDto updatedProducto = productoService.cambioProductoEstatus(idProducto);
            
            return new ResponseEntity<>(updatedProducto, HttpStatus.OK);
            
        } catch (RuntimeException e) {
            //Manejo de errores
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    
    
    @GetMapping("/filtro")
    public ResponseEntity<List<ProductoDto>> getProductosFiltrados(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "idTipo", required = false) Integer idTipo,
            @RequestParam(name = "precioMin", required = false) Double precioMin,
            @RequestParam(name = "precioMax", required = false) Double precioMax) {
        
        List<ProductoDto> productos;

        //para verificar qué parámetros están presentes
        boolean hasNombre = nombre != null && !nombre.isEmpty();
        boolean hasTipo = idTipo != null;
        boolean hasRangoPrecio = precioMin != null && precioMax != null;

        //La lógica de decisión (se priorizan las combinaciones más completas)
        
        if (hasNombre && hasTipo && hasRangoPrecio) { //1. Nombre Y Tipo Y Rango (8)
            productos = productoService.getProductosByNombreAndTipoAndRangoPrecioAndEstatus(nombre, idTipo, precioMin, precioMax);
            
        } else if (hasNombre && hasTipo) { //2. Nombre Y Tipo (4)
            productos = productoService.getProductosByNombreAndTipoAndEstatus(nombre, idTipo);
            
        } else if (hasNombre && hasRangoPrecio) { //3. Nombre Y Rango (6)
            productos = productoService.getProductosByNombreAndRangoPrecioAndEstatus(nombre, precioMin, precioMax);
            
        } else if (hasTipo && hasRangoPrecio) { //4. Tipo Y Rango (7)
            productos = productoService.getProductosByTipoAndRangoPrecioAndEstatus(idTipo, precioMin, precioMax);
            
        } else if (hasRangoPrecio) { //5. Solo Rango (5)
            productos = productoService.getProductosByRangoPrecioAndEstatus(precioMin, precioMax);
            
        } else if (hasNombre) { //6. Solo Nombre (2)
            productos = productoService.getProductosByNombreAndEstatus(nombre);
            
        } else if (hasTipo) { //7. Solo Tipo (3)
            productos = productoService.getProductosByTipoAndEstatus(idTipo);
            
        } else { // 8. Sin filtros (Todos Activos) (1)
            productos = productoService.getAllActiveProductos();
        }

        return ResponseEntity.ok(productos);
    } 
    
    
}
