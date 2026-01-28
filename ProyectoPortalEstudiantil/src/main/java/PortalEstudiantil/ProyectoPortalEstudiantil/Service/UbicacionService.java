/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Canton;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Distrito;
import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.Provincia;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.CantonRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.DistritoRepository;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.ProvinciaRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UbicacionService {

    private final ProvinciaRepository provinciaRepo;
    private final CantonRepository cantonRepo;
    private final DistritoRepository distritoRepo;

    public UbicacionService(
            ProvinciaRepository provinciaRepo,
            CantonRepository cantonRepo,
            DistritoRepository distritoRepo) {

        this.provinciaRepo = provinciaRepo;
        this.cantonRepo = cantonRepo;
        this.distritoRepo = distritoRepo;
    }

    public List<Provincia> listarProvincias() {
        return provinciaRepo.findAll();
    }

    public List<Canton> listarCantonesPorProvincia(Long idProvincia) {
        return cantonRepo.findByProvincia_IdProvincia(idProvincia);
    }

    public List<Distrito> listarDistritosPorCanton(Long idCanton) {
        return distritoRepo.findByCanton_IdCanton(idCanton);
    }
}

