package PortalEstudiantil.ProyectoPortalEstudiantil;

import java.sql.Connection;
import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ProyectoPortalEstudiantilApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(ProyectoPortalEstudiantilApplication.class, args);

        try {
            DataSource ds = context.getBean(DataSource.class);
            try (Connection conn = ds.getConnection()) {
                System.out.println("=================================");
                System.out.println("✅ CONEXION A ORACLE OK");
                System.out.println("=================================");
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR DE CONEXIÓN A ORACLE");
            e.printStackTrace();
        }
    }
}
