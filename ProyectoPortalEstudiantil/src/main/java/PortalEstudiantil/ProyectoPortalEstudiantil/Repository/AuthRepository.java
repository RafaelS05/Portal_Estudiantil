package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.AuthUserData;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Map;

@Repository
public class AuthRepository {
    
    private final SimpleJdbcCall getUserByEmailCall;
    private final SimpleJdbcCall loginSuccessCall;
    private final SimpleJdbcCall loginFailCall;

    public AuthRepository(DataSource dataSource) {
        this.getUserByEmailCall = new SimpleJdbcCall(dataSource)
                .withCatalogName("PORTAL_ESCOLAR_PKG")
                .withProcedureName("AUTH_GET_USER_BY_EMAIL")
                .declareParameters(
                        new SqlParameter("p_email", Types.VARCHAR),
                        new SqlOutParameter("p_username", Types.VARCHAR),
                        new SqlOutParameter("p_password_hash", Types.VARCHAR),
                        new SqlOutParameter("p_role", Types.VARCHAR),
                        new SqlOutParameter("p_intentos", Types.NUMERIC),
                        new SqlOutParameter("p_bloqueado_hasta", Types.TIMESTAMP),
                        new SqlOutParameter("p_enabled", Types.NUMERIC),
                        new SqlOutParameter("p_id_credencial", Types.NUMERIC)
                );

        this.loginSuccessCall = new SimpleJdbcCall(dataSource)
                .withCatalogName("PORTAL_ESCOLAR_PKG")
                .withProcedureName("AUTH_LOGIN_SUCCESS")
                .declareParameters(new SqlParameter("p_id_credencial", Types.NUMERIC));

        this.loginFailCall = new SimpleJdbcCall(dataSource)
                .withCatalogName("PORTAL_ESCOLAR_PKG")
                .withProcedureName("AUTH_LOGIN_FAIL")
                .declareParameters(
                        new SqlParameter("p_id_credencial", Types.NUMERIC),
                        new SqlParameter("p_max_intentos", Types.NUMERIC),
                        new SqlParameter("p_minutos_bloqueo", Types.NUMERIC)
                );
    }

    public AuthUserData getUserByEmail(String email) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_email", email);
        
        Map<String, Object> out = getUserByEmailCall.execute(in);
        
        AuthUserData u = new AuthUserData();
        u.setUsername((String) out.get("p_username"));
        u.setPasswordHash((String) out.get("p_password_hash"));
        u.setRole((String) out.get("p_role"));
        
        Object intentos = out.get("p_intentos");
        u.setIntentos(intentos == null ? 0 : ((Number) intentos).intValue());
        
        u.setBloqueadoHasta((Timestamp) out.get("p_bloqueado_hasta"));
        
        Object enabled = out.get("p_enabled");
        u.setEnabled(enabled == null ? 0 : ((Number) enabled).intValue());
        
        Object idCred = out.get("p_id_credencial");
        u.setIdCredencial(idCred == null ? null : ((Number) idCred).longValue());
        
        return u;
    }

    public void loginSuccess(Long idCredencial) {
        if (idCredencial == null) return;
        loginSuccessCall.execute(new MapSqlParameterSource()
                .addValue("p_id_credencial", idCredencial));
    }

    public void loginFail(Long idCredencial, int maxIntentos, int minutosBloqueo) {
        if (idCredencial == null) return;
        loginFailCall.execute(new MapSqlParameterSource()
                .addValue("p_id_credencial", idCredencial)
                .addValue("p_max_intentos", maxIntentos)
                .addValue("p_minutos_bloqueo", minutosBloqueo));
    }
}