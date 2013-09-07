//========================================== Enterprise PEditor    =====================================================
//==========================================图片编辑               =====================================================
KISSY.add("enterprise/_peditor", function (S) {
    var version = 0,
        peditor_settings = {};


    window.peditor_settings = peditor_settings;

    // 获取 Flash 版本
    function getVersion() {
        if (navigator.plugins != null && navigator.plugins.length > 0 && navigator.plugins["Shockwave Flash"]) {
            return navigator.plugins["Shockwave Flash"].description.split(" ")[2].split(".")[0];
        } else {
            try {
                var axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
                return axo.GetVariable("$version").split(" ")[1].split(",")[0];
            } catch (e) {
            }
        }
    }

    version = getVersion();
    // 生成 Flash Object Code
    function Generateobj(objAttrs, params, embedAttrs) {
        var isIE = (navigator.appVersion.indexOf("MSIE") != -1) ? true : false;
        var isWin = (navigator.appVersion.toLowerCase().indexOf("win") != -1) ? true : false;
        var isOpera = (navigator.userAgent.indexOf("Opera") != -1) ? true : false;
        var str = '';
        if (isIE && isWin && !isOpera) {
            str += '<object ';
            for (var i in objAttrs) {
                str += i + '="' + objAttrs[i] + '" ';
            }
            str += '>';
            for (var i in params) {
                str += '<param name="' + i + '" value="' + params[i] + '"/>';
            }
            str += '</object>';
        } else {
            str += '<embed ';
            for (var i in embedAttrs) {
                str += i + '="' + embedAttrs[i] + '" ';
            }
            str += ' />';
        }
        return str;
    }

    // 解析 Flash 参数属性
    function GetArgs(args, srcParamName, classid, mimeType) {
        var ret = new Object();
        ret.embedAttrs = new Object();
        ret.params = new Object();
        ret.objAttrs = new Object();
        for (var i = 0; i < args.length; i = i + 2) {
            var currArg = args[i].toLowerCase();
            switch (currArg) {
                case "src":
                case "movie":
                    ret.embedAttrs["src"] = args[i + 1];
                    ret.params[srcParamName] = args[i + 1];
                    break;
                case "id":
                    ret.objAttrs[args[i]] = args[i + 1];
                    ret.embedAttrs[args[i]] = ret.objAttrs[args[i]] = args[i + 1];
                    break;
                case "width":
                case "height":
                case "name":
                case "tabindex":
                    ret.embedAttrs[args[i]] = ret.objAttrs[args[i]] = args[i + 1];
                    break;
                default:
                    ret.embedAttrs[args[i]] = ret.params[args[i]] = args[i + 1];
            }
        }
        ret.objAttrs["classid"] = classid;
        if (mimeType)ret.embedAttrs["type"] = mimeType;
        return ret;
    }

    // 生成  编辑器 浮动层
    var bo = {
        ie:window.ActiveXObject,
        ie6:window.ActiveXObject && (document.implementation !== null) && (document.implementation.hasFeature !== null) && (window.XMLHttpRequest === null),
        quirks:document.compatMode === 'BackCompat'
    }, element = {};

    function windowSize() {
        var w = 0,
            h = 0;
        if (document.documentElement.clientWidth !== 0) {
            w = document.documentElement.clientWidth;
            h = document.documentElement.clientHeight;
        } else {
            w = document.body.clientWidth;
            h = document.body.clientHeight;
        }
        return {
            width:w,
            height:h
        };
    }

    // 定位
    function setPosition(event, append) {
        element.div.style.background = '#696969';
        element.div.style.opacity = 0.8;
        element.div.style.filter = 'alpha(opacity=80)';

        if ((bo.ie && bo.quirks) || bo.ie6) {
            var size = windowSize();
            element.div.style.position = 'absolute';
            element.div.style.width = size.width + 'px';
            element.div.style.height = size.height + 'px';
            element.div.style.setExpression('top', "(t=document.documentElement.scrollTop||document.body.scrollTop)+'px'");
            element.div.style.setExpression('left', "(l=document.documentElement.scrollLeft||document.body.scrollLeft)+'px'");
        } else {
            element.div.style.width = '100%';
            element.div.style.height = '100%';
            element.div.style.top = '0';
            element.div.style.left = '0';
            element.div.style.position = 'fixed';
        }
        element.div.style.zIndex = 99998;

        element.idiv.style.border = '1px solid #2c2c2c';
        if ((bo.ie && bo.quirks) || bo.ie6) {
            element.idiv.style.position = 'absolute';
            element.idiv.style.setExpression('top', "25+((t=document.documentElement.scrollTop||document.body.scrollTop))+'px'");
            element.idiv.style.setExpression('left', "35+((l=document.documentElement.scrollLeft||document.body.scrollLeft))+'px'");
        } else {
            element.idiv.style.position = 'fixed';
            element.idiv.style.top = '25px';
            element.idiv.style.left = '35px';
        }
        element.idiv.style.zIndex = 99999;
        if (append) {
            document.body.appendChild(element.div);
            document.body.appendChild(element.idiv);
        }
        element.iframe.style.width = (element.div.offsetWidth - 70) + 'px';
        element.iframe.style.height = (element.div.offsetHeight - 50) + 'px';
        element.iframe.style.border = '1px solid #b1b1b1';
        element.iframe.style.backgroundColor = '#606060';
        element.iframe.style.display = 'block';
        element.iframe.frameBorder = 0;
    }

    function extend(object, extender) {
        for (var attr in extender) {
            if (extender.hasOwnProperty(attr)) {
                object[attr] = extender[attr] || object[attr];
            }
        }
        return object;
    }

    function buildUrl(opt) {
        var url = S.getPath("/enterprise/thirdparty/photo/Express.swf?_=" + new Date().getTime()), attr;
        for (attr in opt) {
            if (opt.hasOwnProperty(attr) && (attr != "close" && attr != "complete")) {
                url += "&" + attr + "=" + escape(opt[attr]);
            }
        }
        return url;
    }

    function flash_html($option) {
        var ret = GetArgs(['width', '100%', 'height', '100%',
            'src', buildUrl($option),
            'movie', buildUrl($option),
            'quality', 'high', 'wmode', 'window', 'devicefont', 'false', 'id', 'peditor', 'bgcolor', '#606060', 'name', 'editor', 'menu',
            'false', 'allowFullScreen', 'true', 'allowScriptAccess', 'always', 'allowFullScreenInteractive', 'true'],
            "movie", "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000", "application/x-shockwave-flash");
        return Generateobj(ret.objAttrs, ret.params, ret.embedAttrs);
    }

    return S.Peditor = {
        init:function ($option) {
            if (version < 10) {
                alert("Flash Player 版本低于10，将无法使用截图功能！");
                return;
            }
            var option = extend({
                target:"http://www.haoyuduo.com/system/fileuploader",
                close:S.Peditor.destroy,
                complete:function () {
                    S.Peditor.destroy();
                }, resource_path:S.getPath("/enterprise/thirdparty/photo"),
                close_function:"window.peditor_close",
                complete_function:"window.peditor_complete",
                quality:100,
                crop_size:"80x80"
            }, $option || {});
            window["peditor_close"] = option.close;
            window["peditor_complete"] = option.complete;
            element.iframe = document.createElement('div');
            element.div = document.createElement('div');
            element.idiv = document.createElement('div');
            setPosition(false, true);
            element.iframe.innerHTML = flash_html(option);
            element.idiv.appendChild(element.iframe);
            S.Event.on(window, "resize", setPosition);
        }, destroy:function () {
            S.DOM.remove("#peditor");
            element.idiv.removeChild(element.iframe);
            document.body.removeChild(element.div);
            document.body.removeChild(element.idiv);
            S.Event.on(window, "resize", setPosition);
            /*setTimeout(function () {
             $._body.find("a:first").trigger("focus");
             }, 300)*/
        }
    };
}, {requires:["event"]});