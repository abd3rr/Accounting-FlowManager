$(document).ready(function() {
    var $dropArea = $('.uploadContainer');

    // Highlight drop area when item is dragged over it
    $dropArea.on('dragenter dragover', function(e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).addClass('active-drop-area');
    });

    // Remove highlight when dragging stops
    $dropArea.on('dragleave dragend drop', function(e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).removeClass('active-drop-area');
    });

    // Handle file drop
    $dropArea.on('drop', function(e) {
        var files = e.originalEvent.dataTransfer.files;
        $('#formFile').prop('files', files);  // Assign files to the file input
        $('.file-info').text(files.length + ' file(s) selected');
    });
});

$(document).ready(function() {
    var $form = $('#file-upload-form');

    $form.on('submit', function() {
        // Show loading indicator
        $('.loading-indicator').show();
    });
});
