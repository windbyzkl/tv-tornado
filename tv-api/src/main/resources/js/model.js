var name = 'Tornado';
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});
function init() {
    console.log(name);
}

function getJSON() {
    $.ajax({
        url: '/hello',
        type: 'get',
        dataType: 'json',
        success: function () {
            console.log('this is ajax func');
        },
        error: function (error) {
            console.log('error!!!' + error);
        }
    });
}

function update() {
    var data = $("#form").serialize();
    console.log(data);
    $.ajax({
        url: '/getTitle',
        data:data,
        type: 'post',
        dataType: 'json',
        success: function () {
            alert('this is ajax func');
        },
        error: function (error) {
            console.log('error!!!' + error);
        }
    });
}

