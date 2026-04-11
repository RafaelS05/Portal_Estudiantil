window.addEventListener("load", function () {

    const modalEl = document.getElementById("modalDesactivar");
    if (modalEl) {
        const modal = new bootstrap.Modal(modalEl);
        const confirmar = document.getElementById("confirmarDesactivar");
        document.querySelectorAll(".btn-desactivar").forEach(btn => {
            btn.addEventListener("click", function () {
                const id = this.getAttribute("data-id");
                confirmar.href = "/evaluaciones/estado/" + id + "?estado=2";
                modal.show();
            });
        });
    }

    const formulario = document.querySelector("form");
    if (formulario) {
        formulario.addEventListener("submit", function (e) {
            const campos = formulario.querySelectorAll("input, select, textarea");
            let hayVacio = false;

            campos.forEach(campo => {
                if (campo.type === "hidden") return;

                if (!campo.value || campo.value.trim() === "") {
                    hayVacio = true;
                    campo.classList.add("is-invalid"); 
                } else {
                    campo.classList.remove("is-invalid");
                }
            });

            if (hayVacio) {
                e.preventDefault();
                mostrarAlerta("Debe rellenar todos los campos antes de guardar.");
            }
        });

        formulario.querySelectorAll("input, select, textarea").forEach(campo => {
            campo.addEventListener("input", function () {
                this.classList.remove("is-invalid");
            });
        });
    }

    function mostrarAlerta(mensaje) {
        const existente = document.getElementById("alerta-validacion");
        if (existente) existente.remove();

        const alerta = document.createElement("div");
        alerta.id = "alerta-validacion";
        alerta.className = "alert alert-danger alert-dismissible fade show mt-3";
        alerta.role = "alert";
        alerta.innerHTML = `
            <i class="fa-solid fa-circle-exclamation me-2"></i>
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        formulario.insertAdjacentElement("beforebegin", alerta);

        setTimeout(() => alerta.remove(), 4000);
    }
});