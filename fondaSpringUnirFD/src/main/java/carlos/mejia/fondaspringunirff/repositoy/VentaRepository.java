package carlos.mejia.fondaspringunirff.repositoy;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import carlos.mejia.fondaspringunirff.entity.Venta;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
	List<Venta> findByFechaventa(LocalDate fechaventa);
	
	Optional<Venta> findTopByidReservaOrderByIdventaDesc(Integer idReserva);
	
	
    //1. Obtener lista de ventas cuyos IDs están en una colección
    List<Venta> findByIdventaIn(List<Integer> idventa);
    
    //2. Obtener lista de ventas cuyos IDs están en una colección Y tienen una fecha específica
    List<Venta> findByIdventaInAndFechaventa(List<Integer> idventa, LocalDate fechaventa);	
    
    
    //Ventas de un cliente
    List<Venta> findByIdCliente(Integer idCliente);

    //Ventas de un cliente y fecha
    List<Venta> findByIdClienteAndFechaventa(Integer idCliente, LocalDate fechaventa);    
}