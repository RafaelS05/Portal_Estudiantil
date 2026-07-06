package PortalEstudiantil.ProyectoPortalEstudiantil.Security;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Credencial;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AuthRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.CredencialRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthRepository authRepository;
    private final CredencialRepository credencialRepository;

    public LoginSuccessHandler(AuthRepository authRepository,
                               CredencialRepository credencialRepository) {
        this.authRepository = authRepository;
        this.credencialRepository = credencialRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();

        if (principal instanceof PortalUserDetails pud) {
            authRepository.loginSuccess(pud.getIdCredencial());

            // Reiniciar contador de intentos fallidos y desbloqueo (HU.13.2)
            try {
                Credencial credencial = credencialRepository.findById(pud.getIdCredencial()).orElse(null);
                if (credencial != null
                        && ((credencial.getIntentosFallidos() != null && credencial.getIntentosFallidos() > 0)
                            || credencial.getBloqueadoHasta() != null)) {
                    credencial.setIntentosFallidos(0);
                    credencial.setBloqueadoHasta(null);
                    credencialRepository.save(credencial);
                }
            } catch (Exception e) {
                // No impedir el login si falla el reinicio del contador
            }
             }
        response.sendRedirect("/");
    }
}