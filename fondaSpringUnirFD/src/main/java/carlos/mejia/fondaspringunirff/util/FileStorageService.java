package carlos.mejia.fondaspringunirff.util;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    // Ruta donde se guardarán las imágenes de los productos
    private final String PRODUCT_IMG_DIR = "/app/imagenesFonda/productos";

    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null; // No hay archivo para guardar
        }
        
        try {
            // 1. Crear el directorio si no existe
            Path rutaDirectorio = Paths.get(PRODUCT_IMG_DIR);
            if (!Files.exists(rutaDirectorio)) {
                Files.createDirectories(rutaDirectorio);
            }

            // 2. Obtener el nombre original del archivo
            String nombreArchivo = file.getOriginalFilename();
            
            // 3. Resolver la ruta completa y copiar el archivo
            Path rutaArchivo = rutaDirectorio.resolve(nombreArchivo).normalize();
            Files.copy(file.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
            
            // 4. Devolver solo el nombre del archivo (para guardar en la DB)
            return nombreArchivo;
            
        } catch (IOException ex) {
            // Manejo de error al guardar el archivo
            throw new RuntimeException("No se pudo almacenar el archivo " + file.getOriginalFilename() + ". Por favor, inténtelo de nuevo!", ex);
        }
    }
}