$(function(){
	$("#tbForm td").css("padding", "10px");
	initDataGrid();
	initDicCombobox();
	loadData();
});
//页面加载完成事件
function init(){
	
}

var selectDeptid = 0;
//表格上传其他参数
var importExcelData = {deptid:selectDeptid};
//初始化文件上传控件
layui.use('upload', function() {
	var upload = layui.upload;
	//用户头像上传初始化
	var uploadInst = upload.render({
		elem : '#user-photo' //绑定元素
		,url : '../service/api?action=uploadHeadimg' //上传服务接口
		,accept : 'images'
		,data : {},
		done : function(result) {//上传完毕回调 200
			var code = result.code;
			if(code==0){
				$("#user-photo").attr("src",basepath+result.absolutePath);
				$("#userimg").val(result.absolutePath);
				top.layer.msg("头像图片上传成功！");
			}
		},
		error : function() {//请求异常回调
			top.layer.msg("头像图片上传出现异常！");
		}
	});
	//批量导入数据上传按钮初始化
	var uploadInst = upload.render({
		elem : '#btn_upload' //绑定元素
		,url : basepath+'service/api?action=importDeptPerson' //上传服务接口
		,accept : 'file'
		,ext:'xls|XLS',
		data : importExcelData,
		before:function(obj){
			importExcelData.deptid = selectDeptid;
			top.layer.msg("正在批量导入数据，请稍候...",{icon:16,shade:[0.5,'#f5f5f5'],scrollbar:false,time:30000});
		},
		done : function(result) {//上传完毕回调 200
			var code = result.code;
			if(code==0){
				top.layer.msg("成功导入更新["+result.data+"]条数据！");
				loadData();
				//loadDeptPerson();
			}else{
				top.layer.msg("批量导入更新数据失败！"+result.msg);
			}
		},
		error : function() {//请求异常回调
			top.layer.msg("批量导入更新数据出现异常！");
		}
	});
});

//数据字典加载完成回调事件
function initDicCombobox(){
	initCombobox("zzmm",getDicData("ZZMM",false),"val","text",function(){});
	initCombobox("zm",getDicData("ZM",false),"val","text",function(){});
}

//加载部门数据
function loadData(){
	$("#menuTree").html("正在加载数据，请稍候......");
	doAjax("../service/api",{action:"loaddata",method:"queryDeptAndPersonCount"},{},function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			cache["treeData"]=data;
			initTree();
		}
	});
}
var editNode = null;
//创建组织目录树
function initTree(){
	var data = cache["treeData"];
	var treeData = getTreeChildren("0");
	$("#menuTree").tree({
		animate:true,
		lines:true,
		formatter:function(node){
			return node.text+" <label class='personcount'>("+node.pcount+"人)</label>";
		},
		onContextMenu: function(e,node){
			e.preventDefault();
		},onSelect:function(node){
			editNode = node;
			selectDeptid = editNode.id;
			$("#nav").attr("deptid",editNode.id);
			$("#deptid").val(editNode.id);
			var tmpArr = new Array();
			tmpArr.push(editNode.text);
			var pnode = $("#menuTree").tree("getParent",editNode.target);
			while(pnode!=null){
				tmpArr.push(pnode.text);
				pnode = $("#menuTree").tree("getParent",pnode.target);
			}
			tmpArr = tmpArr.reverse();
			$("#nav").html(tmpArr.join(" -> "));
			loadDeptPerson();
		},onLoadSuccess:function(node,data){
			var selectNode = null;
			if(data.length == 0){
				return;
			}
			if(selectDeptid != 0){
				selectNode = $('#menuTree').tree('find', selectDeptid);
			}else{
				selectNode = $('#menuTree').tree('find', data[0].id);
			}
			$('#menuTree').tree('select', selectNode.target);
		}
	});
	$("#menuTree").tree("loadData",treeData);
	$("#menuTree .tree-node").css("height","22px");
}
//获取树节点孩子节点
function getTreeChildren(pid){
	var treeArr = new Array();
	var treeData = cache["treeData"];
	var count = treeData.length;
	for(var i=0;i<count;i++){
		var record = treeData[i];
		if(record.pid==pid){
			var treeNode = new Object();
			treeNode.id = record.id;
			treeNode.code = record.code;
			treeNode.text = record.name;
			treeNode.rank = record.rank;
			treeNode.pcount = record.pcount;
			treeNode.pid = record.pid;
			if(pid=="0"){
				treeNode.iconCls = "icon-hamburg-home";
			}else{
				treeNode.iconCls = "icon-hamburg-project";
			}
			treeNode.children = getTreeChildren(record.id);
			treeArr.push(treeNode);
		}
	}
	//根据排序进行排序
	treeArr =treeArr.sort(sortByAtt("rank","desc"));
	return treeArr;
}

