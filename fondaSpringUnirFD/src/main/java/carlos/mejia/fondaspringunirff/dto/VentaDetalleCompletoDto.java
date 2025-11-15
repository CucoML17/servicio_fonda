package carlos.mejia.fondaspringunirff.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class VentaDetalleCompletoDto {
    // Campos principales de Venta
    private Integer idventa;
    private LocalDate fechaventa;
    private Double totalventa;
    private Integer idReserva; 
    
    // DTOs anidados
    private ClienteInfo clienteInfo;
    private EmpleadoAtiende empleadoAtiende;
    private List<ProductoVendido> productosVendidos;
    private ReservaInfo reservaInfo; // Puede ser null
    
    // --- Clases Internas/Estructuras ---

    @Getter 
    @Setter 
    @NoArgsConstructor 
    @AllArgsConstructor
    @Builder
    public static class ClienteInfo {
        private Integer idcliente;
        private String nombrecliente;
    }

    @Getter 
    @Setter 
    @NoArgsConstructor 
    @AllArgsConstructor
    @Builder
    public static class EmpleadoAtiende {
        private Integer idEmpleado;
        private String nombre;
        private String puesto; // Aquí irá el nombre del puesto
    }

    @Getter 
    @Setter 
    @NoArgsConstructor 
    @AllArgsConstructor
    @Builder
    public static class ProductoVendido {
        private Integer idProducto;
        private String nombreProducto;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal;
    }
    
    @Getter 
    @Setter 
    @NoArgsConstructor 
    @AllArgsConstructor
    @Builder
    public static class ReservaInfo {
        private Integer idMesa;
        private Integer numeroMesa;
        private Integer capacidadMesa;
        private String ubicacion;
        private String fechaReserva; // Lo mantendremos como String para el formato de salida
        private String horaReserva; // Lo mantendremos como String
    }
}
