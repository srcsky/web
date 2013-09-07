/**
 Copyright 2013-04-16, Login
 **/
KISSY.add("enterprise/_login", function (S) {
    var $ = S.all,
        login = $("<div class='enterprise_login none'><dl class=\"fields vertical\">\n" +
            "        <dt>邮箱 / 用户名：</dt>\n" +
            "        <dd>" +
            "            <input name=\"username\" type=\"text\" class=\"text username\"/>\n" +
            "            <span class=\"note none\">请输入正确的邮箱或用户名</span>\n" +
            "        </dd>\n" +
            "        <dt>密码：</dt>\n" +
            "        <dd>" +
            "            <input name=\"password\" type=\"password\" class=\"text password\"/>\n" +
            "            <span class=\"note none\">请输入正确的密码</span>\n" +
            "        </dd>\n" +
            "        <dd style='clear: both;'>\n" +
            "            <button type=\"button\" class=\"btn-bl submit\">登录</button>\n" +
            "            &nbsp; <a href=\"javascript:void(0);\" class='register'>免费注册</a>\n" +
            "        </dd>\n" +
            "        <div class=\"wlogin\" style='clear: both;'>\n" +
            "            <p>或者使用合作网站帐号登录：</p>\n" +
            "            <ul class=\"union-login clearfix\" style='width:300px;'>\n" +
            "                <li class=\"qq enterprise_connect_qq\" style='float:left;'><a href=\"javascript:void(0);\"></a></li>\n" +
            "                <li class=\"sina enterprise_connect_sina\"  style='float:left;'><a href=\"javascript:void(0);\"></a></li>\n" +
            "                <li class=\"renren enterprise_connect_renren\"  style='float:left;'><a href=\"javascript:void(0);\"></a></li>\n" +
            "            </ul>\n" +
            "        </div>\n" +
            "    </dl></div>").appendTo("body"),
        submit = login.one("button.submit"),
        register = login.one("a.register"),
        notes = login.all("span.note");

    S.Connect();
    // 提交
    submit.on("click", function () {
        notes.hide();
        new S.IO({url:"/system/ajax!user", type:'post', data:{action:1001,
            username:login.one("input.username").val(),
            password:login.one("input.password").val()}, success:function ($input) {
            if ($input.input.success) {
                S.Window.dialog({id:"enterprise_login"}).close();
                S.Login.success();
            } else {
                if ($input.input.status) {
                    S.Helper.flicker(notes.item($input.input.status - 1));
                } else {
                    S.Window.top_error({content:$input.input.message})
                }
            }
        }});
    })
    // 注册
    register.on("click", function () {
        location.href = "/passport/member!register?login_redirect=" + encodeURIComponent(window.location.href)
        return false;
    });
    // 回车提交
    login.on("keydown", function (event) {
        if (event.keyCode == 13) {
            submit.fire("click");
        }
    })
    return S.Login = function ($option) {
        login.all("input").val("");
        S.Login.success = $option && $option.success ? $option.success : function () {
            location.reload();
        };
        S.Window.dialog_box(S.merge({
            id:"enterprise_login",
            title:"用户登录",
            title:false,
            width:520,
            height:230, drag:true,
            content:login.getDOMNode(),
            close_max:true
        }, $option));
        login.one("input").fire("focus");
    }
}, {requires:["ajax", "enterprise/_window", "enterprise/_connect", "enterprise/_helper"]});