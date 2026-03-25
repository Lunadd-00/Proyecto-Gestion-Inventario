document.addEventListener('DOMContentLoaded', function () {
    const modalIds = ['confirmModal', 'confirmModalUsuario'];

    modalIds.forEach(modalId => {
        const confirmModal = document.getElementById(modalId);

        if (confirmModal) {
            confirmModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;

                const modalTitulo = document.getElementById('modalTitulo');
                const modalTexto1 = document.getElementById('modalTexto1');
                const modalTexto2 = document.getElementById('modalTexto2');
                const modalIcon = document.getElementById('modalIcon');

                const modalIdInput = document.getElementById('modalId');
                const modalNombre = document.getElementById('modalnombre');

                if (modalIdInput)
                    modalIdInput.value = button.getAttribute('data-bs-id');
                if (modalNombre)
                    modalNombre.textContent = button.getAttribute('data-bs-nombre');

                const activoAttr = button.dataset.bsActivo;
                if (activoAttr !== undefined) {
                    const activo = activoAttr === 'true';
                    const btnSubmit = confirmModal.querySelector('button[type="submit"]');

                    if (activo) {
                        btnSubmit.innerHTML = '<i class="fa-solid fa-trash"></i> Desactivar';
                        btnSubmit.className = 'btn btn-danger';
                        if (modalTitulo)
                            modalTitulo.textContent = 'Confirmar desactivación de usuario';
                        if (modalTexto1)
                            modalTexto1.innerHTML = '¿Está seguro que desea desactivar al usuario <span id="modalnombre">' + button.getAttribute('data-bs-nombre') + '</span>?';
                        if (modalTexto2)
                            modalTexto2.textContent = 'Esta acción cambiará el estado del usuario y no podrá acceder al sistema.';
                        if (modalIcon)
                            modalIcon.className = 'fa-solid fa-user-xmark';
                    } else {
                        btnSubmit.innerHTML = '<i class="fa-solid fa-check"></i> Activar';
                        btnSubmit.className = 'btn btn-success';
                        if (modalTitulo)
                            modalTitulo.textContent = 'Confirmar activación de usuario';
                        if (modalTexto1)
                            modalTexto1.innerHTML = '¿Está seguro que desea activar al usuario <span id="modalnombre">' + button.getAttribute('data-bs-nombre') + '</span>?';
                        if (modalTexto2)
                            modalTexto2.textContent = 'El usuario podrá volver a acceder al sistema.';
                        if (modalIcon)
                            modalIcon.className = 'fa-solid fa-user-check';
                    }
                }
            });
        }
    });
});

setTimeout(() => {
    document.querySelectorAll('.toast').forEach(t => t.classList.remove('show'));
}, 4000);