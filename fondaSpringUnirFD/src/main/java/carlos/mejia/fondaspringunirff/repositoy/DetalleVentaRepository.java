package carlos.mejia.fondaspringunirff.repositoy;

import org.springframework.data.jpa.repository.JpaRepository;

import carlos.mejia.fondaspringunirff.entity.DetalleVenta;
import carlos.mejia.fondaspringunirff.entity.DetalleVentaId;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, DetalleVentaId> {
    
}
