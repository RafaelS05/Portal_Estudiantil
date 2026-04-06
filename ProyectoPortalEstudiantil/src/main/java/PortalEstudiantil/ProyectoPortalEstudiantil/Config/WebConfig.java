package PortalEstudiantil.ProyectoPortalEstudiantil.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Normaliza la ruta: barras hacia adelante y asegura que termina en /
        String ruta = uploadDir.replace("\\", "/");
        if (!ruta.endsWith("/")) ruta = ruta + "/";

        // /uploads/mensajes/archivo.pdf → sirve desde la carpeta física uploadDir
        registry.addResourceHandler("/uploads/mensajes/**")
                .addResourceLocations("file:///" + ruta);
    }
}