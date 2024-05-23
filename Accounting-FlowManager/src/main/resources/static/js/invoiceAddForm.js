$(document).ready(function () {
    let uniqueId = 1; // Start with an initial ID

    loadFormDataFromSession(); // Load form data from session storage on page load

    validateForm(); // Initial validation on page load

    $('#addProductRow').click(function () {
        let newRow = $('.product-row-template').first().clone().show();
        resetNewRow(newRow, uniqueId);
        newRow.hide().insertBefore('#addProductRow').fadeIn(200); // Faster appearance with fadeIn
        uniqueId++; // Increment the ID for the next row
        validateForm(); // Re-validate the form after adding a new row
        checkProductRows(); // Check product rows after adding
        saveFormDataToSession(); // Save form data to session storage
    });

    // Validate on every change in the form
    $('form').on('change', 'select, input', function () {
        validateForm();
        saveFormDataToSession(); // Save form data to session storage
    });

    $('#productsContainer').on('click', '.removeProductRow', function () {
        $(this).closest('.row').fadeOut(150, function () { // Faster removal with fadeOut
            $(this).remove();
            validateForm(); // Re-validate after removal
            checkProductRows(); // Check product rows after removal
            saveFormDataToSession(); // Save form data to session storage
        });
    });

    // Update product info on product select change
    $('#productsContainer').on('change', '.product-select', function () {
        updateProductInfo(this);
        validateForm(); // Re-validate when product info is updated
        saveFormDataToSession(); // Save form data to session storage
    });

    $('form').submit(function (event) {
        if (!validateForm()) { // Prevent submission if validation fails
            event.preventDefault();
        } else {
            saveFormDataToSession(); // Save form data to session storage before submitting
            removeTemplateNames(); // Remove names from template row before submission
        }
    });

    checkProductRows(); // Initial check for product rows
});

function resetNewRow(newRow, uniqueId) {
    newRow.find('.product-select').attr('name', 'products[' + uniqueId + '].productId');
    newRow.find('.price-input').attr('name', 'products[' + uniqueId + '].price');
    newRow.find('.quantity-input').attr('name', 'products[' + uniqueId + '].quantity');
    newRow.find('.currency-input').attr('name', 'products[' + uniqueId + '].currency');
    newRow.find('select').val('helper');
    newRow.find('.form-control').val('');
    newRow.attr('id', 'productRow' + uniqueId);
}

function updateProductInfo(element) {
    let row = $(element).closest('.row');
    let price = $(element).find('option:selected').data('price');
    let currency = $(element).find('option:selected').data('currency');
    let quantity = row.find('.quantity-input').val();
    let totalPrice = quantity * price;

    row.find('.price-input').val(totalPrice.toFixed(2));
    row.find('.currency-input').val(currency);
}

function validateForm() {
    let isValid = true;

    console.log("Starting validation...");

    // Check all visible select elements
    $('select:visible').each(function () {
        if ($(this).val() === "helper" || $(this).val() === "") {
            $(this).addClass('is-invalid');
            isValid = false;
            console.log("Invalid select found:", $(this).attr('name'));
        } else {
            $(this).removeClass('is-invalid');
        }
    });

    // Check all visible quantity inputs
    $('.quantity-input:visible').each(function () {
        if ($(this).val() === "" || parseInt($(this).val(), 10) < 1) {
            $(this).addClass('is-invalid');
            isValid = false;
            console.log("Invalid quantity input found:", $(this).attr('name'));
        } else {
            $(this).removeClass('is-invalid');
        }
    });

    // Ensure there is at least one product row
    if ($('#productsContainer .product-row-template:visible').length === 0) {
        isValid = false;
        $('#productAlert').fadeIn(200); // Show alert if no product row
        console.log("No product rows found.");
    } else {
        $('#productAlert').fadeOut(200); // Hide alert if product row exists
    }

    console.log("Form validation status:", isValid);
    $('#submit input[type="submit"]').prop('disabled', !isValid);
    return isValid;
}

function checkProductRows() {
    let productRows = $('#productsContainer .product-row-template:visible');
    if (productRows.length === 0) {
        $('#productAlert').fadeIn(200); // Faster appearance with fadeIn
    } else {
        $('#productAlert').fadeOut(200); // Faster disappearance with fadeOut
    }
}

function saveFormDataToSession() {
    let formData = {
        clientId: $('select[name="clientId"]').val(),
        shippingCostType: $('select[name="shippingCostType"]').val(),
        reduction: $('select[name="reduction"]').val(),
        additionalReduction: $('select[name="additionalReduction"]').val(),
        products: []
    };

    $('#productsContainer .product-row-template:visible').each(function (index, row) {
        let product = {
            productId: $(row).find('.product-select').val(),
            quantity: $(row).find('.quantity-input').val(),
            price: $(row).find('.price-input').val(),
            currency: $(row).find('.currency-input').val()
        };
        formData.products.push(product);
    });

    sessionStorage.setItem('invoiceFormData', JSON.stringify(formData));
}

function loadFormDataFromSession() {
    let formData = sessionStorage.getItem('invoiceFormData');
    if (formData) {
        formData = JSON.parse(formData);

        $('select[name="clientId"]').val(formData.clientId);
        $('select[name="shippingCostType"]').val(formData.shippingCostType);
        $('select[name="reduction"]').val(formData.reduction);
        $('select[name="additionalReduction"]').val(formData.additionalReduction);

        formData.products.forEach(function (product, index) {
            if (index > 0) {
                $('#addProductRow').click();
            }
            let row = $('#productsContainer .product-row-template:visible').eq(index);
            row.find('.product-select').val(product.productId);
            row.find('.quantity-input').val(product.quantity);
            row.find('.price-input').val(product.price);
            row.find('.currency-input').val(product.currency);
            updateProductInfo(row.find('.product-select'));
        });
    }
}

function removeTemplateNames() {
    // Specifically target the template row by its initial hidden state and remove its name attributes
    $('.product-row-template:hidden').each(function () {
        $(this).find('input, select').removeAttr('name');
    });
}
