$(function(){
	$("#tbForm td").css("padding", "10px");
	initDataGrid();
	loadData();
});

//加载数据
function loadData(){
	$("#menuTree").html("正在加载数据，请稍候......");
	var param = {method:"queryDept"};
	doAjax("../service/api",{action:"loaddata",method:"queryDept"},{},function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			cache["treeData"]=data;
			initTree();
		}
	});
}

//创建树结构
function initTree(){
	var data = cache["treeData"];
	var treeData = getTreeChildren("0");
	$("#menuTree").tree({
		animate:false,
		lines:true,
		onContextMenu: function(e,node){
			e.preventDefault();
			$(this).tree('select',node.target);
			$('#mm').menu('show',{
				left: e.pageX,
				top: e.pageY
			});
		},onClick:function(node){
			editNode = node;
			//$("#pid").val(node.id);
			$("#part_id").textbox("setValue",node.id);
			$("#part_code").textbox("setValue",node.code);
			//$("#part_name").val(node.text);
			$("#part_name").textbox("setValue",node.text);
			$("#part_rank").numberbox("setValue",node.rank);
			$("#regionCenter .panel-title").text("["+node.text+"]部门人员列表");
			loadDeptPerson(node.id);
		},onLoadSuccess:function(node,data){
			$("#menuTree").tree("select",data[0].target);
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

//添加树节点
function append(){
	top.layer.prompt({
		title:"请输入你要添加的部门名称"
	},function(value,index,elem){
		top.layer.close(index);
		var nodeText = value;
		var t = $('#menuTree');
		var node = t.tree('getSelected');
		var pid = node.id;
		var pcode = node.code;
		var param = new Object();
		param.action = "saveOrUpdate";
		param.name = nodeText;
		param.pid = pid;
		param.method = "addDept";
		var children = t.tree("getChildren",node.target);
		var count = children.length;
		var code = count<10?"0"+count:""+count;
		param.code = pcode+code;
		param.rank = 100-count;
		top.layerIndex=top.showLayerLoading("请求中处理中，请稍候……");
		doAjax("../service/api",param,{},
		function(rsMap,op){
			top.layer.close(top.layerIndex);
			if(parseInt(rsMap.data,10) == 1){
				top.layer.msg("添加部门数据成功！",{icon:6});
			}
			loadData();
		});
	});
}

//修改功能菜单节点
function updateMenu(){
	var paramArr = new Array();
	var id = $("#part_id").textbox('getValue');
	var name = $("#part_name").textbox('getValue');
	var rank = $("#part_rank").numberbox('getValue');
	var param = new Object();
	param.id = id;
	param.name = name;
	param.rank = rank;
	param.method = "editDept";
	param.action = "saveOrUpdate";
	if(id==null||id==""){
		top.layer.msg("请从左边功能树中选择你要修改的部门！");
		return;
	}
	var t = $('#menuTree');
	var treeNode = t.tree('find',id);
	top.layerIndex=top.showLayerLoading("请求中处理中，请稍候……");
	doAjax("../service/api",param,{},function(rsMap,op){
		top.layer.close(top.layerIndex);
		if(rsMap.data=="1"){
			top.layer.msg("部门信息修改成功！",{icon:6});
			loadData();
		}else{
			top.layer.msg("部门信息修改失败！",{icon:5});
		}
	});
}
//删除部门信息
function removeit(){
	var node = $('#menuTree').tree('getSelected');
	if(node.id==1){
		top.layer.msg("部门根节点不允许删除！");
		return;
	}
	top.layer.confirm("确定要删除该部门信息吗？",{icon:3},function(index){
		top.layer.close(index);
		var param = new Object();
		param.action = "saveOrUpdate";
		param.method="deleteDept";
		param.id = node.id;
		top.layerIndex=top.showLayerLoading("请求中处理中，请稍候……");
		doAjax("../service/api",param,{},function(rsMap,op){
			top.layer.close(top.layerIndex);
			var data = rsMap.data;
			if(data>0){
				top.layer.msg("部门信息删除成功！",{icon:6});
				loadData();
			}else{
				top.layer.msg("部门信息删除失败！",{icon:5});
			}
		});
	})
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
		toolbar:'#tbtool',
		loadMsg:"正在加载数据，请稍候……",
		columns:[[
			{field:'id',title:'编号',width:'40',hidden:true},
			{field:'name',title:'姓名',width:'90',align:"center"},
			{field:'sex',title:'性别',width:'90',align:"center"},
			{field:'zzmm',title:'政治面貌',width:'110',align:"center"},   
			{field:'gwjb',title:'职务',width:'110',align:"center"},   
			{field:'phone',title:'联系方式',width:'120',align:"center"},
			{field:'deptname',title:'所属部门',width:'110',align:"center"},
			{field:'createtime',title:'创建时间',width:170,align:"center"},
			{field:'edittime',title:'修改时间',width:170,align:"center"}
		]]
	});
}

function loadDeptPerson(deptid){
	var param = new Object();
	param.action = "loaddata";
	param.method="loadDeptPerson";
	param.deptid = deptid;
	top.layerIndex=top.showLayerLoading("请求中处理中，请稍候……");
	doAjax("../service/api",param,{},function(rsMap,op){
		top.layer.close(top.layerIndex);
		if(rsMap.code==0){
			var data = rsMap.data;
			$("#dg").datagrid("loadData",data);
		}
	});
}