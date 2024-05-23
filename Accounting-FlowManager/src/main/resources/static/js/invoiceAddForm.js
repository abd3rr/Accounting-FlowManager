$(document).ready(function () {
    let uniqueId = 1; // Start with an initial ID

    validateForm(); // Initial validation on page load

    $('#addProductRow').click(function () {
        let newRow = $('.product-row-template').first().clone().show();
        resetNewRow(newRow, uniqueId);
        newRow.hide().insertBefore('#addProductRow').fadeIn(200); // Faster appearance with fadeIn
        uniqueId++; // Increment the ID for the next row
        validateForm(); // Re-validate the form after adding a new row
        checkProductRows(); // Check product rows after adding
    });

    // Validate on every change in the form
    $('form').on('change', 'select, input', function () {
        validateForm();
    });

    $('#productsContainer').on('click', '.removeProductRow', function () {
        $(this).closest('.row').fadeOut(150, function () { // Faster removal with fadeOut
            $(this).remove();
            validateForm(); // Re-validate after removal
            checkProductRows(); // Check product rows after removal
        });
    });

    // Update product info on product select change
    $('#productsContainer').on('change', '.product-select', function () {
        updateProductInfo(this);
        validateForm(); // Re-validate when product info is updated
    });

    $('form').submit(function (event) {
        if (!validateForm()) { // Prevent submission if validation fails
            event.preventDefault();
        }
    });

    checkProductRows(); // Initial check for product rows
});

function resetNewRow(newRow, uniqueId) {
    newRow.find('.product-select').attr('name', 'product' + uniqueId);
    newRow.find('.price-input').attr('name', 'price' + uniqueId);
    newRow.find('.quantity-input').attr('name', 'quantity' + uniqueId);
    newRow.find('.currency-input').attr('name', 'currency' + uniqueId);
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
