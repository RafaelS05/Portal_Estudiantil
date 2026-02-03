package PortalEstudiantil.ProyectoPortalEstudiantil.Controller;

import PortalEstudiantil.ProyectoPortalEstudiantil.Domain.ResetPasswordRequest;
import PortalEstudiantil.ProyectoPortalEstudiantil.Service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/resetContrasenna")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordService;

    @GetMapping("/recuperar-password")
    public String recuperarView() {
        return "resetContrasenna/recuperar-password";
    }

    @PostMapping("/recuperar-password")
    public String procesarRecuperar(@RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {
        System.out.println(" DEBUG: POST /resetContrasenna/recuperar-password - Email: " + email);
        
        try {
            passwordService.requestPasswordReset(email);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Si el correo existe en nuestro sistema, recibirás un enlace de recuperación.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ha sucedido un error procesando su solicitud. Porfavor intente nuevamente.");
        }

        return "redirect:/resetContrasenna/recuperar-password";
    }

    @GetMapping("/reset-password")
    public String resetView(@RequestParam("token") String token, 
            Model model, RedirectAttributes redirectAttributes) {

        System.out.println("DEBUG: GET /resetContrasenna/reset-password - Token: " + token);
        ResetPasswordRequest validation = passwordService.validateToken(token);

        if (validation.getValid() == null || validation.getValid() == 0) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "El enlace de recuperación es inválido o ha expirado.");
            return "resetContrasenna/reset-password";
        }

        model.addAttribute("token", token);
        model.addAttribute("email", validation.getEmail());
        model.addAttribute("successMessage", "Se ha restablecido su contraseña porfavor inicie sesión.");
        return "resetContrasenna/reset-password";
    }

    @PostMapping("/reset-password")
    public String procesarReset(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes,
            Model model) {
        System.out.println("POST reset token recibido: [" + token + "]");
        
        if (token == null || token.isBlank()) {
            model.addAttribute("errorMessage", "No hay token. Debes ingresar desde el enlace de recuperación.");
            return "resetContrasenna/recuperar-password";
        }
        
        if (!password.equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("errorMessage", "Las contraseñas no coinciden.");
            return "resetContrasenna//reset-password";
        }

        if (!passwordService.isValidPassword(password)) {
            model.addAttribute("token", token);
            model.addAttribute("errorMessage",
                    "La contraseña no cumple con los requisitos de seguridad.");
            return "resetContrasenna//reset-password";
        }

        boolean success = passwordService.resetPassword(token, password);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Tu contraseña ha sido restablecida exitosamente. Puedes iniciar sesión.");
            return "redirect:/login";

        }
        model.addAttribute("token", token);
        model.addAttribute("errorMessage",
                "No se pudo restablecer la contraseña. El enlace puede haber expirado.");
        return "resetContrasenna//reset-password";
    }
}
