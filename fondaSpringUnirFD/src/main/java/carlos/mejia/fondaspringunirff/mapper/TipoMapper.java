package carlos.mejia.fondaspringunirff.mapper;

import carlos.mejia.fondaspringunirff.dto.TipoDto;
import carlos.mejia.fondaspringunirff.entity.Tipo;

public class TipoMapper {
	
	//Entidad a DTO
	public static TipoDto mapToTipoDto(Tipo tipo) {
		
		return new TipoDto(
					tipo.getIdtipo(),
					tipo.getTipo(),
					tipo.getDescripcion()
					
				);
		
	}
	
	//DTO a Entidad
	public static Tipo mapToTipo(TipoDto tipoDto) {
		
		//Le pasamos 'null' en lugar de la lista de productos, sino, esto se hace recursivo
		return new Tipo(
				tipoDto.getIdtipo(),
				tipoDto.getTipo(),
				tipoDto.getDescripcion(),
				null 
			);
	}	

}