//初始化部门人员列表
function initDataGrid(){
	$("#dg").datagrid({
		title:"部门人员列表",
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		nowrap:false,
		fit:true,
		remoteSort:false,
		tools:'#tool',
		toolbar:'#tbtool',
		loadMsg:"正在加载数据，请稍候……",
		columns:[[
			{field:'id',title:'工号',width:'60',align:"center"},
			{field:'name',title:'姓名',width:'90',align:"center"},
			{field:'sex',title:'性别',width:'60',align:"center"},
			{field:'age',title:'年龄',width:'60',align:"center"},
			{field:'phone',title:'联系方式',width:'120',align:"center"},
			{field:'zzmm',title:'政治面貌',width:'80',align:"center"},  
			{field:'zm',title:'职名',width:'120',align:"center"},
			{field:'createtime',title:'创建时间',width:150,align:"center"},
			{field:'edittime',title:'修改时间',width:150,align:"center"}
		]]
	});
}
//加载部门人员信息
function loadDeptPerson(){
	selectDeptid = editNode.id;
	var param = new Object();
	param.action = "loaddata";
	param.method="queryDeptPerson";
	param.deptid = selectDeptid;
	$("#dg").datagrid("loading");
	doAjax("../service/api",param,{},function(rsMap,op){
		$("#dg").datagrid("loaded");
		if(rsMap.code==0){
			var data = rsMap.data;
			console.log(data);
			var count = data.length;
			$("#personCount").text(count);
			$("#dg").datagrid("loadData",data);
		}
	});
}
/**
 * 获取名字的拼音首字母，用于检索
 * @param newValue 最后的字符
 * @param oldValue
 */
function getNamePinYin(newValue, oldValue){
	var param = {};
	param.action="getNameFirstChar";
	param.strtext = newValue;
	doAjax("../service/api",param,{},
	function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			$("#namepy").val(data);
		}
	});
}
/**
 * 检查人员工号是否存在
 * @param newValue
 * @param oldValue
 */
function checkPersonExist(newValue, oldValue){
	var op = $("#op").val();
	if(op=="edit"){
		return;
	}
	var param = {};
	param.action="loaddata";
	param.method = "checkPersonExist";
	param.id = newValue;
	doAjax("../service/api",param,{},
	function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			console.log(data);
			if(data.length>0){
				var person = data[0];
				var info = "【部门："+person.deptname+"，工号："+person.id+"，姓名："+person.name+"】";
				$("#msg").html("该人员编号已存在！"+info);
				$("#btnSubmit").linkbutton("disable");
			}else{
				$("#msg").html("");
				$("#btnSubmit").linkbutton("enable");
			}
		}
	});
}

//添加人员
function addAction(){
	$("#op").val("add");
	$("#id").textbox("setValue","");
	$("#id").textbox("readonly",false);
	$("#name").textbox("setValue","");
	$("#namepy").val("");
	$("#phone").textbox("setValue","");
	$("#description").textbox("setValue","");
	$("#headimg").val("images/user_head.png");
	$("#user-photo").attr("src","../images/user_head.png");
	$("#formWin").window("open").window('setTitle',"添加部门人员");
}
//修改人员信息
function editAction(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.msg("请从下面列表中选择您要修改的数据！",{icon:0});
	}else{
		console.log(selectRow);
		$("#op").val("edit");
		$("#id").textbox("setValue",selectRow.id);
		$("#id").textbox("readonly");
		$("#name").textbox("setValue",selectRow.name);
		$("#namepy").val(selectRow.namepy);
		$("#sex").combobox("setValue",selectRow.sex);
		$("#age").numberbox("setValue",selectRow.age);
		$("#phone").textbox("setValue",selectRow.phone);
		$("#zzmm").combobox("setValue",selectRow.zzmm);
		$("#zm").combobox("setValue",selectRow.zm);
		$("#description").textbox("setValue",selectRow.description);
		if(selectRow.headimg==""||selectRow.headimg=="null"||selectRow.headimg==null){
			selectRow.headimg="images/user_head.png";
		}
		$("#userimg").val(selectRow.headimg);
		$("#user-photo").attr("src",basepath+selectRow.headimg);
		$("#formWin").window("open").window('setTitle',"修改部门人员");;
	}
}

