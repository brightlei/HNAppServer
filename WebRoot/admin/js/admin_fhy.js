$(function(){
	initDataGrid();
	loadDeptData(initCheJianList);
	initDicCombobox();
	var today = dateUtil.nowDate2String("yyyy-MM-dd");
	$("#form-cjpxsj").datebox("setValue",today);
	$("#btn_delete").linkbutton({
		onClick:function(){
			var text = $(this).text();
			if(text=="批量删除"){
				$("#dg").datagrid({singleSelect:false,loadFilter:pagerFilter}).datagrid("showColumn","ids").datagrid('loadData',cache["data"]);
				$(this).linkbutton({text:"执行批量删除"});
			}else{
				deleteData();
				//$("#dg").datagrid({singleSelect:true}).datagrid("hideColumn","ids").datagrid("unselectAll");
				//$(this).linkbutton({text:"批量删除"});
			}
		}
	});
});

function initDicCombobox(){
	initCombobox("zzmm",getDicData("ZZMM",true),"val","text",function(){});
	initCombobox("form-zzmm",getDicData("ZZMM",false),"val","text",function(){});
}

function initCheJianList(data){
	var chejian = loadCheJianList(true);
	initCombobox("cj",chejian,"val","text",chejianChangeEvent);
	var form_chejian = loadCheJianList(false);
	initCombobox("form-cj",form_chejian,"val","text",chejianChangeEvent);
}

//车间切换事件，获取车间工区信息
function chejianChangeEvent(newValue,oldValue){
	var chejianId = getCheJianIdByName(newValue);
	var gq_data = loadCheJianGongQu(chejianId,true);
	var form_gq_data = loadCheJianGongQu(chejianId,false);
	initCombobox("gq",gq_data,"val","text",function(){});
	initCombobox("form-gq",form_gq_data,"val","text",function(){});
}

//初始化文件上传控件
layui.use('upload', function() {
	var upload = layui.upload;
	//执行实例
	var uploadInst = upload.render({
		elem : '#btn_upload' //绑定元素
		,url : basepath+'service/api?action=importfhy' //上传服务接口
		,accept : 'file'
		,ext:'xls|XLS',
		data : {},
		before:function(obj){
			top.layer.msg("正在批量导入数据，请稍候...",{icon:16,shade:[0.5,'#f5f5f5'],scrollbar:false,time:30000});
		},
		done : function(result) {//上传完毕回调 200
			var code = result.code;
			if(code==0){
				top.layer.msg("成功导入更新["+result.data+"]条数据！");
				searchByKey();
			}else{
				top.layer.msg("批量导入更新数据失败！"+result.msg);
			}
		},
		error : function() {//请求异常回调
			top.layer.msg("批量导入更新数据出现异常！");
		}
	});
});

//初始化表格数据
function initDataGrid(){
	$("#dg").datagrid({
		//url:"service/api?action=getfhy",
		border:false,
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		//nowrap:false,
		fit:true,
		pagination:true,
		pageNumber:1,
		remoteSort:false,
		toolbar:"#tools",
		loadMsg:"正在加载数据，请稍候……",
		columns:[[
		    {field:'ids',checkbox:true,hidden:true},
		    {field:'bh',title:'工号',width:'60',align:'center'},
		    {field:'op',title:'操作',width:'60',align:'center',
		    formatter:function(value,row,index){
		    	return '<button class="layui-btn layui-btn-normal layui-btn-xs" onclick="editData('+index+')">修改</button>';
		    }},
			{field:'xm',title:'姓名',width:'80'},
			{field:'cj',title:'车间',width:'110'},
			{field:'gq',title:'工区',width:'120'},
			{field:'xb',title:'性别',width:'64'},
			{field:'age',title:'年龄',width:'64'},
			{field:'sjh',title:'手机号码',width:'110'},
			{field:'zzmm',title:'政治面貌',width:'80'},
			{field:'dqfhzg',title:'当前防护资格',width:'120'},
			{field:'cjpxsj',title:'参加培训时间',width:'130'}
		]]
	});
	searchByKey();
}

var isFirst = true;
//根据关键字查询数据
function searchByKey(){
	var key = $("#key").textbox("getValue");
	var param = {action:"loaddata",method:"queryFhyByKey",key:key};
	$("#dg").datagrid("loading");
	doAjax(basepath+"service/api",param,{},function(rsMap,op){
		$("#dg").datagrid("loaded");
		if(rsMap.code==0){
			var data = rsMap.data;
			cache["data"] = data;
			$('#dg').datagrid({pageNumber:1,loadFilter:pagerFilter}).datagrid('loadData',data);
		}
	});
}
//显示高级查询条件面板
function showCondition(type){
	if(type=="show"){
		$("#importbox").hide();
		$("#querybox").show();
	}else{
		$("#importbox").show();
		$("#querybox").hide();
		searchByKey();
	}
}

