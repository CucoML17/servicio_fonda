package carlos.mejia.fondaspringunirff.repositoy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import carlos.mejia.fondaspringunirff.entity.Producto;



public interface ProductoRepository extends JpaRepository<Producto, Integer> {
	
    //1. Recupera productos por estatus (solo activos)
    //Es como un findAll, pero con WHERE estatus = ?
    List<Producto> findByEstatus(Integer estatus);

    //2. Recupera productos por nombre (LIKE) y estatus = 1
    List<Producto> findByNombreContainingAndEstatus(String nombre, Integer estatus);

    //3. Recupera productos por tipo y estatus = 1
    List<Producto> findByIdTipo_IdtipoAndEstatus(Integer idTipo, Integer estatus);
    
    //4. La combinación de los 3: Por nombre (LIKE), por tipo (id del tipo) y estatus = 1
    List<Producto> findByNombreContainingAndIdTipo_IdtipoAndEstatus(String nombre, Integer idTipo, Integer estatus);
    
    
    //5. Solo Rango de Precio AND Estatus
    List<Producto> findByPrecioBetweenAndEstatus(double precioMin, double precioMax, Integer estatus);

    //6. Nombre AND Rango de Precio AND Estatus
    List<Producto> findByNombreContainingAndPrecioBetweenAndEstatus(String nombre, double precioMin, double precioMax, Integer estatus);

    //7. Tipo AND Rango de Precio AND Estatus
    List<Producto> findByIdTipo_IdtipoAndPrecioBetweenAndEstatus(Integer idTipo, double precioMin, double precioMax, Integer estatus);

    // 8. Nombre AND Tipo AND Rango de Precio AND Estatus
    List<Producto> findByNombreContainingAndIdTipo_IdtipoAndPrecioBetweenAndEstatus(String nombre, Integer idTipo, double precioMin, double precioMax, Integer estatus);  
	

}
