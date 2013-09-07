/**
 Copyright 2013-03-15, Editor 编辑器
 **/
KISSY.add("enterprise/_editor", function (S, Editor, Source, Separator, Bold, Italic, Underline, FontFamily, FontSize, ForeColor, Link, Maximize) {
        function init($option) {
            var option = S.merge({
                focused:true,
                attachForm:true,
                width:'600',
                height:"300",
                srcNode:'#editorEl',
                plugins:[Source, Separator, Bold, Italic, Underline, Separator, ForeColor, FontSize, Link, Maximize]
            }, $option);
            return new Editor(option).render();
        }

        return   S._Editor = {init:init}
    }, {
        requires:[
            "editor",
            "editor/plugin/source-area/",
            "editor/plugin/separator/",
            "editor/plugin/bold/",
            "editor/plugin/italic/",
            "editor/plugin/underline/",
            "editor/plugin/font-family/",
            "editor/plugin/font-size/",
            "editor/plugin/fore-color/",
            "editor/plugin/link/",
            "editor/plugin/maximize/",
            "enterprise/thirdparty/kissyui/1.3.0/editor/theme/cool/editor.css"
        ]
    }
);