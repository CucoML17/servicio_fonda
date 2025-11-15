package carlos.mejia.fondaspringunirff.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
@Table(name="detalle_venta")
public class DetalleVenta {

    @EmbeddedId
    private DetalleVentaId id;

    //Relación muchos a uno
    @ManyToOne
    @MapsId("idVenta") 
    @JoinColumn(name = "id_venta")
    private Venta venta;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private double subtotal;

    
}