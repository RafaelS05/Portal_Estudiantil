
// Activa el tab que devuelve el controller via ?tab=xxx
document.addEventListener('DOMContentLoaded', function () {
    const tabActivo = /*[[${tabActivo}]]*/ 'periodos';
    const btn = document.getElementById('tab-' + tabActivo);
    new bootstrap.Tab(btn ?? document.getElementById('tab-periodos')).show();

    document.querySelectorAll('select.tbl-filter').forEach(poblarFiltro);
});

// Filtro de tabla
function filterTable(id, val) {
    document.querySelectorAll('#' + id + ' tbody tr').forEach(r => {
        r.style.display = r.textContent.toLowerCase().includes(val.toLowerCase()) ? '' : 'none';
    });
}

// ── Filtros combinados por tab (secciones, secc-materia, horarios) ──
const NOMBRES_GRADO = {
    1: 'Primer Grado', 2: 'Segundo Grado', 3: 'Tercer Grado',
    4: 'Cuarto Grado', 5: 'Quinto Grado', 6: 'Sexto Grado'
};
const ORDEN_DIAS = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'];

// Filas de datos de la tabla (excluye la fila "No hay registros")
function filasDe(tableId) {
    return [...document.querySelectorAll('#' + tableId + ' tbody tr')]
        .filter(r => !r.querySelector('td[colspan]'));
}

// Grado = número inicial de la sección ("1-A" → "1")
function gradoDe(numero) {
    const m = numero.trim().match(/^\d+/);
    return m ? m[0] : '';
}

// Llena cada select con los valores únicos de su columna
function poblarFiltro(sel) {
    const col = Number(sel.dataset.col);
    const valores = new Set();
    filasDe(sel.dataset.table).forEach(r => {
        const txt = r.cells[col].textContent.trim();
        if (sel.dataset.mode === 'grado') {
            const g = gradoDe(txt);
            if (g) valores.add(g);
        } else if (txt && txt !== '—') {
            valores.add(txt);
        }
    });
    let lista = [...valores];
    if (sel.dataset.mode === 'grado') lista.sort((a, b) => a - b);
    else if (sel.dataset.mode === 'dia') lista.sort((a, b) => ORDEN_DIAS.indexOf(a) - ORDEN_DIAS.indexOf(b));
    else lista.sort((a, b) => a.localeCompare(b, 'es'));
    lista.forEach(v => {
        const opt = document.createElement('option');
        opt.value = v;
        opt.textContent = sel.dataset.mode === 'grado' ? (NOMBRES_GRADO[v] ?? 'Grado ' + v) : v;
        sel.appendChild(opt);
    });
}

function aplicarFiltros(tableId) {
    const filtros = [...document.querySelectorAll('select.tbl-filter[data-table="' + tableId + '"]')];
    const buscador = document.querySelector('input.tbl-search[data-table="' + tableId + '"]');
    const texto = (buscador ? buscador.value : '').toLowerCase();
    let visibles = 0;
    filasDe(tableId).forEach(r => {
        let ok = !texto || r.textContent.toLowerCase().includes(texto);
        ok = ok && filtros.every(f => {
            if (!f.value) return true;
            const celda = r.cells[Number(f.dataset.col)].textContent.trim();
            return f.dataset.mode === 'grado' ? gradoDe(celda) === f.value : celda === f.value;
        });
        r.style.display = ok ? '' : 'none';
        if (ok) visibles++;
    });
    const contador = document.querySelector('#' + tableId)
        .closest('.tbl-card').querySelector('.card-footer strong');
    if (contador) contador.textContent = visibles;
}

function limpiarFiltros(tableId) {
    document.querySelectorAll('select.tbl-filter[data-table="' + tableId + '"]').forEach(s => s.value = '');
    const buscador = document.querySelector('input.tbl-search[data-table="' + tableId + '"]');
    if (buscador) buscador.value = '';
    aplicarFiltros(tableId);
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

function verEstudiantes(btn) {
    const idSeccion = btn.dataset.id;
    document.getElementById('est-seccion-numero').textContent = btn.dataset.numero;

    const filas = document.querySelectorAll('.fila-estudiante');
    filas.forEach(f => f.classList.add('d-none'));

    const visibles = [...filas].filter(f => f.dataset.seccion == idSeccion);
    visibles.forEach(f => f.classList.remove('d-none'));

    document.getElementById('est-total').textContent = visibles.length;
    document.getElementById('est-vacio').classList.toggle('d-none', visibles.length > 0);

    new bootstrap.Modal(document.getElementById('modal-estudiantes-seccion')).show();
}