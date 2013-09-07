//========================================== Enterprise Map             ================================================
//========================================== 基础,地图辅助方法          ================================================
KISSY.add("enterprise/_map_baidu", function (S, Node, Sizzle, XTemplate, Helper) {

    var $ = S.all, _ready = false, icon = null;

    //百度地图加载
    function ready(callback) {
        if (_ready) {
            S.bind(callback, S.Map)();
        } else {
            window.enterprise_map_initialize = function () {
                _ready = true;
                icon = new BMap.Icon("http://static.mlpui.com/static/img/_lib/map/marker5.png", new BMap.Size(40, 34));
                BMap.Convertor = {
                    translate:function (point, type, callback) {
                        var callbackName = 'cbk_' + Math.round(Math.random() * 10000);    //随机函数名
                        var xyUrl = "http://api.map.baidu.com/ag/coord/convert?from=" + type + "&to=4&x=" + point.lng + "&y=" + point.lat + "&callback=BMap.Convertor." + callbackName;
                        //动态创建script标签
                        load_script(xyUrl);
                        BMap.Convertor[callbackName] = function (xyResult) {
                            delete BMap.Convertor[callbackName];    //调用完需要删除改函数
                            var point = new BMap.Point(xyResult.x, xyResult.y);
                            callback && callback(point);
                        }
                    }
                }
                S.bind(callback, S.Map)();
            }
            var script = document.createElement("script");
            script.src = "http://api.map.baidu.com/api?v=1.3&callback=enterprise_map_initialize";
            document.body.appendChild(script);
        }
    }

    function render($option) {

        var options = S.merge({
            id:"#map_canvas", zoom:15, position:[121.463538, 31.24686],
            scrollwheel:true, navigation:new BMap.NavigationControl({type:BMAP_NAVIGATION_CONTROL_ZOOM}),
            title:"上海美乐拍总部", marker:true, click:S.noop, markerClick:S.noop, markerDrag:S.noop,
            searcherEvent:S.noop, zoomChanged:S.noop, markerDraggable:false, markers:[
                {icon:icon}
            ]
        }, $option);

        options.markers[0].point = new BMap.Point(options.position[0], options.position[1]);
        options.markers[0].title = options.title;

        var map = new BMap.Map($(options.id).getDOMNode(), options);
        map.centerAndZoom(options.markers[0].point, options.zoom);
        map.addControl(options.navigation);

        if (options.scrollwheel) {
            // 启用滚轮放大缩小。
            map.enableScrollWheelZoom();
        }
        map.enableKeyboard();
        map.enableAutoResize();
        map.enableInertialDragging();
        map.addEventListener("click", S.bind(options.click, map));
        map.addEventListener("zoomend", S.bind(options.zoomChanged));
        map.markers = [];
        if (options.marker) {
            S.each(options.markers, function (marker) {
                var marker = new BMap.Marker(marker.point, {icon:marker.icon, title:marker.title});
                marker.setAnimation(BMAP_ANIMATION_DROP);
                if (options.markerDraggable) {
                    marker.enableDragging();
                }
                marker.addEventListener("click", S.bind(options.markerClick, marker));
                marker.addEventListener("dragging", S.bind(options.markerDrag, marker));
                map.addOverlay(marker);
                map.markers.push(marker);
            })
        }
        return map;
    }

    function searcher($option) {
        var options = S.merge({
            address:"一天下大厦",
            markerDraggable:true,
            markerClick:S.noop,
            markerDrag:S.noop,
            searcherEvent:S.noop,
            city:"上海",
            callback:function (point) {
                if (null != point) {
                    //移動地圖中心點
                    options.map.setCenter(point);
                    S.each(options.map.markers, function (marker) {
                        options.map.removeOverlay(marker);
                    });
                    options.map.markers = [];
                    var marker = new BMap.Marker(point, {icon:icon, title:options.title});
                    marker.setAnimation(BMAP_ANIMATION_DROP)
                    if (options.markerDraggable) {
                        marker.enableDragging();
                    }
                    marker.addEventListener("click", S.bind(options.markerClick, marker));
                    marker.addEventListener("dragging", S.bind(options.markerDrag, marker));
                    options.map.addOverlay(marker);
                    options.map.markers.push(marker);
                    options.searcherEvent(point)
                } else {
                    alert("这个地址 百度 map 无资料！");
                }
            }}, $option);
        new BMap.Geocoder().getPoint(options.address, options.callback, options.city);
    }

    // 构造具有 查询路线 的 InfoWindow
    var html_container_info = '  <div class="ks_map_tmpl_container_info">';
    html_container_info += '         <h5 style="font-size:14px;padding-bottom: 5px;">{{title}}</h5>';
    html_container_info += '         <p class="cgray"><!--地址：-->{{address}}</p>';
    /*phone.length >= 8*/
    html_container_info += '        {{#if phone!==""}}<p><!--电话：--> {{_phone phone}}</p>{{/if}}';
    html_container_info += '        {{#if shop!==1024}}<p><!--店铺：--><a href="http://shop${shop}.meilepai.com/" target="_blank" class="underl">进入店铺&raquo;</a></p>{{/if}}<hr/>';
    html_container_info += '         <p class="action"><a method="from" href="javascript:void(0)" class="on">从这里出发</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a method="to" href="javascript:void(0)">到这里去</a></p>';
    html_container_info += '         <p class="exec" style="padding-top: 10px;"><input type="text" class="text"/>&nbsp;&nbsp;<a method="bt" href="javascript:void(0)">公交</a>&nbsp;&nbsp;<a method="dt" href="javascript:void(0)">驾车</a></p>';
    html_container_info += '     </div>';


    function getInfoWindow($options) {
        var options = S.merge({
            city:"上海",
            width:"auto",
            shop:1024,
            title:"上海美乐拍总部",
            phone:"400-650-9697",
            address:"闸北区一天下大厦10B02",
            complete:S.noop,
            panel:"map_bar_line"
        }, $options);
        var infoWindow = new BMap.InfoWindow(new XTemplate(html_container_info, {commands:{
            _phone:function (scopes, option) {
                if (option.params[0].length >= 8) {
                    return option.params[0];
                } else {
                    return "400-650-9697 转" + option.params[0]
                }
            }
        }}).render(options), {width:300, offset:{width:-9, height:-15}});
        infoWindow.addEventListener("open", function (event) {

            var map = event.target.map;

            var _this = $(".ks_map_tmpl_container_info");
            var _input = _this.one("input");
            var actions = _this.all("p.action a").on("click", function () {
                actions.removeClass("on");
                $(this).addClass("on");
            });
            var execs = _this.one("p.exec a").on("click", function () {
                var _a = $(this);

                if (S.isEmpty(_input.val())) {
                    S.Window.upfail({content:"请输入地址", target:this, callback:function () {
                        _input.trigger("focus");
                    }});
                } else {
                    S.each(event.target.map.getOverlays(), function (overlay) {
                        if (overlay instanceof BMap.Polyline) {
                            map.removeOverlay(overlay);
                        } else if (overlay instanceof BMap.Marker && map.markers.indexOf(overlay) == -1) {
                            map.removeOverlay(overlay);
                        }
                    });
                    new BMap.Geocoder().getPoint(_input.val().trim(), function (point) {
                        if (null != point) {
                            var route = new BMap.TransitRoute(event.target.map, {
                                renderOptions:{map:event.target.map, panel:options.panel, autoViewport:true},
                                pageCapacity:3
                            });
                            if (_a.attr("method") == "dt") {
                                route = new BMap.DrivingRoute(event.target.map, {renderOptions:{map:event.target.map, panel:options.panel, autoViewport:true}});
                            }
                            route.setSearchCompleteCallback(S.bind(options.complete, event.target.map));
                            if (actions.filter("a.on").attr("method") == "from") {
                                route.search(/*_input.val().trim()*/point, event.point);
                            } else if (actions.filter("a.on").attr("method") == "to") {
                                route.search(event.point, /*_input.val().trim()*/point);
                            }
                        } else {
                            S.bind(options.complete, event.target.map)({getNumPlans:function () {
                                return 0;
                            }});
                        }
                    }, options.city);
                }
            });
            var scroll = $(S.UA.shell == "ie" || S.UA.shell == "firefox" ? "html" : "body");
            if (scroll.scrollTop() > _this.offset().top) {
                scroll.scrollTop(_this.offset().top - 20);
            }
        });
        return infoWindow;
    }


    // 浮动曾地图 Window
    var html_container = '<div class="ks_map_m_traffic" style="width:900px;{{#if trafficnotice!==""}}height:80px{{/if}}">';
    html_container += '         <h2 class="fsbig">{{title}}</h2>';
    html_container += '         {{#if trafficnotice!==""}}<h3><strong>乘车路线：</strong>{{trafficnotice}}</h3>{{/if}}';
    html_container += '    </div>';
    html_container += '    <div id="map_bar_view" style="width: 940px;height:500px;float: left;"></div>';
    html_container += '    <div id="map_bar_line" style="width: 400px;height:500px;float: left;display: none; overflow-x:hidden;overflow-y:auto;"></div>';
    //html_container += '    <p class="ks_map_declaration">注：地图位置坐标仅供参考，具体情况以实际道路标识信息为准</p>';
    function _window($options) {
        var options = S.merge({
            width:"auto",
            city:"上海",
            shop:1024,
            title:"上海美乐拍总部",
            phone:"400-650-9697",
            address:"闸北区一天下大厦10B02",
            position:[116.404, 39.915, 15],
            trafficnotice:""
        }, $options);
        S.Window.dialog_box({content:new XTemplate(html_container).render(options), width:options.width, init:function () {
            this.DOM.close.addClass("aui_close_max");
            this.DOM.content.css({padding:"0"});
            //构造 InfoWindow
            var infoWindow = getInfoWindow({
                city:options.city,
                shop:options.shop,
                title:options.title, width:options.width, phone:options.phone, address:options.address, complete:function (result) {
                    var map = this;
                    if (result.getNumPlans() == 0) {
                        setTimeout(function () {
                            map.panTo(map.markers[0].getPosition());
                        }, 300);
                        $("#map_bar_view").css({width:940});
                        $("#map_bar_line").hide();
                        _window.upfail({content:"Sorry！未能找到相关路线。"});
                    } else {
                        $("#map_bar_view").css({width:540});
                        $("#map_bar_line").show();
                        map.markers[0].setTop(true);
                        setTimeout(function () {
                            map.setViewport([result.getStart().point, result.getEnd().point]);
                        }, 300);
                    }
                }
            })
            var map = render({
                id:"#map_bar_view", navigation:new BMap.NavigationControl({type:BMAP_NAVIGATION_CONTROL_LARGE}),
                title:options.title, position:options.position, zoom:parseInt(options.position[2]), markerClick:function (event) {
                    map.markers[0].openInfoWindow(infoWindow);
                }
            });
            map.markers[0].openInfoWindow(infoWindow);
        }, close:function () {
            this.DOM.close.removeClass("aui_close_max");
        }});
    }

    function register($options) {
        var options = S.merge({
            id:"#enterprise_ui_map",
            position:[116.404, 39.915, 15],
            shop:1024,
            title:"上海美乐拍总部",
            address:"闸北一天下大厦",
            phone:"400-650-9697"
        }, $options)
        var $map = $(options.id);
        $map.one(".enterprise_ui_map_bar").on("click", function () {
            _window({title:options.title, position:options.position, address:options.address, phone:options.phone, trafficnotice:options.trafficnotice});
        });
        return render({
            id:$map.one(".enterprise_ui_map_body"), title:options.title,
            position:options.position, zoom:parseInt(options.position[2]), scrollwheel:false
        });
    }


    function load_script(xyUrl, callback) {
        var head = document.getElementsByTagName('head')[0];
        var script = document.createElement('script');
        script.type = 'text/javascript';
        script.src = xyUrl;
        //借鉴了jQuery的script跨域方法
        script.onload = script.onreadystatechange = function () {
            if ((!this.readyState || this.readyState === "loaded" || this.readyState === "complete")) {
                callback && callback();
                // Handle memory leak in IE
                script.onload = script.onreadystatechange = null;
                if (head && script.parentNode) {
                    head.removeChild(script);
                }
            }
        };
        // Use insertBefore instead of appendChild  to circumvent an IE6 bug.
        head.insertBefore(script, head.firstChild);
    }

    return S.Map = {
        ready:ready,
        render:render,
        searcher:searcher,
        getInfoWindow:getInfoWindow,
        window:_window,
        register:register,
        load_script:load_script
    }

}, {requires:"node,sizzle,xtemplate,enterprise/_helper,enterprise/_window".split(",")});
