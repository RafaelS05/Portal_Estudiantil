package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(
            @Value("${app.uploads.path}") String uploadsPath) {
        this.uploadRoot = Paths.get(uploadsPath);
    }

    public String guardar(MultipartFile archivo, Long idTicket) {
        try {
            Path carpetaTicket = uploadRoot.resolve("tickets/" + idTicket);
            Files.createDirectories(carpetaTicket);

            String nombre = StringUtils.cleanPath(archivo.getOriginalFilename());
            String extension = nombre.substring(nombre.lastIndexOf("."));

            String nombreFinal = UUID.randomUUID().toString() + extension;

            Path destino = carpetaTicket.resolve(nombreFinal);
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            return "/tickets/" + idTicket + "/" + nombreFinal;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage());
        }
    }

    public void eliminar(String rutaRelativa) {
        try {
            Path archivo = uploadRoot.resolve(rutaRelativa.startsWith("/")
                    ? rutaRelativa.substring(1) : rutaRelativa);
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar el archivo: " + e.getMessage());
        }
    }

}
