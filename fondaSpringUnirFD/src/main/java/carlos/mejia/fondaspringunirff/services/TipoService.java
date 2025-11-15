package carlos.mejia.fondaspringunirff.services;

import java.util.List;

import carlos.mejia.fondaspringunirff.dto.TipoDto;



public interface TipoService {
	
	//CRUD COMPLETO-----
	
	//Registro
	TipoDto createTipo(TipoDto tipoDto);

	//Buscar por id
	TipoDto getTipoById(Integer idTipo);
	
	//Buscar todos
	List<TipoDto> getAllTipo();
	
	//Editar/actualizar datos
	TipoDto updateTipo(Integer idTipo, TipoDto updateTipo);
	
	//Eliminar - delete
	void deleteTipo(Integer idTipo);	

	//FIN CRUD
}
