document.addEventListener('DOMContentLoaded', (event) => {
    document.querySelector('#addProductRow').addEventListener('click', function () {
        // Wait for the product row to be added and then attach event listeners
        setTimeout(function () {
            document.querySelectorAll('.product-select, .quantity-input').forEach(item => {
                item.addEventListener('change', function () {
                    var row = this.parentElement.parentElement;
                    var quantityInput = row.getElementsByClassName('quantity-input')[0];
                    var productSelect = row.getElementsByClassName('product-select')[0];
                    var priceInput = row.getElementsByClassName('price-input')[0];
                    var selectedProductOption = productSelect.options[productSelect.selectedIndex];
                    var pricePerUnit = parseFloat(selectedProductOption.getAttribute('data-price'));
                    var quantity = parseInt(quantityInput.value, 10);
                    var price = pricePerUnit * quantity;
                    priceInput.value = price.toFixed(2);
                });
            });
        }, 0);
    });
});

//to delete product

$(document).ready(function() {
$('.delete-btn').click(function (e) {
    e.preventDefault();
    var productId = $(this).data('id');
    $.ajax({
        url: '/products/' + productId + '/delete',
        type: 'POST',
        success: function () {
            alert('Product deleted');
            location.reload();
        },
        error: function () {
            alert('Failed to delete product');
        }
    });
});
});
document.addEventListener('DOMContentLoaded', (event) => {
    let toastElList = [].slice.call(document.querySelectorAll('.toast'))
    let toastList = toastElList.map(function(toastEl) {
        return new bootstrap.Toast(toastEl, { delay: 3000, autohide: false });
    });
    toastList.forEach(toast => toast.show());
});
