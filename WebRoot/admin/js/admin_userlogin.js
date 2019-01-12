$(function(){
	initdg();
});
//初始化表格
function initdg(){
	$("#dg").datagrid({
		title:"在线用户信息列表",
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		nowrap:false,
		fit:true,
		toolbar:'#tb',
		loadMsg:"正在加载数据，请稍候……",
		columns:[[    	       
		    {field:'ip',title:'IP地址',width:'120',align:'center'},
		    {field:'username',title:'用户名',width:'90',align:'center'},
		    {field:'userxm',title:'登录人',width:'90',align:'center'},
			{field:'loginmethod',title:'登录方式',width:'120',align:'center',formatter:function(value,row,index){
				if(value=="pc"){
					return '<b>PC端</b>';
				}else{
					return '<b>移动端</b>';
				}
			}},
			{field:'longitude',title:'当前位置:经度',width:'120',align:'center'},
			{field:'latitude',title:'当前位置:纬度',width:'120',align:'center'},
			{field:'logintime',title:'登录时间',width:'180',align:'center'}
		]]
	});
	loadData();
}
//加载数据
function loadData(){
	var param = new Object();
	param.method = "listUserLoginLog";
	$("#dg").datagrid("loading");
	doAjax("../json/DataService!listAll",param,{},function(rsMap,op){
		$("#dg").datagrid("loaded");
		if(rsMap.state){
			var data = rsMap.data;
			cache["data"]=data;
			$("#dg").datagrid("loadData",data);
			$("#loginCount").html('当前在线人数：'+data.length);
		}else{
			top.layer.msg("加载在线用户信息失败！"+rsMap.error);
		}
	});
}
//通用用户名或者用户姓名查询
function searchInfo(value,name){
	var data = cache["data"];
	var count = data.length;
	var newdata = [];
	var rcd = null;
	for(var i=0;i<count;i++){
		rcd = data[i];
		if(rcd.username.indexOf(value)!=-1 || rcd.userxm.indexOf(value)!=-1){
			newdata.push(rcd);
		}
	}
	$("#dg").datagrid("loadData",newdata);
}

var isFirstOpen = false;
//显示地图窗口
function showMapWin(){
	$("#mapWin").window("open");
	//如果是第一次打开地图窗口
	if(!isFirstOpen){
		// 百度地图API功能
		map = new BMap.Map("allmap",{enableMapClick:false});    // 创建Map实例
		map.addControl(new BMap.MapTypeControl());   //添加地图类型控件
		map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
		showPointOnMap();
		var mk = pointMarkers[0];
		map.centerAndZoom(mk.point, 13);  // 初始化地图,设置中心点坐标和地图级别
	}
}
//地图上显示坐标点
var pointMarkers = [];
function showPointOnMap(){
	var data =cache["data"];
	var count = data.length;
	var record = null;
	var marker = null;
	pointMarkers = [];
	var lonlat = null;
	var iconsrc = "../images/computer-on.png";
	for(var i=0;i<count;i++){
		record = data[i];
		lonlat = PointConvert.wgs84tobd09(parseFloat(record.longitude),parseFloat(record.latitude));
		if(record.loginmethod=="pc"){
			iconsrc = "../images/computer-on.png";
		}else{
			iconsrc = "../images/mobile-phone.png";
		}
		marker = addMarker(lonlat.lon,lonlat.lat,record.userxm,iconsrc);
		pointMarkers.push(marker);
	}
}
//在地图上添加标注点
function addMarker(lon,lat,text,iconsrc){
	 var point = new BMap.Point(lon,lat);
	 var myIcon = new BMap.Icon(iconsrc, new BMap.Size(parseInt(32,32),parseInt(32,32)));
	 // 创建标注对象并添加到地图
	 var marker = new BMap.Marker(point,{icon:myIcon});    
	 map.addOverlay(marker);
	 var label = new BMap.Label(text,{offset:new BMap.Size(0,32)});
	 marker.setLabel(label);
	 return marker;
}