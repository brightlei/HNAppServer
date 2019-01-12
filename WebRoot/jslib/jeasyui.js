__CreateJSPath = function (js) {
    var scripts = document.getElementsByTagName("script");
    var path = "";
    for (var i = 0, l = scripts.length; i < l; i++) {
        var src = scripts[i].src;
        if (src.indexOf(js) != -1) {
            var ss = src.split(js);
            path = ss[0];
            break;
        }
    }
    var href = location.href;
    href = href.split("#")[0];
    href = href.split("?")[0];
    var ss = href.split("/");
    ss.length = ss.length - 1;
    href = ss.join("/");
    if (path.indexOf("https:") == -1 && path.indexOf("http:") == -1 && path.indexOf("file:") == -1 && path.indexOf("\/") != 0) {
        path = href + "/" + path;
    }
    return path;
}

var bootPATH = __CreateJSPath("jeasyui.js");
var basePath = bootPATH.substring(0,bootPATH.indexOf("/fhy")+3);
//定义全局缓存对象
var cache = {};

//easyui
document.write('<link href="' + bootPATH + 'themes/default/easyui.css" rel="stylesheet" type="text/css" />');
document.write('<link href="' + bootPATH + 'themes/color.css?t=1" rel="stylesheet" type="text/css" />');
document.write('<link href="' + bootPATH + 'themes/icon.css" rel="stylesheet" type="text/css" />');
document.write('<link href="' + bootPATH + 'themes/icons/icon-all.css" rel="stylesheet" type="text/css" />');
document.write('<script src="' + bootPATH + 'jquery.min.js" type="text/javascript"></script>');
document.write('<script src="' + bootPATH + 'jquery.easyui.min.js" type="text/javascript" ></script>');
document.write('<script src="' + bootPATH + 'easyui-lang-zh_CN.js" type="text/javascript" ></script>');
//document.write('<script src="' + bootPATH + 'server.js" type="text/javascript" ></script>');
document.write('<script src="' + bootPATH + 'web-fun.js" type="text/javascript" ></script>');
document.write('<script src="' + bootPATH + 'dicdata.js?t='+Math.random()+'" type="text/javascript" ></script>');
document.write('<script src="' + bootPATH + 'jquery.datagrid.js?t=1" type="text/javascript" ></script>');
user = {
	xm:"管理员",
	logintime:"2018-12-09 12:12:21",
	menupages:""
};