//高级查询数据
function searchByCondition(){
	$("#myform").form('submit',{
		url:basepath+"service/api?action=loaddata&method=queryFhyByCondition",
		onSubmit:function(param){
			var minage = $("#minage").numberbox("getValue");
			var maxage = $("#maxage").numberbox("getValue");
			var condition = " age bwtween 16 and 100 ";
			if(minage==""){
				minage = 18;
			}
			if(maxage==""){
				maxage = 100;
			}
			param.condition = " age between "+minage+" and "+maxage+" ";
		},
		success:function(jsonstr){    
	        var json = eval("("+jsonstr+")");
	        if(json.code==0){
	        	var data = json.data;
	        	$('#dg').datagrid({pageNumber:1,loadFilter:pagerFilter}).datagrid('loadData',data);
	        }
	    }
	});
}
//数值变化
function numberChange(newValue,oldValue){
	var id = $(this).attr("id");
	if(id=="minage"){
		if(newValue!=""){
			$("#maxage").numberbox({
				min:parseInt(newValue),
				max:100
			});
		}
	}
}
//显示表单修改窗口
function editData(index){
	var rows = $("#dg").datagrid("getRows");
	var selectRow = rows[index];
	$("#rowindex").val(index);
	$("#formWin").window("open");
	$("#id").val(selectRow.id);
	$("#form-xm").textbox("setValue",selectRow.xm);
	$("#form-xb").combobox("setValue",selectRow.xb);
	$("#form-cj").combobox("setValue",selectRow.cj);
	$("#form-gq").combobox("setValue",selectRow.gq);
	$("#form-age").textbox("setValue",selectRow.age);
	$("#form-sjh").textbox("setValue",selectRow.sjh);
	$("#form-zzmm").combobox("setValue",selectRow.zzmm);
	$("#form-dqfhzg").textbox("setValue",selectRow.dqfhzg);
	$("#form-cjpxsj").datebox("setValue",selectRow.cjpxsj);
}
//修改表单数据
function submitEditData(){
	$("#editform").form('submit',{
		url:basepath+"service/api",
		onSubmit: function(){
			var isValid = $(this).form('validate');
			if(isValid){
				layer.msg("数据处理中，请稍候...",{icon:16,shade:[0.5,'#333333'],time:10000});
				$("#submitBtn").linkbutton("disable");
				setTimeout(function(){
					$("#submitBtn").linkbutton("enable");
				},5000);
			}
			return isValid;// 返回false终止表单提交
		},
		success: function(jsonstr){
			var json = eval('(' + jsonstr + ')');  
			if(json.code==0){
				var data = json.data;
				if(parseInt(data,10)==1){
					layer.msg("数据修改成功！",{icon:6});
					$("#formWin").window("close");
					refreshData();
				}else{
					layer.msg("数据修改失败！",{icon:5});
				}
			}else{
				layer.msg("数据修改失败！"+json.msg,{icon:5});
			}
		}
	});
}

function refreshData(){
	var index = parseInt($("#rowindex").val(),10);
	var row = {};
	row.xm = $("#form-xm").textbox("getValue");
	row.xb = $("#form-xb").combobox("getValue");
	row.cj = $("#form-cj").combobox("getValue");
	row.gq = $("#form-gq").combobox("getValue");
	row.age = $("#form-age").textbox("getValue");
	row.sjh = $("#form-sjh").textbox("getValue");
	row.zzmm = $("#form-zzmm").combobox("getValue");
	row.dqfhzg = $("#form-dqfhzg").textbox("getValue");
	row.cjpxsj = $("#form-cjpxsj").datebox("getValue");
	$("#dg").datagrid("updateRow",{
		index:index,row:row
	});
}

//删除数据
function deleteData(){
	var rows = $("#dg").datagrid("getSelections");
	if(rows.length==0){
		$("#btn_delete").linkbutton({text:"批量删除"});
		$("#dg").datagrid({singleSelect:true,loadFilter:pagerFilter}).datagrid("hideColumn","ids").datagrid("unselectAll").datagrid('loadData',cache["data"]);
		return;
	}
	layer.confirm('确认要删除这些数据吗？', {icon: 3, title:'提示'}, function(index){
		layer.close(index);
		var idArr = [];
		for(var i=0;i<rows.length;i++){
			idArr.push("'"+rows[i].bh+"'");
		}
		console.log(idArr);
		var param = {action:"saveOrUpdate",method:"deleteFhy",ids:idArr.join(",")};
		doAjax(basepath+"service/api",param,{},function(rsMap,op){
			if(rsMap.code==0){
				var data = rsMap.data;
				if(parseInt(data)>0){
					layer.msg("数据删除成功！");
					$("#btn_delete").linkbutton({text:"批量删除"});
					$("#dg").datagrid({singleSelect:true}).datagrid("hideColumn","ids").datagrid("unselectAll");
					searchByKey();
				}
			}
		});
		/**
		var rows = $("#dg").datagrid("getRows");
		var selectRow = rows[rowIndex];
		var param = {action:"saveOrUpdate",method:"deleteFhy",id:selectRow.id};
		doAjax(basepath+"service/api",param,{},function(rsMap,op){
			if(rsMap.code==0){
				var data = rsMap.data;
				if(parseInt(data)>0){
					layer.msg("数据删除成功！");
					$("#dg").datagrid("deleteRow",rowIndex);
				}
			}
		});
		*/
	});
}