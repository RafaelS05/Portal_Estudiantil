/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Distrito;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DistritoRepository extends JpaRepository<Distrito, Long> {

    List<Distrito> findByCanton_IdCanton(Long idCanton);
}
