package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;


public class ResetPasswordRequest {
    
    private String token;
    private Long idCredencial;
    private Integer exists;
    private Integer valid;
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
