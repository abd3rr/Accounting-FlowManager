$(document).ready(function() {
    $('.btn-primary').on('click', function(event) {
        event.preventDefault(); // Prevent the default action

        var invoiceId = $(this).data('id'); // Get the invoice ID from the data attribute

        // Perform an AJAX call to generate the PDF
        $.ajax({
            url: '/invoice/generate/' + invoiceId, // URL for generating the PDF
            method: 'GET',
            success: function(response) {
                // After successful generation, initiate the download
                var downloadLink = document.createElement('a');
                downloadLink.href = '/invoice/download/' + invoiceId;
                downloadLink.target = '_blank'; // Open the download in a new tab/window
                document.body.appendChild(downloadLink);
                downloadLink.click();
                document.body.removeChild(downloadLink);

                // Redirect to the view page after the download starts
                window.location.href = '/invoice/view/' + invoiceId;
            },
            error: function(error) {
                // Handle any errors
                console.error('Error generating invoice:', error);
                alert('An error occurred while generating the invoice. Please try again.');
            }
        });
    });
});