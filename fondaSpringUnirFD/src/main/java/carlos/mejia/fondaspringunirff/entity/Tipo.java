package carlos.mejia.fondaspringunirff.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name="Tipo")
public class Tipo {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer idtipo;
	
	@Column(name="tipo")
	private String tipo;
	@Column(name="descripcion")
	private String descripcion;	
	
	//Su listado de Productos, vaya relación 1:N
	//Con Producto
    @OneToMany(mappedBy = "idTipo", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Producto> productos;	

}
