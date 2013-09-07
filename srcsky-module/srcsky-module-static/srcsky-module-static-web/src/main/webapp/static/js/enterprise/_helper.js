/**
 Copyright 2013-03-15, Helper 辅助工具类
 **/
KISSY.add("enterprise/_helper", function (S, DOM) {

    var Helper = {
        // 常量
        _const:{
            // 逻辑常量
            YES:1, //是
            NO:2, //否
            // 处理状态
            STATUS_PROCCESS_INIT:1, //未处理
            STATUS_PROCCESS_IN:2, //处理中
            STATUS_PROCCESS_SUCCESS:3, //处理完成
            STATUS_PROCCESS_FAIL:4, //处理失败
            STATIC_LOCAL_URL:S.getPath().replace("js/", "fileuploader"), //"http://static.meilepai.com/static/fileuploader",
            STATIC_REMOTE_URL:"http://img.mlpui.com/static/fileuploader"
        }, emptyObj:{},
        reload:function () {
            window.location.reload();
        },
        redirect:function ($href) {/*重定向*/
            window.location.href = $href;
            return false;
        }
    };
    // 生成文件路径
    Helper._generateDirectory = function (fileid) {
        fileid = Number(fileid);
        return  Math.floor(fileid / (1024 * 1024 * 1024)) + "/" + Math.floor(fileid / (1024 * 1024)) + "/" + Math.floor(fileid / 1024) + "/";
    }
    // 反向生成文件路径
    Helper.fileuploader = function (resource, size) {
        if (typeof(resource) == "object") {
            if (resource.s == Helper._const.STATUS_PROCCESS_SUCCESS) {
                return Helper._const.STATIC_REMOTE_URL + "/img/" + Helper._generateDirectory(resource.id) + resource.id + ((!size || size == "") ? "" : "!" + size);
            } else {
                return Helper._const.STATIC_LOCAL_URL + "/" + Helper._generateDirectory(resource.id) + resource.id + ((!size || size == "") ? "" : "!" + size);
            }
        } else {
            return Helper._const.STATIC_LOCAL_URL + "/" + resource + ((!size || size == "") ? "" : "!" + size);
        }
    }

    // 保留小数位数
    Helper.decimal = function (x) {
        var f_x = parseFloat(x);
        if (isNaN(f_x)) {
            return false;
        }
        f_x = Math.round(f_x * 100, 1) / 100;
        return f_x.toFixed(1);
    }

    // 时间招呼
    Helper.greeting = function () {
        var hour = new Date().getHours();
        if (hour < 6) {
            return ("凌晨好！")
        } else if (hour < 9) {
            return "早上好！"
        } else if (hour < 12) {
            return"上午好！"
        } else if (hour < 14) {
            return"中午好！"
        } else if (hour < 17) {
            return"下午好！"
        } else if (hour < 19) {
            return"傍晚好！"
        } else if (hour < 22) {
            return"晚上好！"
        } else {
            return"夜里好！"
        }
    }

    // 是否开启调试
    Helper.isDebug = function () {
        return (location.toString().indexOf("debug") != -1)
    }

    //==================================================================================================================
    // 用户信息
    Helper.user = {
        info:{id:0, username:"网友"},
        login:false,
        init:function () {
            var uid = S.isEmpty(/web_session_member_key_id=(\d+)/.exec(document.cookie)) ? 0 : /web_session_member_key_id=(\d+)/.exec(document.cookie)[1];
            var uname = S.isEmpty(/web_session_member_key_username=([^;]+)/.exec(document.cookie)) ? "" : /web_session_member_key_username=([^;]+)/.exec(document.cookie)[1];
            this.info.id = S.isEmpty(uid, "0") ? 0 : uid;
            this.info.username = decodeURIComponent(S.isEmpty(uname) ? "" : uname);
            this.login = !S.isEmpty(uid, "0");
        }
    }
    Helper.user.init();

    // 商家信息
    Helper.merchant = {
        info:{id:0},
        login:false,
        init:function () {
            var uid = S.isEmpty(/web_session_merchant_key_id=(\d+)/.exec(document.cookie)) ? 0 : /web_session_merchant_key_id=(\d+)/.exec(document.cookie)[1];
            this.info.id = S.isEmpty(uid, "0") ? 0 : uid;
            this.login = !S.isEmpty(uid, "0");
        }
    }
    Helper.merchant.init();
    //==================================================================================================================

    // 封装 表单字段信息
    Helper.input = function (element) {
        var elements = [];
        S.each(element, function (el) {
            if (/select|textarea|input/i.test(el.nodeName)) {
                elements.push(el);
            } else {
                elements.push.apply(elements, DOM.query("select,textarea,input", el));
            }
        });
        return  S.filter(elements, function (el) {
            return el.name && !el.disabled && (
                el.checked ||
                    /^(?:color|date|datetime|email|hidden|month|number|password|range|search|tel|text|time|url|week)$/i.test(el.type) || /select|textarea/i.test(el.nodeName)
                );
        }, element);
    }
    // 转换为 参数
    Helper.serialize = function (elements) {
        var data = {};
        S.each(elements, function (el) {
            var val = DOM.val(el), vs;
            // 字符串换行平台归一化
            if (S.isArray(val)) {
                val = S.map(val, function (v) {
                    return v.replace(/\r?\n/g, '\r\n');
                });
            } else {
                val = val.replace(/\r?\n/g, '\r\n');
            }
            if (!data.hasOwnProperty(el.name)) {
                data[el.name] = val;
                return
            }
            vs = data[el.name];
            if (!S.isArray(vs)) {
                // 多个元素重名时搞成数组
                vs = data[el.name] = [vs];
            }
            vs.push.apply(vs, S.makeArray(val));
        });
        return data;
    }
    // HTML 元素 闪烁 函数
    Helper.flicker = function (elements, $option) {
        var option = S.merge({count:4, time:0.3}, $option)
        S.each(elements, function (elem) {
            var _self = DOM.get(elem);
            S.use("anim", function (S, Anim) {
                Anim.stop(_self, false, true);
                DOM.css(_self, {opacity:1});
                DOM.show(_self);
                S.later(function () {
                    var config = {duration:option.time, queue:"_flicker_", easing:"easeOutStrong"};
                    for (var i = 0; i < option.count; i++) {
                        Helper.flicker._anim = new Anim(_self, {opacity:i % 2}, config).run();
                    }
                }, 300);
            })
        })
    }
    // 倒计时函数
    function _countdownCountTime(diff, element) {
        var day = parseInt(diff / (60 * 60 * 24), 10),
            hour = parseInt((diff / (60 * 60)) % 24, 10),
            minute = parseInt((diff / 60) % 60, 10),
            second = parseInt(diff % 60, 10),
            html = ["活动剩余时间："];
        if (day != 0) {
            html.push("<span>" + day + "</span>天");
        }
        html.push("<span>" + hour + "</span>小时");
        html.push("<span>" + minute + "</span>分");
        html.push("<span>" + second + "</span>秒");
        DOM.html(element, html.join(""));
        if (diff > 0) {
            diff--;
            S.later(function () {
                _countdownCountTime(diff, element);
            }, 1000);
        }
    }

    Helper.countdown = function (element) {
        if (DOM.hasAttr(element, "diff")) {
            _countdownCountTime(DOM.attr(element, "diff"), element);
        }
    }

    Helper.date = {
        replace:function (str, from, to) {// 替换字符串
            return str.split(from).join(to);
        }, getFullMonth:function (date) { // 返回月份（修正为两位数）
            var v = date.getMonth() + 1;
            if (v > 9)return v.toString();
            return "0" + v;
        }, getFullDate:function (date) { // 返回日 （修正为两位数）
            var v = date.getDate();
            if (v > 9)return v.toString();
            return "0" + v;
        }, getHarfYear:function (date) {   // 返回两位数的年份
            var v = date.getYear();
            if (v > 9)return v.toString();
            return "0" + v;
        }, addDays:function (date, value) {    // 增加天
            date.setDate(date.getDate() + value);
            return date;
        }, addMonths:function (date, value) { // 增加月
            var _date = new Date(date.getTime());
            _date.setMonth(_date.getMonth() + value);
            return _date;
        }, addYears:function (date, value) {
            date.setFullYear(date.getFullYear() + value);
            return date;
        }, format:function (date, str) {   // 格式化日期的表示
            str = this.replace(str, "yyyy", date.getFullYear());
            str = this.replace(str, "MM", this.getFullMonth(date));
            str = this.replace(str, "dd", this.getFullDate(date));
            str = this.replace(str, "yy", this.getHarfYear(date));
            str = this.replace(str, "M", date.getMonth() + 1);
            str = this.replace(str, "d", date.getDate());
            return str;
        }, convert:function (str) {   // 统一日期格式
            str = (str + "").replace(/^\s*/g, "").replace(/\s*$/g, ""); // 去除前后的空白
            var d;
            if (/^[0-9]{8}$/.test(str)) {    // 20040226 -> 2004-02-26
                d = new Date(new Number(str.substr(0, 4)), new Number(str.substr(4, 2)) - 1, new Number(str.substr(6, 2)));
                if (d.getTime())return d;
            }
            d = new Date(str);
            if (d.getTime())return d;
            d = new Date(this.replace(str, "-", "/"));
            if (d.getTime())return d;
            return null;
        }
    }


    return   S.Helper = Helper;

}, {requires:["dom", "event"]});