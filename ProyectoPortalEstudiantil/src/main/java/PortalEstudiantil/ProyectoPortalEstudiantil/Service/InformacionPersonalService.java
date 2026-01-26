/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Service;



import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.*;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.*;
import org.springframework.stereotype.Service;

@Service
public class InformacionPersonalService {

    private final UsuarioRepository usuarioRepo;
    private final TelefonoRepository telefonoRepo;
    private final CorreoRepository correoRepo;
    private final DireccionRepository direccionRepo;

    public InformacionPersonalService(
            UsuarioRepository usuarioRepo,
            TelefonoRepository telefonoRepo,
            CorreoRepository correoRepo,
            DireccionRepository direccionRepo) {

        this.usuarioRepo = usuarioRepo;
        this.telefonoRepo = telefonoRepo;
        this.correoRepo = correoRepo;
        this.direccionRepo = direccionRepo;
    }

    public Usuario obtenerUsuario(Long idUsuario) {
        return usuarioRepo.findById(idUsuario).orElse(null);
    }

    public Telefono obtenerTelefono(Long idUsuario) {
        return telefonoRepo.findByIdUsuario(idUsuario);
    }

    public Correo obtenerCorreoLogin(Long idUsuario) {
        return correoRepo.findByIdUsuarioAndEsLogin(idUsuario, "S");
    }

    public Direccion obtenerDireccion(Long idUsuario) {
        return direccionRepo.findByUsuario_IdUsuario(idUsuario);
    }

    public void guardarUsuario(Usuario usuario) {
        usuarioRepo.save(usuario);
    }

    public void guardarTelefono(Telefono telefono) {
        telefonoRepo.save(telefono);
    }

    public void guardarCorreo(Correo correo) {
        correoRepo.save(correo);
    }

    public void guardarDireccion(Direccion direccion) {
        direccionRepo.save(direccion);
    }
}
