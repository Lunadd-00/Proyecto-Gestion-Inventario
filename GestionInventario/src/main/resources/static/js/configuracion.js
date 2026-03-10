document.addEventListener('DOMContentLoaded', function () {
    const modalIds = ['confirmModal', 'confirmModalUsuario'];

    modalIds.forEach(modalId => {
        const confirmModal = document.getElementById(modalId);

        if (confirmModal) {
            confirmModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;

                const modalIdInput = document.getElementById('modalId');
                const modalNombre = document.getElementById('modalnombre');

                if (modalIdInput)
                    modalIdInput.value = button.getAttribute('data-bs-id');
                if (modalNombre)
                    modalNombre.textContent = button.getAttribute('data-bs-nombre');

                const activo = button.getAttribute('data-bs-activo');
                if (activo !== null) {
                    const btnSubmit = confirmModal.querySelector('button[type="submit"]');

                    if (activo === 'true') {
                        btnSubmit.innerHTML = '<i class="fa-solid fa-trash"></i> [[#{usuario.desactivar}]]';
                        btnSubmit.className = 'btn btn-danger';
                    } else {
                        btnSubmit.innerHTML = '<i class="fa-solid fa-check"></i> [[#{usuario.activar}]]';
                        btnSubmit.className = 'btn btn-success';
                    }
                }
            });
        }
    });
});

setTimeout(() => {
    document.querySelectorAll('.toast').forEach(t => t.classList.remove('show'));
}, 4000);


