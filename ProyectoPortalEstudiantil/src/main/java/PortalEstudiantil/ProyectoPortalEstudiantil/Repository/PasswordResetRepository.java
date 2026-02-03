package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.ResetPasswordRequest;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;

@Repository
public class PasswordResetRepository {
    
    private SimpleJdbcCall requestResetCall;
    private SimpleJdbcCall validateTokenCall;
    private SimpleJdbcCall resetPassowrdCall;

    public PasswordResetRepository(DataSource dataSource) {
        this.requestResetCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("AUTH_REQUEST_RESET")
                .declareParameters(
                        new SqlParameter("p_email", Types.VARCHAR),
                        new SqlOutParameter("p_token", Types.VARCHAR),
                        new SqlOutParameter("p_id_credencial", Types.NUMERIC),
                        new SqlOutParameter("p_exists", Types.NUMERIC)
                );

        this.validateTokenCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("AUTH_VALIDATE_TOKEN")
                .declareParameters(
                        new SqlParameter("p_token", Types.VARCHAR),
                        new SqlOutParameter("p_valid", Types.NUMERIC),
                        new SqlOutParameter("p_id_credencial", Types.NUMERIC),
                        new SqlOutParameter("p_email", Types.VARCHAR)
                );

        this.resetPassowrdCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("AUTH_RESET_PASSWORD")
                .declareParameters(
                        new SqlParameter("p_token", Types.VARCHAR),
                        new SqlParameter("p_new_password_hash", Types.VARCHAR),
                        new SqlOutParameter("p_success", Types.NUMERIC)
                );

    }

    public ResetPasswordRequest requestReset(String email) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_email", email);

        Map<String, Object> result = requestResetCall.execute(params);

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken((String) result.get("p_token"));

        Object exists = result.get("p_exists");
        request.setExists(exists == null ? 0 : ((Number) exists).intValue());

        Object id_credencial = result.get("p_id_credencial");
        request.setIdCredencial(id_credencial == null ? null : ((Number) id_credencial).longValue());

        return request;
    }

    public ResetPasswordRequest validateToken(String token) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_token", token);

        Map<String, Object> result = validateTokenCall.execute(params);

        ResetPasswordRequest request = new ResetPasswordRequest();

        Object valid = result.get("p_valid");
        request.setValid(valid == null ? 0 : ((Number) valid).intValue());

        Object idCred = result.get("p_id_credencial");
        request.setIdCredencial(idCred == null ? null : ((Number) idCred).longValue());

        request.setEmail((String) result.get("p_email"));

        return request;
    }
    
    public boolean resetPassword(String token, String newPasswordHash){
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_token", token)
                .addValue("p_new_password_hash", newPasswordHash);
        Map<String, Object> result = resetPassowrdCall.execute(params);
        
        Object success = result.get("p_success");
        return success != null && ((Number) success).intValue() == 1;
    }
}
