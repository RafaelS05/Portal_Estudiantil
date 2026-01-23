package PortalEstudiantil.ProyectoPortalEstudiantil.Security;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.AuthUserData;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AuthRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final AuthRepository authRepository;
    private final int maxIntentos = 5;
    private final int minutosBloqueo = 15;

    public LoginFailureHandler(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {
        String email = request.getParameter("email");
        
        if (email != null && !email.isBlank()) {
            try {
                AuthUserData data = authRepository.getUserByEmail(email);
                if (data.getIdCredencial() != null) {
                    authRepository.loginFail(data.getIdCredencial(), maxIntentos, minutosBloqueo);
                }
            } catch (Exception e) {
                // Log error silently
            }
        }
        
        response.sendRedirect("/login?error");
    }
}