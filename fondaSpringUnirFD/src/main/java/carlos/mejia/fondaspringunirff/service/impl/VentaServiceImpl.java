package carlos.mejia.fondaspringunirff.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import carlos.mejia.fondaspringunirff.dto.AtenderDto;
import carlos.mejia.fondaspringunirff.dto.ClienteDto;
import carlos.mejia.fondaspringunirff.dto.EmpleadoDto;
import carlos.mejia.fondaspringunirff.dto.MesaReservaDto;
import carlos.mejia.fondaspringunirff.dto.ProductoDto;
import carlos.mejia.fondaspringunirff.dto.PuestoDto;
import carlos.mejia.fondaspringunirff.dto.ReservaFeignDto;
import carlos.mejia.fondaspringunirff.dto.VentaAtendidaDto;
import carlos.mejia.fondaspringunirff.dto.VentaDetalleCompletoDto;
import carlos.mejia.fondaspringunirff.dto.VentaDetalleCompletoDto.ClienteInfo;
import carlos.mejia.fondaspringunirff.dto.VentaDetalleCompletoDto.EmpleadoAtiende;
import carlos.mejia.fondaspringunirff.dto.VentaDetalleCompletoDto.ProductoVendido;
import carlos.mejia.fondaspringunirff.dto.VentaDetalleCompletoDto.ReservaInfo;
import carlos.mejia.fondaspringunirff.dto.VentaDto;
import carlos.mejia.fondaspringunirff.dto.VentaListDto;
import carlos.mejia.fondaspringunirff.dto.VentaRequestDto;
import carlos.mejia.fondaspringunirff.dto.VentaUpdateDto;
import carlos.mejia.fondaspringunirff.entity.DetalleVenta;
import carlos.mejia.fondaspringunirff.entity.DetalleVentaId;
import carlos.mejia.fondaspringunirff.entity.Producto;
import carlos.mejia.fondaspringunirff.entity.Venta;
import carlos.mejia.fondaspringunirff.exception.ResourceNotFoundException;
import carlos.mejia.fondaspringunirff.feign.ClienteFeignClient;
import carlos.mejia.fondaspringunirff.feign.ReservarFeignClient;
import carlos.mejia.fondaspringunirff.mapper.VentaMapper;
import carlos.mejia.fondaspringunirff.repositoy.DetalleVentaRepository;
import carlos.mejia.fondaspringunirff.repositoy.ProductoRepository;
import carlos.mejia.fondaspringunirff.repositoy.VentaRepository;
import carlos.mejia.fondaspringunirff.services.ProductoService;
import carlos.mejia.fondaspringunirff.services.VentaService;
import carlos.mejia.fondaspringunirff.util.TicketPdfGeneradorService;
import feign.FeignException.NotFound;
import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository; 
    private final ClienteFeignClient clienteFeignClient;
    private final ReservarFeignClient reservarFeignClient;
    
    private final TicketPdfGeneradorService ticketPdfGeneradorService;
    
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    
 
    private final ProductoService productoService;
    
    
    private static final ZoneId MEXICO_CITY_ZONE = ZoneId.of("America/Mexico_City");
    
    
    
    @Override
    public VentaDto createVenta(VentaRequestDto ventaRequestDto) {
        //Valida que el cliente exista en el otro microservicio
        if (ventaRequestDto.getIdCliente() != null) {
            try {
                //Llama al microservicio Cliente solo si el ID no es null
                clienteFeignClient.getClienteById(ventaRequestDto.getIdCliente());
            } catch (Exception ex) {
                
                throw new ResourceNotFoundException("Cliente no encontrado con ID: " + ventaRequestDto.getIdCliente());
            }
        }
        
        //Valida la reserva
        if (ventaRequestDto.getIdReserva() != null) {
            try {
                //Llama al microservicio Reserva
                reservarFeignClient.getReservaById(ventaRequestDto.getIdReserva()); 
            } catch (NotFound ex) { 
                throw new ResourceNotFoundException("Reserva no encontrada con ID: " + ventaRequestDto.getIdReserva());
            } catch (Exception ex) {
                
                throw new RuntimeException("Error al contactar microservicio Reservaciones: " + ex.getMessage());
            }
        }
        
        
        

        //2. Crea ahora sí la venta
        Venta venta = new Venta();
        venta.setFechaventa(LocalDate.now(MEXICO_CITY_ZONE));
        
        //El ID del cliente se guarda tal como viene
        venta.setIdCliente(ventaRequestDto.getIdCliente()); 
        
        //Set para la reserva
        venta.setIdReserva(ventaRequestDto.getIdReserva());
        
        
        Venta savedVenta = ventaRepository.save(venta); 

        double totalVenta = 0;


        //3. Los detalles de la venta
        for (var detallePedido : ventaRequestDto.getProductos()) {
            //Buscar producto
            Producto producto = productoRepository.findById(detallePedido.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detallePedido.getIdProducto()));

            //Calcula el subtotal y el total de la venta
            double subtotal = producto.getPrecio() * detallePedido.getCantidad();
            totalVenta += subtotal;

            //4. Se crea y se guarda el detalle de la venta
            DetalleVentaId detalleVentaId = new DetalleVentaId(savedVenta.getIdventa(), detallePedido.getIdProducto());
            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setId(detalleVentaId);
            detalleVenta.setVenta(savedVenta);
            detalleVenta.setCantidad(detallePedido.getCantidad());
            detalleVenta.setSubtotal(subtotal);

            //Añadir el detalle a la colección de la entidad Venta
            savedVenta.getDetallesVenta().add(detalleVenta);
            detalleVentaRepository.save(detalleVenta);
        }

        //5. Guarda la venta final
        savedVenta.setTotalventa(totalVenta);
        Venta finalVenta = ventaRepository.save(savedVenta);

        return VentaMapper.mapToVentaDto(finalVenta);
    }

    @Override
    public VentaDto getVentaById(Integer idVenta) {
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + idVenta));
        return VentaMapper.mapToVentaDto(venta);
    }

    @Override
    public List<VentaListDto> getAllVentas() {
        List<Venta> ventas = ventaRepository.findAll();
        
        //Obtiene toda las ventas simples, sin su detalle
        return ventas.stream()
            .map(VentaMapper::mapToVentaListDto) 
            .collect(Collectors.toList());
    }
    
    //El delete:
    @Override
    public void deleteVenta(Integer idVenta) {
        
        //Verificar si la venta existe
        Venta venta = ventaRepository.findById(idVenta)
            .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + idVenta));

        //Eliminar los detalles de la venta primero, en cascada.
        detalleVentaRepository.deleteAll(venta.getDetallesVenta()); 
        
        //Eliminar la venta principal
        ventaRepository.delete(venta);
    }    
    
    
    @Override
    public byte[] generateTicketPdf(Integer idVenta) {
        
        VentaDto venta = getVentaById(idVenta); 
        
        // 1. Obtener el Nombre del Cliente
        String nombreCliente = "N/A";
        if (venta.getIdCliente() != null) {
            try {
                ClienteDto cliente = clienteFeignClient.getClienteById(venta.getIdCliente());
                nombreCliente = cliente.getNombrecliente();
            } catch (Exception e) {
                nombreCliente = "Cliente no encontrado";
            }
        }
        
        // 2. Obtener el Nombre del Empleado (¡Ahora más simple!)
        String nombreEmpleado = "N/A";
        try {
            // Llama al nuevo endpoint que te da el DTO de Empleado directo
            EmpleadoDto empleado = reservarFeignClient.getEmpleadoByVentaId(idVenta);
            
            if (empleado != null) {
                nombreEmpleado = empleado.getNombre();
            }
        } catch (Exception e) {
            // Puede fallar si la venta no tiene un registro en 'Atender'
            nombreEmpleado = "Empleado no asignado";
        }
        
        // 3. Generar el PDF
        return ticketPdfGeneradorService.generateTicketPdf(venta, nombreCliente, nombreEmpleado);
    }
    
    
    
    //El horror
    @Override
    public VentaDetalleCompletoDto getDetalleCompleto(Integer idVenta) {
        
        // 1. OBTENER VENTA PRINCIPAL Y MAPPEAR A DTO COMPLETO
        Venta ventaEntity = ventaRepository.findById(idVenta)
                        .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + idVenta));
        
        VentaDto venta = VentaMapper.mapToVentaDto(ventaEntity); // Mappea a VentaDto para usar los detalles

        // --- 2. OBTENER CLIENTE ---
        ClienteInfo clienteInfo = null;
        try {
            ClienteDto cliente = clienteFeignClient.getClienteById(venta.getIdCliente());
            clienteInfo = ClienteInfo.builder()
                    .idcliente(cliente.getIdcliente())
                    .nombrecliente(cliente.getNombrecliente()) // Usando 'nombrecliente' del DTO que proporcionaste
                    .build();
        } catch (Exception e) {
            System.err.println("Error Feign al obtener Cliente: " + e.getMessage());
            clienteInfo = ClienteInfo.builder()
                    .nombrecliente("Cliente Desconocido/Error")
                    .build();
        }
        
        // --- 3. OBTENER EMPLEADO Y PUESTO ---
        EmpleadoAtiende empleadoAtiende = null;
        try {
            EmpleadoDto empleadoDto = reservarFeignClient.getEmpleadoByVentaId(idVenta);
            String nombrePuesto = "N/A";
            
            if (empleadoDto.getIdPuesto() != null) {
                try {
                     // NUEVA LLAMADA FEIGN para obtener el nombre del puesto
                     PuestoDto puesto = reservarFeignClient.getPuestoById(empleadoDto.getIdPuesto());
                     nombrePuesto = puesto.getNombrePuesto();
                } catch (Exception e) {
                    System.err.println("Error Feign al obtener Puesto: " + e.getMessage());
                    nombrePuesto = "Puesto no encontrado";
                }
            }
            
            empleadoAtiende = EmpleadoAtiende.builder()
                    .idEmpleado(empleadoDto.getIdEmpleado())
                    .nombre(empleadoDto.getNombre())
                    .puesto(nombrePuesto) // Se asigna el nombre del puesto
                    .build();
            
        } catch (Exception e) {
            System.err.println("Error Feign al obtener Empleado: " + e.getMessage());
            empleadoAtiende = EmpleadoAtiende.builder().nombre("Empleado no asignado").puesto("N/A").build();
        }
        
        // --- 4. PREPARAR DETALLE DE PRODUCTOS ---
        List<ProductoVendido> productosVendidos = venta.getDetallesVenta().stream()
            .map(detalle -> {
                ProductoDto producto = productoService.getProductoById(detalle.getIdProducto());
                double precioUnitario = detalle.getSubtotal() / detalle.getCantidad();
                
                return ProductoVendido.builder()
                        .idProducto(detalle.getIdProducto())
                        .nombreProducto(producto != null ? producto.getNombre() : "Producto Desconocido")
                        .cantidad(detalle.getCantidad())
                        .precioUnitario(precioUnitario)
                        .subtotal(detalle.getSubtotal())
                        .build();
            })
            .collect(Collectors.toList());

        // --- 5. OBTENER RESERVA Y MESA (Si existe idReserva) ---
        ReservaInfo reservaInfo = null;
        if (venta.getIdReserva() != null) {
            try {
                // Obtener reserva con el DTO corregido (contiene java.util.Date)
                ReservaFeignDto reserva = reservarFeignClient.getReservaById(venta.getIdReserva());
                
                // Obtener mesa
                MesaReservaDto mesa = reservarFeignClient.getMesaById(reserva.getIdMesa());
                
                reservaInfo = ReservaInfo.builder()
                    .idMesa(mesa.getIdMesa())
                    .numeroMesa(mesa.getNumero())
                    .capacidadMesa(mesa.getCapacidad())
                    .ubicacion(mesa.getUbicacion())
                    // Formatear Date a String para la salida JSON
                    .fechaReserva(dateFormat.format(reserva.getFecha()))
                    .horaReserva(timeFormat.format(reserva.getHora()))
                    .build();
            } catch (Exception e) {
                System.err.println("Error Feign al obtener Reserva/Mesa: " + e.getMessage());
                // Dejamos reservaInfo como null, que es lo esperado en caso de fallo o inexistencia
            }
        }

        // --- 6. CONSTRUIR EL DTO DE RESPUESTA FINAL ---
        return VentaDetalleCompletoDto.builder()
                .idventa(venta.getIdventa())
                .fechaventa(venta.getFechaventa())
                .totalventa(venta.getTotalventa())
                .idReserva(venta.getIdReserva())
                .clienteInfo(clienteInfo)
                .empleadoAtiende(empleadoAtiende)
                .productosVendidos(productosVendidos)
                .reservaInfo(reservaInfo) 
                .build();
    }    
    
    
    //Filtro de fechas
    @Override
    public List<VentaListDto> getVentasByFecha(LocalDate fecha) {
        
        // Llama al nuevo método del Repository
        List<Venta> ventas = ventaRepository.findByFechaventa(fecha);
        
        // Mapea la lista de entidades a DTOs de listado
        return ventas.stream()
            .map(VentaMapper::mapToVentaListDto)
            .collect(Collectors.toList());
    }    
    
    
    @Override
    public VentaListDto getVentaByReservaId(Integer idReserva) {
        
        // 1. Buscar la VENTA MÁS RECIENTE por idReserva
        Venta venta = ventaRepository.findTopByidReservaOrderByIdventaDesc(idReserva)
                // Lanzar excepción si NO se encuentra ninguna venta asociada a la reserva
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ninguna Venta para la Reserva ID: " + idReserva));
        
        // 2. Mappear la entidad Venta a VentaListDto
        return VentaMapper.mapToVentaListDto(venta);
    }
    
    
    
    
    // MODIFICACIÓN DEL RETORNO Y EL MAPPEO
    @Override
    public List<VentaAtendidaDto> getVentasAtendidasByEmpleado(Integer idEmpleado, LocalDate fecha) { // TIPO DE RETORNO CAMBIADO
     
      // 1. LLAMAR A RESERVACIONES: Obtener todos los ID de Venta que el empleado atendió
      List<AtenderDto> atendidas = reservarFeignClient.getAtenderByEmpleadoId(idEmpleado);
     
      // Extraer solo los ID de Venta
      List<Integer> idsVenta = atendidas.stream()
          .map(AtenderDto::getIdVenta)
          .collect(Collectors.toList());
     
      if (idsVenta.isEmpty()) {
        return List.of(); // Si no atendió ninguna venta, retornamos una lista vacía
      }

      // 2. OBTENER LAS ENTIDADES VENTA (FILTRANDO POR ID Y POSIBLEMENTE POR FECHA)
      List<Venta> ventas;
     
      if (fecha != null) {
        ventas = ventaRepository.findByIdventaInAndFechaventa(idsVenta, fecha);
      } else {
        ventas = ventaRepository.findByIdventaIn(idsVenta);
      }

      // 3. MAPPEAR y DEVOLVER usando el nuevo Mapper
      return ventas.stream()
          .map(VentaMapper::mapToVentaAtendidaDto) // CAMBIADO EL MAPPER
          .collect(Collectors.toList());
    }
    
    
    
    
    @Override
    public List<VentaListDto> getVentasByClienteIdAndFecha(Integer idCliente, LocalDate fecha) {

        //1. Validar que el cliente exista (Opcional, pero buena práctica)
        if (idCliente != null) {
            try {
                clienteFeignClient.getClienteById(idCliente);
            } catch (Exception ex) {
                //Si el cliente no existe, lanzamos una excepción o devolvemos lista vacía
                throw new ResourceNotFoundException("Cliente no encontrado con ID: " + idCliente);
            }
        } else {
             //Si el ID del cliente es nulo, no se puede buscar.
             return List.of(); 
        }

        //2. Obtener las ventas de la base de datos
        List<Venta> ventas;
        
        if (fecha != null) {
            //Buscar por ID de Cliente Y Fecha
            ventas = ventaRepository.findByIdClienteAndFechaventa(idCliente, fecha);
        } else {
            //Buscar solo por ID de Cliente
            ventas = ventaRepository.findByIdCliente(idCliente);
        }

        //3. Mapear la lista de entidades a VentaListDto
        return ventas.stream()
            .map(VentaMapper::mapToVentaListDto)
            .collect(Collectors.toList());
    }    
    
 // NUEVO MÉTODO 1: Actualización simple de la Venta (manteniendo detalles)
    @Override
    public VentaDto updateVenta(Integer idVenta, VentaUpdateDto ventaUpdateDto) {
        // 1. Verificar si la venta existe
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + idVenta));

        // 2. Aplicar solo los cambios deseados
        // Nota: Fechaventa y totalventa no se modifican aquí.
        
        // El ID de Cliente puede ser null o actualizarse
        venta.setIdCliente(ventaUpdateDto.getIdCliente());
        
        // El ID de Reserva puede ser null o actualizarse
        venta.setIdReserva(ventaUpdateDto.getIdReserva());
        
        // El Estado se actualiza
        venta.setEstado(ventaUpdateDto.getEstado());

        // 3. Guardar y retornar
        Venta updatedVenta = ventaRepository.save(venta);
        return VentaMapper.mapToVentaDto(updatedVenta);
    }
    
    // NUEVO MÉTODO 2: Actualización completa (Venta y Detalles)
    @Override
    public VentaDto updateVentaCompleta(Integer idVenta, VentaRequestDto ventaRequestDto) {
        
        // 1. Verificar si la venta principal existe
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + idVenta));

        // 2. VALIDACIONES (reutilizamos la lógica del createVenta)
        // Puedes agregar aquí la lógica de validación de cliente y reserva si es necesario,
        // similar a la que ya tienes en `createVenta`.
        
        // 3. BORRAR DETALLES DE VENTA EXISTENTES
        detalleVentaRepository.deleteAll(venta.getDetallesVenta());
        // Limpiar la colección de la entidad para el siguiente paso
        venta.getDetallesVenta().clear();

        // 4. ACTUALIZAR CAMPOS DE VENTA PRINCIPAL (similares al create)
        venta.setFechaventa(LocalDate.now()); // Se puede actualizar la fecha o mantener la original
        venta.setIdCliente(ventaRequestDto.getIdCliente());
        venta.setIdReserva(ventaRequestDto.getIdReserva());
        venta.setEstado(0); // O el estado por defecto para una venta en proceso de edición.
        
        Venta savedVenta = ventaRepository.save(venta);
        double totalVenta = 0;

        // 5. INSERTAR NUEVOS DETALLES (reutilizamos la lógica del createVenta)
        for (var detallePedido : ventaRequestDto.getProductos()) {
            Producto producto = productoRepository.findById(detallePedido.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detallePedido.getIdProducto()));

            double subtotal = producto.getPrecio() * detallePedido.getCantidad();
            totalVenta += subtotal;

            DetalleVentaId detalleVentaId = new DetalleVentaId(savedVenta.getIdventa(), detallePedido.getIdProducto());
            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setId(detalleVentaId);
            detalleVenta.setVenta(savedVenta);
            detalleVenta.setCantidad(detallePedido.getCantidad());
            detalleVenta.setSubtotal(subtotal);

            savedVenta.getDetallesVenta().add(detalleVenta);
            detalleVentaRepository.save(detalleVenta);
        }

        // 6. ACTUALIZAR TOTAL y guardar venta final
        savedVenta.setTotalventa(totalVenta);
        Venta finalVenta = ventaRepository.save(savedVenta);

        return VentaMapper.mapToVentaDto(finalVenta);
    }    
    
    
}
