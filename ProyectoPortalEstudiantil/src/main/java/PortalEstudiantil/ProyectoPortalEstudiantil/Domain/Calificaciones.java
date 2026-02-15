/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PortalEstudiantil.ProyectoPortalEstudiantil.Domain;

import jakarta.persistence.*;

/**
 *
 * @author marjo
 */

@Entity
@Table(name = "CALIFICACIONES_TB")

public class Calificaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CALIFICACIONES")
    private Long idCalificaciones;
    
    @Column(name = "CALIFICACION", precision = 5, scale = 2)
    private java.math.BigDecimal calificacion;
  
    // Aquí falta el FK de Estados
    // Falta el de Matrícula
    // Falta el de Evaluación
    
    
}
