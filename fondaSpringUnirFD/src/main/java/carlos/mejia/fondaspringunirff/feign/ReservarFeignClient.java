package carlos.mejia.fondaspringunirff.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import carlos.mejia.fondaspringunirff.dto.AtenderDto;
import carlos.mejia.fondaspringunirff.dto.EmpleadoDto;
import carlos.mejia.fondaspringunirff.dto.MesaReservaDto;
import carlos.mejia.fondaspringunirff.dto.PuestoDto;
import carlos.mejia.fondaspringunirff.dto.ReservaFeignDto;

@FeignClient(name = "reservaciones") 
public interface ReservarFeignClient {
    
    @GetMapping("/api/reservaciones/buscaid/{id}")
    ReservaFeignDto getReservaById(@PathVariable("id") Integer id);
    
    
    @GetMapping("/api/mesas/buscaid/{id}")
    MesaReservaDto getMesaById(@PathVariable("id") Integer id);
    
    @GetMapping("/api/atender/empleado/venta/{idVenta}")
    EmpleadoDto getEmpleadoByVentaId(@PathVariable("idVenta") Integer idVenta);
    
    //Obtener DTO del Puesto por su ID
    @GetMapping("/api/puestos/buscaid/{id}")
    PuestoDto getPuestoById(@PathVariable("id") Integer id);
    
    @GetMapping("/api/atender/empleado/{idEmpleado}")
    List<AtenderDto> getAtenderByEmpleadoId(@PathVariable("idEmpleado") Integer idEmpleado);    
    
}
