//========================================== Enterprise Validation      ================================================
//========================================== 基础,表单验证方法          =============================================
KISSY.add("enterprise/_valid", function (S) {
    var $ = S.all;
    // 验证函数
    function check($element) {
        var message = "";
        $($element).each(function () {
            var element = $(this).css("background-color", "#FFF");
            if (!message) {
                var label = (!S.isEmpty(element.attr("label")) ? element.attr("label") : element.prev().text());
                if (S.trim(element.val()) == "") {
                    if (S.trim(element.attr("requiredMsg")) != "") {
                        message = element.attr("requiredMsg");
                    } else {
                        if (element.getDOMNode().tagName.toUpperCase() == "SELECT") {
                            message = label + "必须选择！";
                        } else {
                            message = label + "必须填写！";
                        }
                    }
                } else if (element.attr("regex") != "" && !new RegExp(element.attr("regex")).test(element.val())) {
                    message = element.attr("regexMsg");
                    element.trigger("focus");
                } else if (element.hasClass("int") && !/^\-?\d+?$/.test(element.val())) {
                    if (element.attr("intMsg") && element.attr("intMsg") != "") {
                        message = element.attr("intMsg");
                    } else {
                        message = label + "格式错误,必须为数字！"
                    }
                } else if (element.hasClass("letter") && !/^[a-zA-z\s\-]+?$/.test(element.val())) {
                    if (element.attr("letterMsg") && element.attr("letterMsg") != "") {
                        message = element.attr("letterMsg");
                    } else {
                        message = label + "格式错误,必须为字母！";
                    }
                } else if (element.hasClass("number") && !/^\-?\d+(\.\d{0,2})?$/.test(element.val())) {
                    if (element.attr("numberMsg") && element.attr("numberMsg") != "") {
                        message = element.attr("numberMsg");
                    } else {
                        message = label + "格式错误,且小数只能保留两位！"
                    }
                }
                if (message) {
                    element.fire("focus");
                    element.css("background", "pink");
                }
            }
        });
        return message;
    }

    return S.Valid = {check:check};
}, {requires:["node"]});