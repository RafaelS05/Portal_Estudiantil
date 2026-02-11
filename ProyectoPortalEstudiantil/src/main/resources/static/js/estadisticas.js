// ==================== ESTADÍSTICAS - FORMULARIO ====================

document.addEventListener('DOMContentLoaded', function() {
    
    // Configurar calculadora de porcentaje
    setupCalculadora();
    
    // Validaciones en tiempo real
    setupValidaciones();
    
    // Auto-calcular reprobados
    setupAutoCalculos();
});

/**
 * Configura el botón de calcular porcentaje
 */
function setupCalculadora() {
    const btnCalcular = document.getElementById('calcularPorcentaje');
    if (!btnCalcular) return;
    
    btnCalcular.addEventListener('click', function() {
        calcularPorcentaje();
    });
}

/**
 * Calcula el porcentaje de aprobación automáticamente
 */
function calcularPorcentaje() {
    const totalEstudiantes = parseFloat(document.getElementById('totalEstudiantes').value) || 0;
    const aprobados = parseFloat(document.getElementById('aprobados').value) || 0;
    const porcentajeInput = document.getElementById('porcentajeAprobacion');
    
    if (totalEstudiantes === 0) {
        alert('Debe ingresar el total de estudiantes primero');
        return;
    }
    
    if (aprobados > totalEstudiantes) {
        alert('El número de aprobados no puede ser mayor que el total de estudiantes');
        return;
    }
    
    const porcentaje = (aprobados / totalEstudiantes) * 100;
    porcentajeInput.value = porcentaje.toFixed(2);
    
    // Efecto visual
    porcentajeInput.classList.add('bg-success', 'bg-opacity-10');
    setTimeout(() => {
        porcentajeInput.classList.remove('bg-success', 'bg-opacity-10');
    }, 1000);
}

/**
 * Configura validaciones en tiempo real
 */
function setupValidaciones() {
    // Validar que las notas estén entre 0 y 100
    const notaInputs = document.querySelectorAll('input[type="number"][min="0"][max="100"]');
    
    notaInputs.forEach(input => {
        input.addEventListener('blur', function() {
            const valor = parseFloat(this.value);
            
            if (valor < 0) {
                this.value = 0;
                mostrarAlerta(this, 'El valor no puede ser negativo');
            } else if (valor > 100) {
                this.value = 100;
                mostrarAlerta(this, 'El valor no puede ser mayor a 100');
            }
        });
    });
    
    // Validar nota máxima >= nota mínima
    const notaMaxima = document.querySelector('input[name="notaMaxima"]');
    const notaMinima = document.querySelector('input[name="notaMinima"]');
    
    if (notaMaxima && notaMinima) {
        notaMaxima.addEventListener('blur', validarRangoNotas);
        notaMinima.addEventListener('blur', validarRangoNotas);
    }
}

/**
 * Valida que la nota máxima sea mayor o igual a la mínima
 */
function validarRangoNotas() {
    const notaMaxima = parseFloat(document.querySelector('input[name="notaMaxima"]').value) || 0;
    const notaMinima = parseFloat(document.querySelector('input[name="notaMinima"]').value) || 0;
    
    if (notaMaxima < notaMinima) {
        alert('La nota máxima debe ser mayor o igual a la nota mínima');
        document.querySelector('input[name="notaMaxima"]').focus();
    }
}

/**
 * Configura cálculos automáticos
 */
function setupAutoCalculos() {
    const totalEstudiantes = document.getElementById('totalEstudiantes');
    const aprobados = document.getElementById('aprobados');
    const reprobados = document.getElementById('reprobados');
    
    if (!totalEstudiantes || !aprobados || !reprobados) return;
    
    // Calcular reprobados automáticamente
    function calcularReprobados() {
        const total = parseFloat(totalEstudiantes.value) || 0;
        const aprobadosVal = parseFloat(aprobados.value) || 0;
        
        if (aprobadosVal > total) {
            alert('El número de aprobados no puede ser mayor que el total de estudiantes');
            aprobados.value = total;
            return;
        }
        
        const reprobadosVal = total - aprobadosVal;
        reprobados.value = reprobadosVal;
        
        // Auto-calcular porcentaje si los campos están completos
        if (total > 0 && aprobadosVal >= 0) {
            calcularPorcentaje();
        }
    }
    
    totalEstudiantes.addEventListener('input', calcularReprobados);
    aprobados.addEventListener('input', calcularReprobados);
}

/**
 * Muestra una alerta temporal junto a un input
 */
function mostrarAlerta(input, mensaje) {
    // Remover alertas anteriores
    const alertaAnterior = input.parentElement.querySelector('.validation-alert');
    if (alertaAnterior) {
        alertaAnterior.remove();
    }
    
    // Crear nueva alerta
    const alerta = document.createElement('div');
    alerta.className = 'validation-alert text-danger small mt-1';
    alerta.innerHTML = `<i class="fa-solid fa-exclamation-circle me-1"></i>${mensaje}`;
    
    input.parentElement.appendChild(alerta);
    
    // Quitar alerta después de 3 segundos
    setTimeout(() => {
        alerta.remove();
    }, 3000);
}

/**
 * Valida el formulario antes de enviar
 */
function validarFormulario(event) {
    const form = event.target;
    
    // Validaciones personalizadas
    const promedio = parseFloat(form.querySelector('input[name="promedio"]').value) || 0;
    const notaMaxima = parseFloat(form.querySelector('input[name="notaMaxima"]').value) || 0;
    const notaMinima = parseFloat(form.querySelector('input[name="notaMinima"]').value) || 0;
    
    if (notaMaxima < notaMinima) {
        alert('La nota máxima debe ser mayor o igual a la nota mínima');
        event.preventDefault();
        return false;
    }
    
    if (promedio < notaMinima || promedio > notaMaxima) {
        alert('El promedio debe estar entre la nota mínima y la nota máxima');
        event.preventDefault();
        return false;
    }
    
    return true;
}

// Adjuntar validación al formulario
document.querySelector('form')?.addEventListener('submit', validarFormulario);