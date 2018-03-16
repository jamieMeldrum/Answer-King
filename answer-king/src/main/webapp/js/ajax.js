function ajax(uri, method, data) {
    ajax(uri, method, data, 'json');
}
function ajax(uri, method, data, dataType) {
    var request = {
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        url: uri,
        type: method,
        contentType: "application/json",
        accepts: "application/json",
        cache: false,
        dataType: dataType,
        data: JSON.stringify(data),
        error: function(xhr) {
            var err = xhr.responseText;
            bootbox.alert(err);
        }
    };
    return $.ajax(request);
}