
// Activa el tab que devuelve el controller via ?tab=xxx
document.addEventListener('DOMContentLoaded', function () {
    const tabActivo = /*[[${tabActivo}]]*/ 'periodos';
    const btn = document.getElementById('tab-' + tabActivo);
    new bootstrap.Tab(btn ?? document.getElementById('tab-periodos')).show();
});

// Filtro de tabla
function filterTable(id, val) {
    document.querySelectorAll('#' + id + ' tbody tr').forEach(r => {
        r.style.display = r.textContent.toLowerCase().includes(val.toLowerCase()) ? '' : 'none';
    });
}

// Llenar modales de edición
function llenarEditarPeriodo(b) {
    document.getElementById('ep-id').value = b.dataset.id;
    document.getElementById('ep-nombre').value = b.dataset.nombre;
    document.getElementById('ep-inicio').value = b.dataset.inicio;
    document.getElementById('ep-fin').value = b.dataset.fin;
}
function llenarEditarMateria(b) {
    document.getElementById('em-id').value = b.dataset.id;
    document.getElementById('em-nombre').value = b.dataset.nombre;
    document.getElementById('em-codigo').value = b.dataset.codigo !== 'null' ? b.dataset.codigo : '';
}
function llenarEditarSeccion(b) {
    document.getElementById('es-id').value = b.dataset.id;
    document.getElementById('es-numero').value = b.dataset.numero;
    document.getElementById('es-periodo').value = b.dataset.periodo;
}
function llenarEditarAula(b) {
    document.getElementById('ea-id').value = b.dataset.id;
    document.getElementById('ea-numero').value = b.dataset.numero;
}
function llenarEditarSM(b) {
    document.getElementById('esm-id').value = b.dataset.id;
    document.getElementById('esm-seccion').value = b.dataset.seccion;
    document.getElementById('esm-materia').value = b.dataset.materia;
    document.getElementById('esm-docente').value = b.dataset.docente;
}
function llenarEditarHorario(b) {
    document.getElementById('eh-id').value = b.dataset.id;
    document.getElementById('eh-dia').value = b.dataset.dia;
    document.getElementById('eh-inicio').value = b.dataset.inicio;
    document.getElementById('eh-fin').value = b.dataset.fin;
    document.getElementById('eh-sm').value = b.dataset.sm;
    document.getElementById('eh-aula').value = b.dataset.aula !== 'null' ? b.dataset.aula : '';
}
