$(function(){
	$("#tbForm td").css("padding","10px 5px");
	$("input[name=rtype]").click(function(){
		initColumns();
	});
});
var webconfigs = [];
//加载配置文件
function loadConfigs(){
	doAjax("../json/Web!loadConfigs",{},{},function(rsMap,op){
		if(rsMap.state){
			webconfigs = rsMap.list;
			initColumns();
		}else{
			
		}
	});
}
//初始化表字段
function initColumns(){
	var count = webconfigs.length;
	var config = null;
	var table = $("input[name=rtype]:checked").val();
	var nodeName = table+"_columns";
	var nodeValue = "";
	for(var i=0;i<count;i++){
		config = webconfigs[i];
		if(config.nodeName==nodeName){
			nodeValue = config.nodeValue;
			break;
		}
	}
	var columnArray = eval("("+nodeValue+")");
	var columns = new Array();
	for(var i=0;i<columnArray.length;i++){
		columns.push({field:columnArray[i].field,title:columnArray[i].text});
	}
	initDataTable(columns);
}

//页面加载完成事件
function onloadComplete(){
	loadConfigs();
}

//初始化数据表格
function initDataTable(columns){
	var table = $("input[name=rtype]:checked").val();
	var tableName = $("input[name=rtype]:checked").next().text();
	$("#dg").datagrid({
		title:tableName+"数据表["+table+"]数据列表",
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		nowrap:false,
		fit:true,
		remoteSort:false,
		toolbar:'#tb',
		//closed:true,
		loadMsg:"正在加载数据，请稍候……",
		columns:[columns]
	}).datagrid("loadData",[]);
	loadTableData();
}
//加载表数据
function loadTableData(){
	var table = $("input[name=rtype]:checked").val();
	var param = {table:table,method:"getTableData"};
	$("#dg").datagrid("loading");
	doAjax("../json/DataService!listAll",param,{},function(rsMap,op){
		$("#dg").datagrid("loaded");
		if(rsMap.state){
			var data = rsMap.data;
			$("#dg").datagrid("loadData",data);
		}else{
			
		}
	});
}

//加载数据表字段数据
function loadTableColumns(){
	var table = $("input[name=rtype]:checked").val();
	var tableName = $("input[name=rtype]:checked").next().text();
	doAjax("../json/DataService!listAll",{method:"getTableColumns",table:table},{},function(rsMap,op){
		if(rsMap.state){
			var data = rsMap.data;
			var count = data.length;
			var columns = [];
			for(var i=0;i<count;i++){
				columns.push({field:data[i].column_name,title:data[i].description});
			}
			initDataTable(table,tableName,columns);
		}else{
			
		}
	});
}

//提交保存数据
function doSubmitAction(){
	var id = $("#id").val();
	var table = $("input[name=rtype]:checked").val();
	var name = $("#name").textbox("getValue");
	var columnType = $("#columnType").val();
	var column_length = $("#column_length").val();
	var description = $("#description").textbox("getValue");
	//alert(table+","+name+","+columnType+","+description);
	if(name==null||name==""){
		top.layer.alert("字段名称不能为空！",{icon:0});
		return;
	}
	if(description==null||description==""){
		top.layer.alert("字段说明不能为空！",{icon:0});
		return;
	}
	if(columnType=="varchar"){
		columnType = "varchar("+column_length+")";
	}
	var param = {};
	param.table = table;
	param.column_name = name;
	param.columnType = columnType;
	param.description = description;
	var op = $("#op").val();
	var serviceUrl = "../json/DataService!addColumn";
	var msgtips = "添加";
	if(op=="edit"){
		serviceUrl = "../json/DataService!editColumn";
		param.updatecomment = true;
		msgtips = "修改";
		if(selectRow.description==null||selectRow.description=="null"){
			param.addcomment = true;
		}else if(selectRow.description!=description){
			param.editcomment = true;
		}
	}
	doAjax(serviceUrl,param,{},
	function(rsMap,op){
		hideLoading();
		if(rsMap.state){
			loadTableColumns();
			$("#formWin").window("close");
			top.layer.alert(msgtips+"字段【"+name+"】成功！",{icon:6});
		}else{
			top.layer.alert(msgtips+"字段【"+name+"】失败！"+rsMap.error,{icon:2});
		}
	});
}
//添加按钮事件
function addAction(){
	$("#name").textbox("setValue","");
	$("#description").textbox("setValue","");
	$("#op").val("add");
	$("#formWin").window("open");
	$("#name").textbox("enable");
	$("#columnType").attr("disabled",false);
}
var selectRow = null;
//修改按钮事件
function editAction(){
	selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.alert("请从下面列表中选择您要修改的数据！",{icon:0});
	}else{
		$("#op").val("edit");
		$("#name").textbox("setValue",selectRow.column_name);
		$("#name").textbox("disable");
		$("#columnType").val(selectRow.data_type);
		$("#columnType").attr("disabled",true);
		showLength();
		$("#column_length").val(selectRow.max_length);
		$("#description").textbox("setValue",selectRow.description);
		$("#formWin").window("open");
		
	}
}

//删除数据
function deleteAction(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.alert("请从下面列表中选择您要删除的数据！",{icon:0});
	}else{
		top.layer.confirm("确定要删除该数据吗？", function(index){
    		top.layer.close(index);
    		var table = $("input[name=rtype]:checked").val();
    		var columnName = selectRow.column_name;
    		var param = new Object();
			param.method="deleteTableColumn";
			param.table = table;
			param.column_name = columnName;
			doAjax("../json/DataService!editData",param,{},function(rsMap,op){
				var state = rsMap.state;
				if(state){
					loadTableColumns();
					top.layer.alert("表字段【"+columnName+"】删除成功！",{icon:6});
				}else{
					top.layer.alert("表字段【"+columnName+"】删除失败！"+rsMap.error,{icon:5});
				}
			});
    	});
	}
}

//显示文件上传窗口
function showUploadWin(){
	var table = $("input[name=rtype]:checked").val();
	top.openLayerWindow("数据批量导入", 0.5, ["600px","300px"], 2, "../upload-excel-layerdata.jsp?table="+table, function(index) {
		top.layer.close(index);
		loadData();
	});
}