package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByIdUsuario(Long idUsuario);

    List<Usuario> findByNombreContaining(String nombre);

    List<Usuario> findByIdTipoUsuarioFk(Long idTipoUsuarioFk);

    List<Usuario> findByIdEstadoFk(Long idEstadoFk);

    long countByIdEstadoFk(Long idEstadoFk);

    long countByIdTipoUsuarioFk(Long idTipoUsuarioFk);

    List<Usuario> findByIdTipoUsuarioFkAndIdEstadoFk(Long idTipoUsuario, Long idEstado);
    
    // (para la regla del último admin)
    long countByIdTipoUsuarioFkAndIdEstadoFk(Long idTipoUsuarioFk, Long idEstadoFk);

    @Query("SELECT u FROM Usuario u WHERE CONCAT(u.nombre, ' ', u.primerApellido, ' ', u.segundoApellido) LIKE %:nombreCompleto%")
    List<Usuario> findByNombreCompletoContaining(@Param("nombreCompleto") String nombreCompleto);

    // BUSCAR ENCARGADO POR CORREO (búsqueda parcial)
    @Query(value = """
        SELECT u.*
        FROM USUARIOS_TB u
        JOIN CORREO_TB co ON co.ID_USUARIO_FK = u.ID_USUARIO
        WHERE LOWER(co.CORREO) LIKE LOWER(CONCAT('%', :correo, '%'))
          AND co.ES_LOGIN = 'S'
          AND u.ID_TIPOUSUARIO_FK = :idTipoEncargado
          AND u.ID_ESTADO_FK = 1
          AND co.ID_ESTADO_FK = 1
    """, nativeQuery = true)
    List<Usuario> buscarEncargadosPorCorreo(@Param("correo") String correo,
            @Param("idTipoEncargado") Long idTipoEncargado);

    // LISTADO ESTUDIANTES (vista completa con contacto y encargado)
    interface EstudianteListadoRow {

        Long getIdUsuario();

        String getNombre();

        String getPrimerApellido();

        String getSegundoApellido();

        String getTelefono();

        String getCorreoLogin();

        String getEncargadoNombre();
    }

    @Query(value = """
        SELECT
            u.ID_USUARIO         AS idUsuario,
            u.NOMBRE             AS nombre,
            u.PRIMER_APELLIDO    AS primerApellido,
            u.SEGUNDO_APELLIDO   AS segundoApellido,
            t.NUMERO             AS telefono,
            co.CORREO            AS correoLogin,
            CONCAT(ue.NOMBRE, ' ', ue.PRIMER_APELLIDO,
                   CASE WHEN ue.SEGUNDO_APELLIDO IS NULL OR ue.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', ue.SEGUNDO_APELLIDO) END
            ) AS encargadoNombre
        FROM USUARIOS_TB u
        LEFT JOIN TELEFONO_TB t
               ON t.ID_USUARIO_FK = u.ID_USUARIO AND t.ID_ESTADO_FK = 1
        LEFT JOIN CORREO_TB co
               ON co.ID_USUARIO_FK = u.ID_USUARIO AND co.ES_LOGIN = 'S' AND co.ID_ESTADO_FK = 1
        LEFT JOIN ENCARGADOESTUDIANTE_TB ee
               ON ee.ID_USUARIO_ESTUDIANTE_FK = u.ID_USUARIO
              AND ee.ID_ESTADO_FK = 1
              AND ee.ID_ENCARGADOESTUDIANTE = (
                    SELECT MAX(ee2.ID_ENCARGADOESTUDIANTE)
                    FROM ENCARGADOESTUDIANTE_TB ee2
                    WHERE ee2.ID_USUARIO_ESTUDIANTE_FK = u.ID_USUARIO
                      AND ee2.ID_ESTADO_FK = 1
               )
        LEFT JOIN USUARIOS_TB ue
               ON ue.ID_USUARIO = ee.ID_USUARIO_ENCARGADO_FK
        WHERE u.ID_TIPOUSUARIO_FK = :idTipoEstudiante
          AND u.ID_ESTADO_FK = 1
        ORDER BY u.ID_USUARIO DESC
    """, nativeQuery = true)
    List<EstudianteListadoRow> listarEstudiantesConContactoYEncargado(@Param("idTipoEstudiante") Long idTipoEstudiante);

    @Query(value = """
        SELECT
            u.ID_USUARIO         AS idUsuario,
            u.NOMBRE             AS nombre,
            u.PRIMER_APELLIDO    AS primerApellido,
            u.SEGUNDO_APELLIDO   AS segundoApellido,
            t.NUMERO             AS telefono,
            co.CORREO            AS correoLogin,
            CONCAT(ue.NOMBRE, ' ', ue.PRIMER_APELLIDO,
                   CASE WHEN ue.SEGUNDO_APELLIDO IS NULL OR ue.SEGUNDO_APELLIDO = ''
                        THEN '' ELSE CONCAT(' ', ue.SEGUNDO_APELLIDO) END
            ) AS encargadoNombre
        FROM USUARIOS_TB u
        LEFT JOIN TELEFONO_TB t
               ON t.ID_USUARIO_FK = u.ID_USUARIO AND t.ID_ESTADO_FK = 1
        LEFT JOIN CORREO_TB co
               ON co.ID_USUARIO_FK = u.ID_USUARIO AND co.ES_LOGIN = 'S' AND co.ID_ESTADO_FK = 1
        LEFT JOIN ENCARGADOESTUDIANTE_TB ee
               ON ee.ID_USUARIO_ESTUDIANTE_FK = u.ID_USUARIO
              AND ee.ID_ESTADO_FK = 1
              AND ee.ID_ENCARGADOESTUDIANTE = (
                    SELECT MAX(ee2.ID_ENCARGADOESTUDIANTE)
                    FROM ENCARGADOESTUDIANTE_TB ee2
                    WHERE ee2.ID_USUARIO_ESTUDIANTE_FK = u.ID_USUARIO
                      AND ee2.ID_ESTADO_FK = 1
               )
        LEFT JOIN USUARIOS_TB ue
               ON ue.ID_USUARIO = ee.ID_USUARIO_ENCARGADO_FK
        WHERE u.ID_TIPOUSUARIO_FK = :idTipoEstudiante
          AND u.ID_ESTADO_FK = 1
          AND (
              LOWER(u.NOMBRE)          LIKE LOWER(CONCAT('%', :busqueda, '%')) OR
              LOWER(u.PRIMER_APELLIDO) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR
              LOWER(u.SEGUNDO_APELLIDO)LIKE LOWER(CONCAT('%', :busqueda, '%')) OR
              LOWER(CONCAT(u.NOMBRE, ' ', u.PRIMER_APELLIDO)) LIKE LOWER(CONCAT('%', :busqueda, '%'))
          )
        ORDER BY u.ID_USUARIO DESC
    """, nativeQuery = true)
    List<EstudianteListadoRow> buscarEstudiantesConContacto(
            @Param("idTipoEstudiante") Long idTipoEstudiante,
            @Param("busqueda") String busqueda
    );

    // CRUD
    @Query(value = """
        CALL USUARIOS_INSERTAR(
            :nombre,
            :primerApellido,
            :segundoApellido,
            :idTipoUsuarioFk,
            :idEstadoFk
        )
    """, nativeQuery = true)
    Long insertarUsuarioRetornaId(
            @Param("nombre") String nombre,
            @Param("primerApellido") String primerApellido,
            @Param("segundoApellido") String segundoApellido,
            @Param("idTipoUsuarioFk") Long idTipoUsuarioFk,
            @Param("idEstadoFk") Long idEstadoFk
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL USUARIOS_MODIFICAR(
            :idUsuario,
            :nombre,
            :primerApellido,
            :segundoApellido,
            :idTipoUsuarioFk
        )
    """, nativeQuery = true)
    void modificarUsuario(
            @Param("idUsuario") Long idUsuario,
            @Param("nombre") String nombre,
            @Param("primerApellido") String primerApellido,
            @Param("segundoApellido") String segundoApellido,
            @Param("idTipoUsuarioFk") Long idTipoUsuarioFk
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL USUARIOS_CAMBIAR_ESTADO(
            :idUsuario,
            :idEstado
        )
    """, nativeQuery = true)
    void cambiarEstadoUsuario(
            @Param("idUsuario") Long idUsuario,
            @Param("idEstado") Long idEstado
    );

    //Busca el Tipo de usuario 3 = Estudiante para la matricula
    @Query(value = """
    SELECT *
    FROM USUARIOS_TB
    WHERE ID_TIPOUSUARIO_FK = 3
      AND ID_ESTADO_FK = 1
    ORDER BY PRIMER_APELLIDO, NOMBRE
""", nativeQuery = true)
    List<Usuario> listarEstudiantes();

}
