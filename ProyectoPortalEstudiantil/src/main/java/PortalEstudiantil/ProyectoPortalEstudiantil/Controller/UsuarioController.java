/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsuarioController {

    @GetMapping("/usuarios")
    public String gestionUsuarios() {
        return "usuarios";
    }
}