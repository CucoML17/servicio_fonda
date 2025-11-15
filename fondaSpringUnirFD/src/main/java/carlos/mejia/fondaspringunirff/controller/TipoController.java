package carlos.mejia.fondaspringunirff.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import carlos.mejia.fondaspringunirff.dto.TipoDto;
import carlos.mejia.fondaspringunirff.services.TipoService;
import lombok.AllArgsConstructor;

@CrossOrigin("*")

@AllArgsConstructor
@RestController
@RequestMapping("/api/tipo")
public class TipoController {
	
	//Inyección de dependencias
	private TipoService tipoService;
	
	//Construir el REST API para agregar un Tipo
	@PostMapping("/guardar")
	public ResponseEntity<TipoDto> crearTipo(@RequestBody TipoDto tipoDto){
		
		TipoDto guardarTipo = tipoService.createTipo(tipoDto);
		
		return new ResponseEntity<> (guardarTipo, HttpStatus.CREATED);
	}
	
	
	@GetMapping("/buscaid/{id}")
	public ResponseEntity<TipoDto> getTipoById(@PathVariable("id") Integer idTipo){
		TipoDto tipoEncontrado = tipoService.getTipoById(idTipo);
		
		return ResponseEntity.ok(tipoEncontrado);		
	}
	
	//Retorna lista
	@GetMapping("/listat")
	public ResponseEntity<List<TipoDto>> getAllTipo(){
		
		List<TipoDto> listaTipos = tipoService.getAllTipo();
		return ResponseEntity.ok(listaTipos);
		
	}
	
	
	//Para el Update
	@PutMapping("/actualizar/{id}")
	public ResponseEntity<TipoDto> updateTipo(@PathVariable("id") Integer idTipo,
			@RequestBody TipoDto updateTipo){
		
		TipoDto tipoDto = tipoService.updateTipo(idTipo, updateTipo);
		
		return ResponseEntity.ok(tipoDto);
		
	}
	
	//Para el delete
	@DeleteMapping("/eliminar/{id}")
	public ResponseEntity<String> deleteTipo(@PathVariable("id") Integer idTipo){
		tipoService.deleteTipo(idTipo);
		
		return ResponseEntity.ok("Tipo eliminado");
	
	}	

}