package carlos.mejia.fondaspringunirff.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import carlos.mejia.fondaspringunirff.dto.TipoDto;
import carlos.mejia.fondaspringunirff.entity.Tipo;
import carlos.mejia.fondaspringunirff.mapper.TipoMapper;
import carlos.mejia.fondaspringunirff.repositoy.TipoRepository;
import carlos.mejia.fondaspringunirff.services.TipoService;
import lombok.AllArgsConstructor;



@Service
@AllArgsConstructor
public class TipoServiceImpl implements TipoService{
	
	private TipoRepository tipoRepository;

	//CREATE (OK)
	@Override
	public TipoDto createTipo(TipoDto tipoDto) {
		//Obtiene DTO
		Tipo tipo = TipoMapper.mapToTipo(tipoDto);
		//Guarda a nivel de base de datos el tipo
		Tipo savedTipo = tipoRepository.save(tipo);
		//Retorna el tipo en forma de DTO que se acaba de guardar en la base de datos
		return TipoMapper.mapToTipoDto(savedTipo);
	}
	
	//Consulta por id
	@Override
	public TipoDto getTipoById(Integer idTipo) {
		Tipo tipo = tipoRepository.findById(idTipo).orElse(null);

		return TipoMapper.mapToTipoDto(tipo);	
	}

	//Consulta todo
	@Override
	public List<TipoDto> getAllTipo() {
		List<Tipo> tipos = tipoRepository.findAll();		
		
		
		return tipos.stream().map((tipo) -> TipoMapper.mapToTipoDto(tipo))
				.collect(Collectors.toList());
	}

	//UPDATE actualiza
	@Override
	public TipoDto updateTipo(Integer idTipo, TipoDto updateTipo) {
		
		Tipo tipo = tipoRepository.findById(idTipo).orElse(null);
		
		//Modificar los atributos
		tipo.setTipo(updateTipo.getTipo());
		tipo.setDescripcion(updateTipo.getDescripcion());
		
		
		//Guarda a nivel de base de datos el Tipo
		Tipo updatedTipo = tipoRepository.save(tipo);
		
		
		//Retorna el tipo en forma de DTO que se acaba de guardar en la base de datos
		return TipoMapper.mapToTipoDto(updatedTipo);
	}

	//DELETE
	@Override
	public void deleteTipo(Integer idTipo) {
		Tipo tipo = tipoRepository.findById(idTipo).orElse(null);
		
		tipoRepository.deleteById(idTipo);
		
	}

}