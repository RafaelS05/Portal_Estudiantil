/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Service;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.*;
import PortalEstudiantil.ProyectoPortalEstudiantil.Repository.*;
import jakarta.transaction.Transactional;
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
        return telefonoRepo.findByUsuario_IdUsuario(idUsuario);
    }

    public Correo obtenerCorreoLogin(Long idUsuario) {
        return correoRepo.findByUsuario_IdUsuarioAndEsLogin(idUsuario, "S");
    }

    public Direccion obtenerDireccion(Long idUsuario) {
        return direccionRepo.findByUsuario_IdUsuario(idUsuario);
    }

    // ACTUALIZAR INFORMACIÓN
    @Transactional
    public void actualizarInformacion(
            Usuario usuarioForm,
            String correoTxt,
            String numeroTxt,
            String otrasSenas
    ) {

        // USUARIO
        Usuario usuario = usuarioRepo
                .findById(usuarioForm.getIdUsuario())
                .orElseThrow();

        usuario.setNombre(usuarioForm.getNombre());
        usuario.setPrimerApellido(usuarioForm.getPrimerApellido());
        usuario.setSegundoApellido(usuarioForm.getSegundoApellido());

        // CORREO (login)
        Correo correo = correoRepo
                .findByUsuario_IdUsuarioAndEsLogin(usuario.getIdUsuario(), "S");

        if (correo != null) {
            correo.setCorreo(correoTxt);
        }

        // TELÉFONO
        Telefono telefono = telefonoRepo
                .findByUsuario_IdUsuario(usuario.getIdUsuario());

        if (telefono != null) {
            telefono.setNumero(numeroTxt);
        }

        // DIRECCIÓN
        Direccion direccion = direccionRepo
                .findByUsuario_IdUsuario(usuario.getIdUsuario());

        if (direccion != null) {
            direccion.setOtrasSenas(otrasSenas);
        }
    }

//    @Transactional
//    public void guardarDireccion(
//            Long idUsuario,
//            String otrasSenas,
//            Long idProvincia,
//            Long idCanton,
//            Long idDistrito
//    ) {
//
//        Direccion direccionBD
//                = direccionRepo.findByUsuario_IdUsuario(idUsuario);
//
//        if (direccionBD == null) {
//            // 👉 INSERT
//            direccionRepo.insertarDireccion(
//                    otrasSenas,
//                    idUsuario,
//                    idProvincia,
//                    idCanton,
//                    idDistrito,
//                    1L // estado ACTIVO
//            );
//
//        } else {
//            // 👉 UPDATE
//            direccionRepo.modificarDireccion(
//                    direccionBD.getIdDireccion(),
//                    otrasSenas,
//                    idUsuario,
//                    idProvincia,
//                    idCanton,
//                    idDistrito
//            );
//        }
//    }

//    @Transactional
//    public void actualizarCorreoLogin(Long idUsuario, String correoTxt) {
//
//        Correo correoBD
//                = correoRepo.findByUsuario_IdUsuarioAndEsLogin(idUsuario, "S");
//
//        if (correoBD == null) {
//            throw new IllegalStateException(
//                    "El usuario no tiene correo login registrado"
//            );
//        }
//
//        correoRepo.modificarCorreo(
//                correoBD.getIdCorreo(),
//                correoTxt
//        );
//    }

}
