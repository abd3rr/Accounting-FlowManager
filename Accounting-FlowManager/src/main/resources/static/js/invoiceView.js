$(document).ready(function() {
    $('.btn-primary').on('click', function(event) {
        event.preventDefault(); // Prevent the default action

        var invoiceId = $(this).data('id'); // Get the invoice ID from the data attribute

        // Perform an AJAX call to generate the PDF
        $.ajax({
            url: '/invoice/generate/' + invoiceId, // URL for backend processing
            method: 'GET',
            success: function(response) {
                // After successful backend processing, create a temporary link to download the file
                var downloadLink = document.createElement('a');
                downloadLink.href = '/invoice/download/' + invoiceId;
                downloadLink.download = 'Facture#' + invoiceId + '.pdf';
                document.body.appendChild(downloadLink);
                downloadLink.click();
                document.body.removeChild(downloadLink);

                // Optionally, redirect to the view page after the download starts
                window.location.href = '/invoice/view/' + invoiceId;
            },
            error: function(error) {
                // Handle any errors
                console.error('Error processing invoice:', error);
                alert('An error occurred while processing the invoice. Please try again.');
            }
        });
    });
});
