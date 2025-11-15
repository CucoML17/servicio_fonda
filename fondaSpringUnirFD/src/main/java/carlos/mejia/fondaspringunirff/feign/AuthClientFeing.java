package carlos.mejia.fondaspringunirff.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "authservicio")
public interface AuthClientFeing {
	
	@GetMapping("/api/auth/estatus/{username}")
    Integer getEstatusByUsername(
        @RequestHeader("Authorization") String token, // CRÍTICO: Reenviar el token de seguridad
        @PathVariable("username") String username
    );		

}