//删除数据
function deleteAction(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.alert("请从下面列表中选择您要删除的数据！",{icon:0});
	}else{
		var rowIndex = $("#dg").datagrid("getRowIndex",selectRow);
		top.layer.confirm("确定要删除该数据吗？", function(index){
    		top.layer.close(index);
    		var param = new Object();
    		param.action="saveOrUpdate";
			param.method="deletePerson";
			param.id = selectRow.id;
			doAjax("../service/api",param,{},function(rsMap,op){
				var data = rsMap.data;
				if(data>0){
					top.layer.msg("人员信息删除成功！",{icon:6});
					//删除列表中的数据并更新树目录中的总数
					$("#dg").datagrid("deleteRow",rowIndex);
					var rowcount = $("#dg").datagrid("getRows").length;
					$('#menuTree').tree('update', {target:editNode.target,pcount:rowcount});
					//loadDeptPerson();
				}else{
					top.layer.msg("人员信息删除失败！",{icon:5});
				}
			});
    	});
	}
}

//提交保存数据
function doSubmitAction(){
	var formArray = $("#myform").serializeArray();
	var param = formArrayToJson(formArray);
	if(param.id == "" || param.id == null){
		top.layer.msg("人员编号或工号不能为空！",{icon:0});
		return;
	}
	if(param.name == "" || param.name == null){
		top.layer.msg("人员姓名不能为空！",{icon:0});
		return;
	}
	param.action = "saveOrUpdate";
	param.method="addDeptPerson";
	if(param.op == "add"){
		param.method="addDeptPerson";
	}else{
		param.method="editDeptPerson";
	}
	console.log(param);
	doAjax("../service/api",param,{},
	function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			if(parseInt(data,10)==1){
				$("#formWin").window("close");
				loadDeptPerson();
				top.layer.msg("人员信息数据保存成功！",{icon:6});
			}else{
				top.layer.msg("人员信息数据保存失败！",{icon:5});
			}
		}else{
			top.layer.msg("人员信息数据保存失败！"+rsMap.msg,{icon:2});
		}
	});
	return;
	/**
	var id = $("#id").val();
	var name = $("#name").textbox("getValue");
	var sex = $("#sex").combobox("getValue");
	var phone = $("#phone").textbox("getValue");
	var deptid = $("#deptid").val();
	var zzmm = $("#zzmm").combobox("getValue");
	var gwjb = $("#gwjb").combobox("getValue");
	var description = $("#description").textbox("getValue");
	var headimg = $("#userimg").val();
	//alert(name+","+department+","+duty+","+telephone+","+description+","+headimg);
	if(name==null||name==""){
		top.layer.msg("人员姓名不能为空！",{icon:0});
		//showSuccessMsg("opMessage","人员姓名不能为空！");
		return;
	}
	var param = {};
	param.name = name;
	param.sex = sex;
	param.phone = phone;
	param.zzmm = zzmm;
	param.gwjb = gwjb;
	param.deptid = $("#nav").attr("deptid");
	param.description = description;
	param.headimg = headimg;
	param.action = "saveOrUpdate";
	param.method="addDeptPerson";
	console.log(param);
	if(id!=null&&id!=""){
		param.method="editDeptPerson";
		param.id=id;
	}
	doAjax("../service/api",param,{},
	function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			if(parseInt(data,10)==1){
				$("#formWin").window("close");
				loadDeptPerson();
				top.layer.msg("人员信息数据保存成功！",{icon:6});
			}else{
				top.layer.msg("人员信息数据保存失败！",{icon:5});
			}
		}else{
			top.layer.msg("人员信息数据保存失败！"+rsMap.msg,{icon:2});
		}
	});
	*/
}