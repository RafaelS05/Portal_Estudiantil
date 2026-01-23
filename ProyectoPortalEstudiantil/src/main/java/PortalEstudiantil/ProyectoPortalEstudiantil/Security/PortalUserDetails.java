package PortalEstudiantil.ProyectoPortalEstudiantil.Security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

public class PortalUserDetails implements UserDetails {

    private final String email;
    private final String username;
    private final String passwordHash;
    private final String role;
    private final Integer enabled;
    private final Timestamp bloqueadoHasta;
    private final Long idCredencial;

    public PortalUserDetails(String email, String username, String passwordHash, String role,
                             Integer enabled, Timestamp bloqueadoHasta, Long idCredencial) {
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.enabled = enabled;
        this.bloqueadoHasta = bloqueadoHasta;
        this.idCredencial = idCredencial;
    }

    public Long getIdCredencial() { 
        return idCredencial; 
    }
    
    public String getDisplayName() { 
        return username; 
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String normalized = (role == null ? "USER" : role).toUpperCase().replace(" ", "_");
        return List.of(new SimpleGrantedAuthority("ROLE_" + normalized));
    }

    @Override
    public String getPassword() { 
        return passwordHash; 
    }

    @Override
    public String getUsername() { 
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { 
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() {
        if (bloqueadoHasta == null) return true;
        return bloqueadoHasta.toInstant().isBefore(Instant.now());
    }

    @Override
    public boolean isCredentialsNonExpired() { 
        return true; 
    }

    @Override
    public boolean isEnabled() {
        return enabled != null && enabled == 1;
    }
}