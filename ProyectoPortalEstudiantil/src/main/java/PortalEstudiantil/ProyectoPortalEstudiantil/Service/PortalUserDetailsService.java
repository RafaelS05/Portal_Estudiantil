package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.AuthUserData;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.AuthRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Security.PortalUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PortalUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    public PortalUserDetailsService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthUserData data = authRepository.getUserByEmail(email);
        
        if (data.getUsername() == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }

        return new PortalUserDetails(
            email,
            data.getUsername(),
            data.getPasswordHash(),
            data.getRole(),
            data.getEnabled(),
            data.getBloqueadoHasta(),
            data.getIdCredencial()
        );
    }
}