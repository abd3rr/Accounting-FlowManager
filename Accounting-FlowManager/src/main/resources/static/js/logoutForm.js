document.addEventListener('DOMContentLoaded', function () {
    const logoutForm = document.getElementById('logoutForm');
    const userId = logoutForm.getAttribute('data-user-id');

    function clearFormDataFromSession(userId) {
        if (userId) {
            const formDataKey = 'invoiceFormData-' + userId;
            if (sessionStorage.getItem(formDataKey)) {
                sessionStorage.removeItem(formDataKey);
                console.log('Session data cleared for user:', userId);
            }
        }
    }

    clearFormDataFromSession(userId);

    // Optionally delay the logout submission to ensure all scripts have finished executing
    setTimeout(function () {
        logoutForm.submit();
    }, 500); // Delay of 500 milliseconds
});