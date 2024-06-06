document.addEventListener('DOMContentLoaded', function() {
    // Assuming 'userId' is passed dynamically and we've added a data attribute in the HTML to retrieve it.
    var userId = document.querySelector('form').getAttribute('data-user-id');

    function clearFormDataFromSession(userId) {
        sessionStorage.removeItem('invoiceFormData-' + userId);
    }

    clearFormDataFromSession(userId);

    // Automatically submit the logout form
    document.querySelector('form').submit();
});
