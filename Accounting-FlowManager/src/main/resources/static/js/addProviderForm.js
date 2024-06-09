let selectedProviders = [];

function selectProvider(element) {
    const providerId = element.getAttribute('data-provider-id');
    if (selectedProviders.includes(providerId)) {
        selectedProviders = selectedProviders.filter(id => id !== providerId);
        element.classList.remove('active');
    } else {
        selectedProviders.push(providerId);
        element.classList.add('active');
    }
    document.getElementById('selectedProviders').value = selectedProviders.join(',');
    updateValidationMessage();  // Update validation message anytime selection changes
}

function updateValidationMessage() {
    const errorDiv = document.getElementById('providerError');
    if (selectedProviders.length > 0) {
        errorDiv.style.display = 'none';
    } else {
        errorDiv.style.display = 'block';
    }
}

document.querySelector('form').addEventListener('submit', function (event) {
    if (selectedProviders.length === 0) {
        event.preventDefault(); // Prevent form submission
        updateValidationMessage(); // Display the error message without an alert
    }
});
