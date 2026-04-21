///* 
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
// */
document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('selectSeccion').addEventListener('change', function () {
        const idSeccion = this.value;
        const selectMatricula = document.getElementById('selectMatricula');

        selectMatricula.innerHTML = '<option value="" disabled selected>Cargando...</option>';
        selectMatricula.disabled = true;

        fetch(`/calificaciones/matriculas-por-seccion?idSeccion=${idSeccion}`)
            .then(res => res.json())
            .then(data => {
                selectMatricula.innerHTML = '<option value="" disabled selected>Seleccione un estudiante...</option>';
                data.forEach(m => {
                    const opt = document.createElement('option');
                    opt.value = m.id;
                    opt.textContent = m.nombre;
                    selectMatricula.appendChild(opt);
                });
                selectMatricula.disabled = false;
            })
            .catch(() => {
                selectMatricula.innerHTML = '<option value="" disabled selected>Error al cargar</option>';
            });
    });
});