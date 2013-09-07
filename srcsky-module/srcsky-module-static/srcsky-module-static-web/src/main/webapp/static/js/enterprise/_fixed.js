//========================================== Enterprise 定位    ========================================================
//========================================== Fixed              ========================================================
KISSY.add("enterprise/_fixed", function (S, DOM, Event) {

    // 滚动事件
    function scroll($element, $call1, $call2, $call3) {
        var iScrollTop = DOM.scrollTop(window);
        var sClass = "enterprise_fixed";
        if (!DOM.data($element, sClass)) {
            DOM.data($element, sClass, {css:{position:DOM.css($element, "position"), top:DOM.css($element, "top")}, offset:DOM.offset($element)});
        }
        var oOrig = DOM.data($element, sClass);
        var bIsSticky = DOM.hasClass($element, sClass);
        if (iScrollTop > oOrig.offset.top && !bIsSticky) {
            DOM.css($element, {position:'fixed', top:0});
            DOM.addClass($element, sClass);
            if ($call1)$call1();
        } else if (iScrollTop < oOrig.offset.top && bIsSticky) {
            DOM.css($element, oOrig.css);
            DOM.removeClass($element, sClass);
            if ($call2)$call2();
        }
        if ($call3)S.bind($call3, $element)(iScrollTop, oOrig.offset.top, bIsSticky);
    }

    // $element 定位元素
    // $call1 达到浮动位置 时触发的事件
    // $call2 还原时触发的事件
    // $call3 包含以上两个事件
    return S.Fixed = function ($element, $call1, $call2, $call3) {
        var _arguments = arguments;
        if (S.UA.shell != "ie" || S.UA.ie > 6) {
            Event.on(window, "scroll", S.buffer(function (event) {
                scroll.apply(null, _arguments);
            }, 10))
        }
    }
}, {requires:["dom", "event"]});