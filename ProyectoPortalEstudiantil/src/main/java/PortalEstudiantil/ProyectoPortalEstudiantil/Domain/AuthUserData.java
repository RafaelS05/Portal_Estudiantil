package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import java.sql.Timestamp;

public class AuthUserData {
    private String username;
    private String passwordHash;
    private String role;
    private Integer intentos;
    private Timestamp bloqueadoHasta;
    private Integer enabled; // 1 o 0
    private Long idCredencial;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getIntentos() { return intentos; }
    public void setIntentos(Integer intentos) { this.intentos = intentos; }

    public Timestamp getBloqueadoHasta() { return bloqueadoHasta; }
    public void setBloqueadoHasta(Timestamp bloqueadoHasta) { this.bloqueadoHasta = bloqueadoHasta; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }

    public Long getIdCredencial() { return idCredencial; }
    public void setIdCredencial(Long idCredencial) { this.idCredencial = idCredencial; }
}
