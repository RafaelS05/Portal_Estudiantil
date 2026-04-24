/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Evaluacion;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author marjo
 */
@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    Page<Evaluacion> findByTipoContainingIgnoreCase(String busqueda, Pageable pageable);

    Page<Evaluacion> findByIdEstadoFk(Long estado, Pageable pageable);

    Page<Evaluacion> findByTipoContainingIgnoreCaseAndIdEstadoFk(String busqueda, Long estado, Pageable pageable);

    List<Evaluacion> findByIdEstadoFk(Long estado);
}
