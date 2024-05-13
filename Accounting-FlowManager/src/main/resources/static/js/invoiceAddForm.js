$(document).ready(function() {
    // Counter to maintain unique IDs
    let rowCounter = 1;

    $('.add-btn').on('click', function() {
        addProductRow();
    });

    function addProductRow() {
        rowCounter++;  // Increment the counter for a new unique ID
        const row = `
        <div class="row mb-3" id="row${rowCounter}">
            <div class="col-sm-4">
                <label class="form-label">Produits</label>
                <select class="form-select" id="product${rowCounter}">
                    <option selected>Choose a product</option>
                    <option value="1">1</option>
                </select>
            </div>
            <div class="col-sm-4">
                <label class="form-label">Prix</label>
                <input type="text" class="form-control" id="price${rowCounter}" value="updated on produits selected" readonly>
            </div>
            <div class="col-sm-3">
                <label class="form-label" for="quantity${rowCounter}">Quantité</label>
                <input type="number" id="quantity${rowCounter}" name="quantity${rowCounter}" min="1" class="form-control">
            </div>
            <div class="col-sm-1 d-flex align-items-end">
                <button type="button" class="btn btn-danger delete-btn" onclick="deleteProductRow(${rowCounter})">
                    Delete
                </button>
            </div>
        </div>
        `;

        $('#invoiceForm').append(row);
    }

    // Function to delete the row
    window.deleteProductRow = function(rowId) {
        $(`#row${rowId}`).remove();
    };
});