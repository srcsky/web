KISSY.add("enterprise/_xiuxiu", function (S) {
    var $ = S.all;

    function loadScript(option, callback) {
        if (undefined == window.xiuxiu) {
            $("body").append(S.substitute("<div id='{id}_container' style='display:none;background:{background};width:{width}px;height:{height}px'><div id='{id}'></div></div>", option));
            S.getScript("http://open.web.meitu.com/sources/xiuxiu.js", {success:function () {
                xiuxiu.single = true;
                xiuxiu._apiTrack = S.noop;
                xiuxiu.params.bgcolor = option.background;
                xiuxiu.embedSWF(option.id, 3, "100%", "100%");
                xiuxiu.setUploadURL(option.url, option.id);
                xiuxiu.setUploadType(2, option.id);
                xiuxiu.setUploadDataFieldName("fileuploader", option.id);
                callback();
            }});
        } else {
            $("#" + option.id + "_container").append(S.substitute("<div id='{id}'></div>", option));
            xiuxiu.params.bgcolor = option.background;
            xiuxiu.embedSWF(option.id, 3, "100%", "100%");
            xiuxiu.setUploadURL(option.url, option.id);
            xiuxiu.setUploadType(2, option.id);
            xiuxiu.setUploadDataFieldName("fileuploader", option.id);
            callback();
        }
    }

    return S.Xiuxiu = {
        init:function ($option) {
            var option = S.merge({
                id:"enterprise_xiuxiu_container", width:900, height:500, background:"#eceeef",
                image:"http://open.web.meitu.com/sources/images/1.jpg",
                url:S.substitute("http://{host}/system/fileuploader", {host:location.hostname}), background:"#eceeef", success:S.noop
            }, $option);
            loadScript(option, function () {
                S.Xiuxiu.dialog = S.Window.dialog_box({
                    close:function () {
                        swfobject.removeSWF(xiuxiu.defaultID);
                    },content:document.getElementById(option.id + "_container"), width:option.width, height:option.height, padding:"0", cancel:true, cancelVal:"关闭", resize:false, init:function () {
                        $(this.DOM.content[0]).css({width:option.width, height:option.height, background:option.background});
                        xiuxiu.onInit = function () {
                            S.later(function () {
                                xiuxiu.loadPhoto(option.image);
                            }, 300);
                        }
                        xiuxiu.onUploadResponse = function (data) {
                            var input = eval("(" + data + ")").input;
                            S.bind(option.success, {close:function () {
                                S.Xiuxiu.dialog.close();
                            }})(input);
                        }
                    }
                });
            })
        }
    };

}, {requires:["node", "enterprise/_window"]})