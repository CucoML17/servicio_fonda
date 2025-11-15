package carlos.mejia.fondaspringunirff.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import carlos.mejia.fondaspringunirff.dto.ClienteDto;

@FeignClient(name = "resurantespringf") //Nombre del proyecto del otro microservicio
public interface ClienteFeignClient {

    
    @GetMapping("/api/cliente/buscaid/{id}")
    ClienteDto getClienteById(@PathVariable("id") Integer id);
}
