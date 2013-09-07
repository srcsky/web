// 联合登陆
KISSY.add("enterprise/_connect", function (S, DOM, Event) {
    var $ = DOM.query,
        _windowInfo = "toolbar=0,status=0,resizable=1,width=900,height=450,left=" + (DOM.width(window) - 900) / 2 + ",top=" + (DOM.height(window) - 450) / 2,
        _windowLocation = "/system/connect?type={type}";
    return S.Connect = function () {
        S.each($(".enterprise_connect_qq,.enterprise_connect_sina,.enterprise_connect_renren"), function (elem) {
            Event.on(elem, "click", function () {
                if (DOM.hasClass(this, "enterprise_connect_qq")) {
                    window.open(S.substitute(_windowLocation, {type:5}), "mb", _windowInfo);
                } else if (DOM.hasClass(this, "enterprise_connect_sina")) {
                    window.open(S.substitute(_windowLocation, {type:6}), "mb", _windowInfo);
                } else if (DOM.hasClass(this, "enterprise_connect_renren")) {
                    window.open(S.substitute(_windowLocation, {type:8}), "mb", _windowInfo);
                }
            });
        });
    };
}, {requires:["dom", "event"]});