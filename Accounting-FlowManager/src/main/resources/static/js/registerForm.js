// Password confirmation validation
const passwordInput = document.getElementById('password');
const confirmPasswordInput = document.getElementById('confirmPassword');

confirmPasswordInput.addEventListener('input', function () {
    if (passwordInput.value !== confirmPasswordInput.value) {
        confirmPasswordInput.setCustomValidity('Passwords do not match');
    } else {
        confirmPasswordInput.setCustomValidity('');
    }
});

$(document).ready(function() {
    // JavaScript form validation
    const forms = document.querySelectorAll('.needs-validation');

    Array.prototype.slice.call(forms)
        .forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (!form.checkValidity() || !validateSelectedProviders()) {
                    event.preventDefault();
                    event.stopPropagation();
                }

                form.classList.add('was-validated');
            }, false);
        });

    $('.provider-list .list-group-item').click(function() {
        $(this).toggleClass('active');
        updateSelectedProviders();
    });

    $('#providerSearch').on('input', function() {
        const searchTerm = $(this).val().toLowerCase();
        $('.provider-list .list-group-item').each(function() {
            const providerName = $(this).text().toLowerCase();
            if (providerName.includes(searchTerm)) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    });

    function updateSelectedProviders() {
        var selectedProviders = $('.provider-list .list-group-item.active')
            .map(function() {
                return $(this).data('provider-id');
            })
            .get()
            .join(',');
        $('#selectedProviders').val(selectedProviders);
    }

    function validateSelectedProviders() {
        const selectedProviders = $('.provider-list .list-group-item.active');
        const providerError = $('#providerError');

        if (selectedProviders.length === 0) {
            providerError.show();
            return false;
        } else {
            providerError.hide();
            return true;
        }
    }
});