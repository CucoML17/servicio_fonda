package carlos.mejia.fondaspringunirff.security;


import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        //Permite cualquier origen (el equivalente a @CrossOrigin("*"))
        configuration.setAllowedOrigins(Arrays.asList("*")); 
        
        //Permite los métodos que usas (GET, POST, PUT, DELETE, y CRUCIAL: OPTIONS)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        //permitir el header Authorization y Content-Type
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); 
        
        //Permite enviar cookies (aunque JWT no las usa directamente, es buena práctica)
        configuration.setAllowCredentials(false); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //Aplica esta configuración a todas las rutas
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }    

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
		    //Aplicar la configuración CORS
		    .cors(cors -> cors.configurationSource(corsConfigurationSource()))        
            //1. Deshabilitar CSRF (necesario para APIs REST sin sesiones)
            .csrf(csrf -> csrf.disable())
            
            //2. Configurar las reglas de autorización
            .authorizeHttpRequests(auth -> auth
            		
            	//todos:
                .requestMatchers("/imagenes/productos/**").permitAll()
                
                .requestMatchers("/api/producto/buscaid/**").permitAll()
                .requestMatchers("/api/producto/listat").permitAll()
                .requestMatchers("/api/producto/filtro/**").permitAll()
                
                .requestMatchers("/api/tipo/buscaid/**").permitAll()
                .requestMatchers("/api/tipo/listat").permitAll()
                
                
                //Producto
                .requestMatchers("/api/producto/guardar/**").hasAnyAuthority("Supervisor", "Administrador")
                .requestMatchers("/api/producto/actualizar/**").hasAnyAuthority("Supervisor", "Administrador")
                .requestMatchers("/api/producto/eliminar/**").hasAnyAuthority("Supervisor", "Administrador")
                .requestMatchers("/api/producto/cambiar/**").hasAnyAuthority("Supervisor", "Administrador")
                
                //Tipo
                .requestMatchers("/api/tipo/guardar").hasAnyAuthority("Supervisor", "Administrador")
                .requestMatchers("/api/tipo/actualizar/**").hasAnyAuthority("Supervisor", "Administrador")
                .requestMatchers("/api/tipo/eliminar/**").hasAnyAuthority("Supervisor", "Administrador")
                
                //Venta
                .requestMatchers("/api/ventas/guardar").hasAnyAuthority("Cajero", "Administrador")
                .requestMatchers("/api/ventas/listaf").hasAnyAuthority("Cajero", "Mesero", "Administrador")
                .requestMatchers("/api/ventas/buscaid/**").hasAnyAuthority("Cajero", "Mesero", "Administrador")
                .requestMatchers("/api/ventas/eliminar/**").hasAnyAuthority("Cajero", "Administrador")
                .requestMatchers("/api/ventas/ticket/**").hasAnyAuthority("Cajero", "Administrador", "Mesero", "Cliente")
                .requestMatchers("/api/ventas/detallecompleto/**").hasAnyAuthority("Cliente", "Cajero", "Mesero", "Supervisor", "Administrador")
                
                .requestMatchers("/api/ventas/actualizar/**").hasAnyAuthority("Mesero", "Administrador")
                
                
                .requestMatchers("/api/ventas/ventareserva/**").hasAnyAuthority("Cajero", "Mesero", "Administrador")
                
                //Una especial para elc liente solo ver las suyas
                
                .requestMatchers("/api/ventas/atendidas/**").hasAnyAuthority("Mesero", "Cajero", "Supervisor", "Administrador")
                
                                
                
                .anyRequest().authenticated()
            )
            
            //3. Establecer la política de sesión a STATELESS (CRUCIAL para JWT)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            //4. Añadir nuestro filtro JWT antes del filtro estándar de Spring
            .addFilterBefore(
                    jwtAuthFilter, 
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}

