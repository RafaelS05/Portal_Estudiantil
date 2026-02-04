// Esperar a que el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {

    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebarClose = document.getElementById('sidebarClose');
    const sidebar = document.getElementById('appSidebar');
    const overlay = document.getElementById('sidebarOverlay');
    const body = document.body;

    // Función para abrir sidebar
    function openSidebar() {
        sidebar.classList.add('active');
        overlay.classList.add('active');
        body.classList.add('sidebar-open');
    }

    // Función para cerrar sidebar
    function closeSidebar() {
        sidebar.classList.remove('active');
        overlay.classList.remove('active');
        body.classList.remove('sidebar-open');
    }

    // Event listener para el botón hamburguesa
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function(e) {
            e.stopPropagation();
            openSidebar();
        });
    }

    // Event listener para el botón cerrar
    if (sidebarClose) {
        sidebarClose.addEventListener('click', function(e) {
            e.stopPropagation();
            closeSidebar();
        });
    }

    // Event listener para el overlay
    if (overlay) {
        overlay.addEventListener('click', function() {
            closeSidebar();
        });
    }

    // Cerrar sidebar al hacer clic en un link (solo en móvil)
    const sidebarLinks = sidebar.querySelectorAll('.sidebar-link');

    sidebarLinks.forEach(function(link) {
        link.addEventListener('click', function() {

            // Solo cerrar en pantallas pequeñas
            if (window.innerWidth <= 991) {
                closeSidebar();
            }
        });
    });

    // Cerrar sidebar al presionar la tecla ESC
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && sidebar.classList.contains('active')) {
            closeSidebar();
        }
    });

    // Cerrar sidebar al cambiar el tamaño de la ventana a desktop
    window.addEventListener('resize', function() {
        if (window.innerWidth > 991) {
            closeSidebar();
        }
    });

});
