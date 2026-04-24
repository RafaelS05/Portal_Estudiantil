            const NIVEL_TEXTO = {1: 'Deficiente', 2: 'Regular', 3: 'Bueno', 4: 'Muy Bueno', 5: 'Excelente'};
            const NIVEL_CLASE = {1: 'fb-nivel-1', 2: 'fb-nivel-2', 3: 'fb-nivel-3', 4: 'fb-nivel-4', 5: 'fb-nivel-5'};

            document.addEventListener('change', e => {
                if (!e.target.classList.contains('cal-input'))
                    return;
                const badge = e.target.closest('tr')?.querySelector('.nivel-label');
                if (!badge)
                    return;
                const v = parseInt(e.target.value);
                badge.textContent = v ? NIVEL_TEXTO[v] : '—';
                badge.className = 'badge nivel-label ' + (v ? NIVEL_CLASE[v] : 'fb-nivel-3');
            });

            document.getElementById('modalEvaluar')?.addEventListener('shown.bs.modal', () => {
                document.querySelectorAll('#batchForm .cal-input').forEach(sel => {
                    const v = parseInt(sel.value);
                    const badge = sel.closest('tr')?.querySelector('.nivel-label');
                    if (!badge)
                        return;
                    badge.textContent = v ? NIVEL_TEXTO[v] : '—';
                    badge.className = 'badge nivel-label ' + (v ? NIVEL_CLASE[v] : 'fb-nivel-3');
                });
            });

            function guardarBatch() {
                const idSeccionmateria = parseInt(document.getElementById('idSeccionmateria')?.value);
                if (!idSeccionmateria) {
                    showToast('Seleccioná una sección-materia primero.', 'danger');
                    return;
                }
                const filas = document.querySelectorAll('#batchForm tbody tr');
                const evaluaciones = [];
                let valido = true;
                filas.forEach(fila => {
                    const idMatricula = parseInt(fila.dataset.idmatricula);
                    const idFeedback = fila.dataset.idfeedback ? parseInt(fila.dataset.idfeedback) : null;
                    const calSelect = fila.querySelector('.cal-input');
                    const calificacion = calSelect ? parseInt(calSelect.value) : null;
                    const comentario = fila.querySelector('.comentario-input')?.value?.trim() || null;
                    if (!calificacion || calificacion < 1 || calificacion > 5) {
                        calSelect?.classList.add('is-invalid');
                        valido = false;
                        return;
                    }
                    calSelect?.classList.remove('is-invalid');
                    evaluaciones.push({idMatricula, calificacion, comentario, idFeedbackExistente: idFeedback});
                });
                if (!valido) {
                    showToast('Calificá a todos los estudiantes (1–5) antes de guardar.', 'danger');
                    return;
                }
                const btn = document.querySelector('#modalEvaluar .btn-primary');
                btn.disabled = true;
                btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Guardando...';
                fetch('/feedback/registrar', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json', 'X-CSRF-TOKEN': getCsrfToken()},
                    body: JSON.stringify({idSeccionmateria, evaluaciones})
                })
                        .then(r => r.json())
                        .then(data => {
                            btn.disabled = false;
                            btn.innerHTML = '<i class="bi bi-save-fill me-1"></i>Guardar Evaluaciones';
                            if (data.ok) {
                                bootstrap.Modal.getInstance(document.getElementById('modalEvaluar'))?.hide();
                                showToast(data.mensaje, 'success');
                                setTimeout(() => location.reload(), 1800);
                            } else {
                                showToast(data.mensaje || 'Error al guardar.', 'danger');
                            }
                        })
                        .catch(() => {
                            btn.disabled = false;
                            btn.innerHTML = '<i class="bi bi-save-fill me-1"></i>Guardar Evaluaciones';
                            showToast('Error de conexión. Intentá de nuevo.', 'danger');
                        });
            }

            function limpiarFormulario() {
                document.querySelectorAll('#batchForm .cal-input').forEach(s => {
                    s.value = '';
                    s.classList.remove('is-invalid');
                });
                document.querySelectorAll('#batchForm .comentario-input').forEach(t => t.value = '');
                document.querySelectorAll('#batchForm .nivel-label').forEach(b => {
                    b.textContent = '—';
                    b.className = 'badge nivel-label fb-nivel-3';
                });
            }

            function getCsrfToken() {
                return document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || '';
            }

            function verComentario(btn) {
                document.getElementById('modalComentarioTexto').textContent = btn.dataset.comentario || 'Sin comentario.';
                new bootstrap.Modal(document.getElementById('modalComentario')).show();
            }

            function showToast(msg, tipo = 'success') {
                const container = document.getElementById('toastContainer');
                const id = 'toast-' + Date.now();
                const icon = tipo === 'success' ? 'bi-check-circle-fill' : 'bi-x-circle-fill';
                container.insertAdjacentHTML('beforeend',
                        `<div id="${id}" class="toast align-items-center text-white bg-${tipo} border-0"
                          role="alert" data-bs-autohide="true" data-bs-delay="3500">
                        <div class="d-flex">
                            <div class="toast-body fw-semibold"><i class="bi ${icon} me-2"></i>${msg}</div>
                            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                        </div>
                    </div>`);
                bootstrap.Toast.getOrCreateInstance(document.getElementById(id)).show();
            }

            const urlParams = new URLSearchParams(location.search);
            if (urlParams.get('activeTab') === 'comparativa') {
                const t = document.querySelector('[data-bs-target="#tab-comparativa"]');
                if (t)
                    bootstrap.Tab.getOrCreateInstance(t).show();
            }

