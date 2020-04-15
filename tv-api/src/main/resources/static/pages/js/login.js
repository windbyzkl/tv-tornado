var Login = function () {

    function serializeToObject(jsonArray) {
        var o = {};
        $.each(jsonArray, function () {
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    }

    // 构建登录对象
    var handleLogin = function () {

        // jquery-form-validate 前端的验证框架
        $('.login-form').validate({
            errorElement: 'span', //default input error message container
            errorClass: 'help-block', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
                username: {
                    required: true
                },
                password: {
                    required: true
                }
            },

            messages: {
                username: {
                    required: "用户名不能为空."
                },
                password: {
                    required: "密码不能为空."
                }
            },

            highlight: function (element) { // hightlight error inputs
                $(element).closest('.form-group').addClass('has-error'); // set error class to the control group
            },

            success: function (label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },

            errorPlacement: function (error, element) {
                error.insertAfter(element.closest('#input-error'));
            },

            submitHandler: function (form) {

                var loginForm = $('.login-form');
                var jsonParam = serializeToObject(loginForm.serializeArray());
                //console.log(serializeToObject(loginForm.serializeArray()));
                console.log(JSON.stringify(jsonParam));

                $.ajax({
                    dataType: "json",
                    type: "post", // 提交方式 get/post
                    contentType: "application/json",
                    url: '/login', // 需要提交的 url
                    data: JSON.stringify(jsonParam),
                    success: function (data) {
                        window.location.href = "/manager/center.html";
                    }
                })
                /*
                loginForm.ajaxSubmit({
                    dataType: "json",
                    type: "post", // 提交方式 get/post
                    contentType: "application/json",
                    url: '/login', // 需要提交的 url
                    data: JSON.stringify(jsonParam),
                    success: function (data) {
                        // 登录成功或者失败的提示信息
                        alert(data);
                        if (data.status == 200 && data.msg == "OK") {
                            window.location.href = hdnContextPath + "/center.action";
                        } else {
//                        	SweetAlert.error(data.msg);
                            alert(data.msg);
                        }
                    }
                });
*/
            }
        });

    };

    return {
        init: function () {
            handleLogin();
        }

    };

}();

jQuery(document).ready(function () {
    Login.init();
});