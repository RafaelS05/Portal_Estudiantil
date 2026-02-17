package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Horario;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface HorarioRepository extends JpaRepository<Horario, Long>{

    Horario findByIdHorario(Long idHorario);

    List<Horario> findByIdEstadoFk(Long idEstadoFk);

    long countByIdEstadoFk(Long idEstadoFk);

    // Consultas útiles
    @Query(value = """
        SELECT *
        FROM HORARIO_TB
        WHERE ID_ESTADO_FK = 1
        ORDER BY DIA_SEMANA ASC, HORA_INICIO ASC
    """, nativeQuery = true)
    List<Horario> listarActivos();

    @Query(value = """
        SELECT *
        FROM HORARIO_TB
        WHERE ID_SECCIONMATERIA_FK = :idSeccionMateria
        ORDER BY DIA_SEMANA ASC, HORA_INICIO ASC
    """, nativeQuery = true)
    List<Horario> listarPorSeccionMateria(@Param("idSeccionMateria") Long idSeccionMateria);

    @Query(value = """
        SELECT *
        FROM HORARIO_TB
        WHERE ID_AULA_FK = :idAula
        ORDER BY DIA_SEMANA ASC, HORA_INICIO ASC
    """, nativeQuery = true)
    List<Horario> listarPorAula(@Param("idAula") Long idAula);

    @Query(value = """
        SELECT *
        FROM HORARIO_TB
        WHERE ID_SECCIONMATERIA_FK = :idSeccionMateria
          AND DIA_SEMANA = :diaSemana
        ORDER BY HORA_INICIO ASC
    """, nativeQuery = true)
    List<Horario> listarPorSeccionMateriaYDia(@Param("idSeccionMateria") Long idSeccionMateria,
            @Param("diaSemana") Integer diaSemana);

    // Validaciones (UQ)
    // UQ: (DIA_SEMANA, HORA_INICIO, HORA_FIN, ID_SECCIONMATERIA_FK)
    @Query(value = """
        SELECT COUNT(*)
        FROM HORARIO_TB
        WHERE DIA_SEMANA = :diaSemana
          AND HORA_INICIO = :horaInicio
          AND HORA_FIN = :horaFin
          AND ID_SECCIONMATERIA_FK = :idSeccionMateriaFk
          AND (:idHorario IS NULL OR ID_HORARIO <> :idHorario)
    """, nativeQuery = true)
    long contarDuplicadoBloque(
            @Param("diaSemana") Integer diaSemana,
            @Param("horaInicio") String horaInicio,
            @Param("horaFin") String horaFin,
            @Param("idSeccionMateriaFk") Long idSeccionMateriaFk,
            @Param("idHorario") Long idHorario
    );

    @Query(value = """
        SELECT *
        FROM HORARIO_TB
        WHERE DIA_SEMANA = :diaSemana
          AND HORA_INICIO = :horaInicio
          AND HORA_FIN = :horaFin
          AND ID_SECCIONMATERIA_FK = :idSeccionMateriaFk
        LIMIT 1
    """, nativeQuery = true)
    Optional<Horario> findFirstByBloque(@Param("diaSemana") Integer diaSemana,
            @Param("horaInicio") String horaInicio,
            @Param("horaFin") String horaFin,
            @Param("idSeccionMateriaFk") Long idSeccionMateriaFk);

    // CRUD
    @Modifying
    @Transactional
    @Query(value = """
        CALL HORARIO_INSERTAR(
            :diaSemana,
            :horaInicio,
            :horaFin,
            :idAulaFk,
            :idSeccionMateriaFk,
            :idEstadoFk
        )
    """, nativeQuery = true)
    Long insertarHorarioRetornaId(
            @Param("diaSemana") Integer diaSemana,
            @Param("horaInicio") String horaInicio,
            @Param("horaFin") String horaFin,
            @Param("idAulaFk") Long idAulaFk,
            @Param("idSeccionMateriaFk") Long idSeccionMateriaFk,
            @Param("idEstadoFk") Long idEstadoFk
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL HORARIO_MODIFICAR(
            :idHorario,
            :diaSemana,
            :horaInicio,
            :horaFin,
            :idAulaFk,
            :idSeccionMateriaFk
        )
    """, nativeQuery = true)
    void modificarHorario(
            @Param("idHorario") Long idHorario,
            @Param("diaSemana") Integer diaSemana,
            @Param("horaInicio") String horaInicio,
            @Param("horaFin") String horaFin,
            @Param("idAulaFk") Long idAulaFk,
            @Param("idSeccionMateriaFk") Long idSeccionMateriaFk
    );

    @Modifying
    @Transactional
    @Query(value = """
        CALL HORARIO_CAMBIAR_ESTADO(
            :idHorario,
            :idEstado
        )
    """, nativeQuery = true)
    void cambiarEstadoHorario(
            @Param("idHorario") Long idHorario,
            @Param("idEstado") Long idEstado
    );

}
