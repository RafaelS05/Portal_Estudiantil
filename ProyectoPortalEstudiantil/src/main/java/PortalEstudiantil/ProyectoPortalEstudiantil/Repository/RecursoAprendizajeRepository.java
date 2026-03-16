/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.RecursoAprendizaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 *
 * @author marjo
 */
@Repository
public interface RecursoAprendizajeRepository extends JpaRepository<RecursoAprendizaje, Integer> {
    
    List<RecursoAprendizaje> findByIdEstadoFk(Integer idEstadoFk);
    
    default List<RecursoAprendizaje> findActivos() {
        return findByIdEstadoFk(1);
    }
    
    List<RecursoAprendizaje> findByTituloContainingIgnoreCase(String titulo);
    
    boolean existsByUrlRecurso(String urlRecurso);
}
