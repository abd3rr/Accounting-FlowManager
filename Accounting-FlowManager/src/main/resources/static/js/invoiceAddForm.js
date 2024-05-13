$(document).ready(function() {
    let uniqueId = 1; // Start with an initial ID

    $('#addProductRow').click(function() {
        let newRow = $('.product-row-template').first().clone().show();
        newRow.find('.product-select').attr('name', 'product' + uniqueId);
        newRow.find('.price-input').attr('name', 'price' + uniqueId);
        newRow.find('.quantity-input').attr('name', 'quantity' + uniqueId);
        newRow.attr('id', 'productRow' + uniqueId);

        // Append new row with fadeIn effect
        newRow.hide().appendTo('#productsContainer').fadeIn(400);

        uniqueId++; // Increment the ID for the next row
    });

    // Event delegation to handle dynamically added elements
    $('#productsContainer').on('click', '.removeProductRow', function() {
        // Fade out the row and remove it from DOM after animation completes
        $(this).closest('.row').fadeOut(400, function() {
            $(this).remove();
        });
    });
});
