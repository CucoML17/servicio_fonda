package carlos.mejia.fondaspringunirff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="Producto")
public class Producto {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id_producto;
	
	@Column(name="nombre")
	private String nombre;
	
	@Column(name="descripcion")
	private String descripcion;
	
	@Column(name="precio")
	private double precio;	
	
	//El nuevo atributo
	@Column(name="estatus")
	private Integer estatus;
	
	//Para la imagen del platillo:
	@Column(name="imgProducto")
	private String imgProducto;
	
	//La llave foránea:
	@ManyToOne
	@JoinColumn(name="idTipo")
    private Tipo idTipo;	

}
