$(function(){
	initDateTime(1);
});

var today = dateUtil.nowDate2String("yyyy-MM-dd HH:mm:ss");
var starttime = today;
var endtime = today;

function initDateTime(type){
	$("#custom_con").css("display","none");
	starttime = today;
	endtime = today;
	if(type>0){
		endtime = dateUtil.calculateByDate(today,type);
	}else{
		starttime = dateUtil.calculateByDate(today,type);
	}
	console.log(starttime+","+endtime);
	$("#starttime").datebox({
		onSelect:function(date){
			var stime = dateUtil.date2String("yyyy-MM-dd HH:mm:ss",date);
			var etime = $("#endtime").datebox("getValue")+" 00:00:00";
			var diff = dateUtil.diffDateTime(stime,etime);
			if(diff<0){
				top.layer.msg("开始日期不能超过结束日期！",{icon:0});
				setTimeout(function(){
					$("#starttime").datebox("setValue",starttime);
				},1);
			}else{
				starttime = stime;
			}
		}
	}).datebox("setValue",starttime);
	$("#endtime").datebox({
		onSelect:function(date){
			var etime = dateUtil.date2String("yyyy-MM-dd HH:mm:ss",date);
			var stime = $("#starttime").datebox("getValue")+" 00:00:00";
			var diff = dateUtil.diffDateTime(stime,etime);
			if(diff<0){
				top.layer.msg("结束日期不能小于开始日期！",{icon:0});
				setTimeout(function(){
					$("#endtime").datebox("setValue",endtime);
				},1);
			}else{
				endtime = etime;
			}
		}
	}).datebox("setValue",endtime);
	initdg();
}

function setCustomTime(){
	$("#custom_con").css("display","");
}

//初始化表格,获取用户操作日志
function initdg(){
	var stime = $("#starttime").datebox("getValue")+" 00:00:00";
	var etime = $("#endtime").datebox("getValue")+" 23:59:59";
	$("#dg").datagrid({
		border:false,
		url:"../service/api?action=useroplog&stime="+stime+"&etime="+etime,
		title:"用户操作日志",
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		nowrap:true,
		fit:true,
		pagination:true,
		pageNumber:1,
		toolbar:'#tb',
		loadMsg:"正在加载数据，请稍候……",
		columns:[[    	       
		    {field:'createtime',title:'操作时间',width:'150',align:'center'},
		    {field:'ip',title:'IP地址',width:'120',align:'center'},
		    {field:'username',title:'用户名',width:'90',align:'center'},
		    {field:'userxm',title:'操作人',width:'70',align:'center'},
		    {field:'name',title:'业务名称',width:'120',align:'center'},
		    {field:'optype',title:'操作类型',width:'120',align:'center'},
			{field:'opcontent',title:'操作内容',width:'390',align:'left'}
		]],
		onClickRow:function(index, row){
			showMoreInfo(row);
		}
	});
}
//显示详细信息
function showMoreInfo(row){
	var sb = new Array();
	sb.push("<div style='width:788px;height:446px;padding:5px;overflow:hidden;'>");
	sb.push("<table border='1' bordercolor='#eeeeee' style='width:100%;height:100%;border-collapse:collapse;font-size:13px;'>");
	sb.push("<tr><td style='padding:5px;'><b>客户端IP地址：</b>"+row.ip+"<span style='margin-left:50px;'><b>操作用户：</b>"+row.username+"（"+row.userxm+"）</span><span style='margin-left:50px;'><b>操作时间：</b>"+row.createtime+"</span></td></tr>");
	sb.push("<tr><td style='padding:5px;'><span style='display:inline-block;width:220px;'><b>业务名称：</b>"+row.name+"</span><span style='margin-left:30px;'><b>操作类型：</b>"+row.optype+"</span></td></tr>");
	sb.push("<tr><td style='padding:5px;height:360px;' vAlign='top'><b>操作内容：</b><div style='width:100%;height:350px;overflow:auto;'>"+row.opcontent+"</div></td></tr>");
	sb.push("</table>");
	sb.push("</div>");
	var content = sb.join("");
	top.layerIndex = top.openLayerWindow("用户操作日志详细信息",0.6,["800px","500px"],1,content,function(index){
		top.layer.close(index);
	},function(index){
		top.layerInde = index;
	});
}