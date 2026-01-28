package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;


public class ResetPasswordRequest {
    
    private String token;
    private Long idCredencial;
    private Integer exists; // 1 si el correo existe, 0 si no
    private Integer valid;  // 1 si el token es válido, 0 si no
    private String email;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getIdCredencial() {
        return idCredencial;
    }

    public void setIdCredencial(Long idCredencial) {
        this.idCredencial = idCredencial;
    }

    public Integer getExists() {
        return exists;
    }

    public void setExists(Integer exists) {
        this.exists = exists;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
