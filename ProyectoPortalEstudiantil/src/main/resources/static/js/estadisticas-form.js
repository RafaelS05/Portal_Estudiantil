// ==================== ESTADÍSTICAS - LISTADO Y DASHBOARD ====================

document.addEventListener('DOMContentLoaded', function() {
    
    // Inicializar anillos de progreso
    initProgressRings();
    
    // Configurar botones de acción
    setupActionButtons();
    
    // Auto-cerrar alertas
    autoCloseAlerts();
});

/**
 * Inicializa los anillos de progreso circulares
 */
function initProgressRings() {
    const rings = document.querySelectorAll('.progress-ring-fill');
    
    rings.forEach(ring => {
        const percent = parseFloat(ring.getAttribute('data-percent')) || 0;
        const circumference = 2 * Math.PI * 25; // radio = 25
        const offset = circumference - (percent / 100) * circumference;
        
        // Determinar color según porcentaje
        let color = '#10b981'; // verde por defecto
        if (percent < 50) {
            color = '#ef4444'; // rojo
        } else if (percent < 70) {
            color = '#f59e0b'; // naranja
        }
        
        ring.style.stroke = color;
        
        // Animar el anillo
        setTimeout(() => {
            ring.style.strokeDashoffset = offset;
        }, 100);
    });
}

/**
 * Configura los botones de acción de la tabla
 */
function setupActionButtons() {
    // Botones de estado
    const estadoBtns = document.querySelectorAll('[data-action="cambiar-estado"]');
    estadoBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const id = this.getAttribute('data-id');
            const estado = this.getAttribute('data-estado');
            cambiarEstado(id, estado);
        });
    });
}

/**
 * Confirma la eliminación de una estadística
 */
function confirmarEliminacion(button) {
    const id = button.getAttribute('data-id');
    
    if (confirm('¿Está seguro de que desea eliminar esta estadística?\n\nEsta acción no se puede deshacer.')) {
        const form = document.getElementById('formEliminar');
        form.action = `/estadisticas/eliminar/${id}`;
        form.submit();
    }
}

/**
 * Cambia el estado de una estadística
 */
function cambiarEstado(id, nuevoEstado) {
    const estadoTexto = nuevoEstado == 1 ? 'activar' : 'desactivar';
    
    if (confirm(`¿Está seguro de que desea ${estadoTexto} esta estadística?`)) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `/estadisticas/cambiar-estado/${id}`;
        
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'idEstado';
        input.value = nuevoEstado;
        
        form.appendChild(input);
        document.body.appendChild(form);
        form.submit();
    }
}

/**
 * Auto-cierra las alertas después de 5 segundos
 */
function autoCloseAlerts() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
}

/**
 * Formatea números con separadores de miles
 */
function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

/**
 * Exportar datos a CSV (función opcional)
 */
function exportarCSV() {
    const tabla = document.querySelector('table');
    let csv = [];
    const filas = tabla.querySelectorAll('tr');
    
    filas.forEach(fila => {
        const cols = fila.querySelectorAll('td, th');
        const csvRow = [];
        cols.forEach(col => {
            csvRow.push(col.innerText);
        });
        csv.push(csvRow.join(','));
    });
    
    const csvContent = csv.join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'estadisticas.csv';
    a.click();
}