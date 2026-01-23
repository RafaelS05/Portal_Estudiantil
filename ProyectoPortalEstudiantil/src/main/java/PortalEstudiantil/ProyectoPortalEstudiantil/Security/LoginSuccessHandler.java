package PortalEstudiantil.ProyectoPortalEstudiantil.Security;

import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AuthRepository;
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

    public LoginSuccessHandler(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof PortalUserDetails pud) {    
            authRepository.loginSuccess(pud.getIdCredencial());
             }
        response.sendRedirect("/");
    }
}