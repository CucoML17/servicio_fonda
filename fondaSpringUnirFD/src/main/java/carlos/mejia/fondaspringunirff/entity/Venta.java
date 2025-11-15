package carlos.mejia.fondaspringunirff.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idventa;

    @Column(nullable = false)
    private LocalDate fechaventa;

    @Column(nullable = false)
    private double totalventa;

    //La relacion con cliente
    @Column(name = "id_cliente")
    private Integer idCliente; 
    
    @Column(nullable = false)
    private int estado;
    
    //---------------
    //Si es venta directa, sin reserva, puede quedar null
    @Column(name = "id_reserva", nullable = true) //Puede ser null
    private Integer idReserva;
    

    //Relación con DetalleVenta
    @OneToMany(mappedBy = "venta")
    private Set<DetalleVenta> detallesVenta = new HashSet<>(); 
}