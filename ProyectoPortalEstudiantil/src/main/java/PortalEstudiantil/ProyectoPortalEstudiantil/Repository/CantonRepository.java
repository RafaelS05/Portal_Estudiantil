/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Repository;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Canton;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CantonRepository extends JpaRepository<Canton, Long> {

    List<Canton> findByProvincia_IdProvincia(Long idProvincia);
}
