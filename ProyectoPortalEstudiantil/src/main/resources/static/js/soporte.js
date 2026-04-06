function setStar(el) {
    const val = parseInt(el.getAttribute('data-val'));
    document.getElementById('inputPuntuacion').value = val;
    document.querySelectorAll('.star-btn').forEach(function (s, i) {
        s.className = (i < val)
                ? 'fa-solid fa-star fa-2x star-btn'
                : 'fa-regular fa-star fa-2x star-btn';
        s.style.color = (i < val) ? '#f9a825' : '#dee2e6';
    });
}

let pasoActual = 1;

/* ── Actualiza visual del stepper ───────────── */
function actualizarStepper() {
    [1, 2, 3].forEach(function (i) {
        const circulo = document.getElementById('sc' + i);
        const label = document.getElementById('si' + i);
        if (i < pasoActual) {
            circulo.className = 'step-circle done';
            circulo.textContent = '✓';
            label.className = 'step-label done';
        } else if (i === pasoActual) {
            circulo.className = 'step-circle active';
            circulo.textContent = i;
            label.className = 'step-label active';
        } else {
            circulo.className = 'step-circle inactive';
            circulo.textContent = i;
            label.className = 'step-label';
        }
    });

    [1, 2].forEach(function (i) {
        document.getElementById('sl' + i).className =
                i < pasoActual ? 'step-line done' : 'step-line';
    });

    document.getElementById('btnAnterior').style.visibility =
            pasoActual > 1 ? 'visible' : 'hidden';

    const btnSig = document.getElementById('btnSiguiente');
    if (pasoActual === 3) {
        btnSig.innerHTML = 'Enviar <i class="fa-solid fa-paper-plane ms-1"></i>';
        btnSig.classList.replace('btn-primary', 'btn-success');
    } else {
        btnSig.innerHTML = 'Siguiente <i class="fa-solid fa-arrow-right ms-1"></i>';
        if (btnSig.classList.contains('btn-success'))
            btnSig.classList.replace('btn-success', 'btn-primary');
    }
}

function mostrarPaso(n) {
    [1, 2, 3].forEach(function (i) {
        document.getElementById('paso' + i).style.display = i === n ? 'block' : 'none';
    });
    pasoActual = n;
    actualizarStepper();
}

function resetModal() {
    document.getElementById('formNuevoTicket').reset();
    ['inputTitulo', 'inputDesc', 'selectCat'].forEach(function (id) {
        document.getElementById(id).classList.remove('is-invalid');
    });
    document.getElementById('errPrioridad').style.display = 'none';
    mostrarPaso(1);
    actualizarContador('inputTitulo', 'ccTitulo', 120);
    actualizarContador('inputDesc', 'ccDesc', 800);
}

function validarPaso1() {
    let ok = true;
    const titulo = document.getElementById('inputTitulo');
    const desc = document.getElementById('inputDesc');
    const cat = document.getElementById('selectCat');
    [titulo, desc, cat].forEach(function (el) {
        el.classList.remove('is-invalid');
    });
    if (!titulo.value.trim()) {
        titulo.classList.add('is-invalid');
        ok = false;
    }
    if (!desc.value.trim()) {
        desc.classList.add('is-invalid');
        ok = false;
    }
    if (!cat.value) {
        cat.classList.add('is-invalid');
        ok = false;
    }
    return ok;
}

function validarPaso2() {
    const sel = document.querySelector('input[name="prioridad"]:checked');
    document.getElementById('errPrioridad').style.display = sel ? 'none' : 'block';
    return !!sel;
}

function rellenarResumen() {
    const titulo = document.getElementById('inputTitulo').value.trim() || '—';
    const catEl = document.getElementById('selectCat');
    const cat = catEl.options[catEl.selectedIndex]?.text || '—';
    const prioEl = document.querySelector('input[name="prioridad"]:checked');
    const prio = prioEl ? prioEl.value : '—';
    document.getElementById('cd-titulo').textContent = titulo;
    document.getElementById('cd-cat').textContent = cat;
    document.getElementById('cd-prio').textContent = prio;
}

function pasoSiguiente() {
    if (pasoActual === 1 && !validarPaso1())
        return;
    if (pasoActual === 2 && !validarPaso2())
        return;
    if (pasoActual === 3) {
        document.getElementById('formNuevoTicket').submit();
        return;
    }
    if (pasoActual === 2)
        rellenarResumen();
    mostrarPaso(pasoActual + 1);
}

function pasoAnterior() {
    if (pasoActual > 1)
        mostrarPaso(pasoActual - 1);
}

function actualizarContador(inputId, counterId, max) {
    const len = document.getElementById(inputId).value.length;
    const el = document.getElementById(counterId);
    el.textContent = len + ' / ' + max;
    el.classList.toggle('char-count-warning', len > max * 0.9);
}

document.getElementById('inputTitulo').addEventListener('input', function () {
    actualizarContador('inputTitulo', 'ccTitulo', 120);
});
document.getElementById('inputDesc').addEventListener('input', function () {
    actualizarContador('inputDesc', 'ccDesc', 800);
});

/* ─────────────────────────────────────── */
document.addEventListener('DOMContentLoaded', actualizarStepper);

    function previewImagen(input) {
        if (!input.files || !input.files[0]) return;
        const file = input.files[0];

        // Validar tipo
        if (!['image/png','image/jpeg'].includes(file.type)) {
            alert('Solo se permiten imágenes PNG o JPG.');
            limpiarPreview(); return;
        }
        // Validar tamaño
        if (file.size > 5 * 1024 * 1024) {
            alert('El archivo no puede superar los 5 MB.');
            limpiarPreview(); return;
        }

        const reader = new FileReader();
        reader.onload = e => {
            document.getElementById('previewImg').src     = e.target.result;
            document.getElementById('previewNombre').textContent = file.name;
            document.getElementById('previewTamano').textContent =
                '(' + (file.size / 1024).toFixed(0) + ' KB)';
            document.getElementById('previewContainer').style.display = 'block';
            document.getElementById('dropZone').style.display = 'none';
            document.getElementById('btnSubir').disabled = false;
        };
        reader.readAsDataURL(file);
    }

    function limpiarPreview() {
        document.getElementById('inputArchivo').value    = '';
        document.getElementById('previewImg').src        = '';
        document.getElementById('previewContainer').style.display = 'none';
        document.getElementById('dropZone').style.display         = 'block';
        document.getElementById('btnSubir').disabled = true;
    }

    function handleDrop(event) {
        event.preventDefault();
        document.getElementById('dropZone').style.borderColor = '#c8d0e0';
        const input = document.getElementById('inputArchivo');
        input.files = event.dataTransfer.files;
        previewImagen(input);
    }

    function abrirImagen(src) {
        document.getElementById('imagenAmpliada').src = src;
        new bootstrap.Modal(document.getElementById('modalImagen')).show();
    }