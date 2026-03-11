/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */
document.addEventListener("DOMContentLoaded", function(){

    const modal = new bootstrap.Modal(document.getElementById("modalDesactivar"));
    const confirmar = document.getElementById("confirmarDesactivar");

    document.querySelectorAll(".btn-desactivar").forEach(btn => {

        btn.addEventListener("click", function(){

            const id = this.getAttribute("data-id");

            confirmar.href = "/evaluaciones/estado/" + id + "?estado=2";

            modal.show();

        });

    });

});

