$(function(){
	$("#tbForm td").css("padding", "10px");
	loadData();
});

//加载数据
function loadData(){
	$("#menuTree").html("正在加载数据，请稍候......");
	doAjax(basepath+"service/api",{action:"loaddata",method:"getRoleRight"},{},function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			cache["treeData"]=data;
			initTree();
			loadWebPages();
		}else{
			$("#menuTree").html("加载数据失败！");
		}
	});
}
//加载系统所有页面
function loadWebPages(){
	doAjax(basepath+"service/api",{action:"webpages"},{},function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			cache["webpages"]=data;
			initPageCombobox();
			//console.log(data);
			//data.unshift({"val":"","text":"请选择"});
			//initCombobox("right_pageurl",data,"val","text",function(){});
		}
	});
}

function initPageCombobox(){
	var pages = cache["webpages"];
	var rights = cache["treeData"];
	var root = $("#menuTree").tree("find",1);
	rights = $("#menuTree").tree("getChildren",root.target);
	console.log(rights);
	var newpages = [{"val":"","text":"请选择"}];
	var isuse = false;
	for(var i=0;i<pages.length;i++){
		isuse = false;
		for(var k=0;k<rights.length;k++){
			if(rights[k]!=null && rights[k].pageurl==pages[i].text){
				isuse = true;
				console.log(pages[i].text);
				break;
			}
		}
		if(!isuse){
			newpages.push(pages[i]);
		}
	}
	initCombobox("right_pageurl",newpages,"val","text",function(){});
}

//创建树结构
function initTree(){
	var data = cache["treeData"];
	var treeData = getTreeChildren("0");
	$("#menuTree").tree({
		animate:true,
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
			$("#pid").val(node.id);
			$("#right_id").val(node.id);
			$("#right_name").val(node.text);
			$("#right_pageurl").combobox('setValue',node.pageurl);
		}
	});
	$("#menuTree").tree("loadData",treeData);
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
			treeNode.text = record.name;
			treeNode.pid = record.pid;
			treeNode.pageurl = record.pageurl;
			treeNode.iconCls = record.iconcls;
			treeNode.children = getTreeChildren(record.id);
//			if(pid=="0"){
//				treeNode.iconCls="icon-hamburg-sitemap";
//			}else if(treeNode.children.length>0){
//				treeNode.iconCls="icon-hamburg-folder";
//			}else{
//				treeNode.iconCls="icon-hamburg-home";
//			}
			treeArr.push(treeNode);
		}
	}
	return treeArr;
}

//添加树节点
function append(){
	top.layer.prompt({
		title:"请输入你要添加的页面菜单名称"
	},function(value,index,elem){
		top.layer.close(index);
		var nodeText = value;
		var t = $('#menuTree');
		var node = t.tree('getSelected');
		var pid = node.id;
		var param = new Object();
		param.name = nodeText;
		param.pid = pid;
		param.method = "addRoleRight";
		param.action = "saveOrUpdate";
		top.layerIndex=top.showLayerLoading("请求中处理中，请稍候……");
		doAjax(basepath+"service/api",param,{},
		function(rsMap,op){
			top.layer.close(top.layerIndex);
			if(rsMap.data!="-1"){
				top.layer.msg("添加系统功能菜单成功！",{icon:6});
			}
			loadData();
		});
	});
}

//修改功能菜单节点
function updateMenu(){
	var paramArr = new Array();
	var id = $("#right_id").val();
	var name = $("#right_name").val();
	var pageurl = $("#right_pageurl").combobox('getValue');
	var param = new Object();
	param.id = id;
	param.name = name;
	param.pageurl = pageurl;
	param.method = "editRoleRight";
	param.action = "saveOrUpdate";
	if(id==null||id==""){
		top.layer.msg("请从左边功能树中选择你要修改的项！",{icon:0});
		//showErrorMsg("opMessage","请从左边功能树中选择你要修改的项！");
		return;
	}
	var t = $('#menuTree');
	var treeNode = t.tree('find',id);
	doAjax(basepath+"service/api",param,{},function(rsMap,op){
		if(rsMap.data=="1"){
			top.layer.msg("权限菜单项信息修改成功！",{icon:6});
			treeNode.text = name;
			treeNode.pageurl = pageurl;
			t.tree('update',treeNode);
			initPageCombobox();
		}else{
			top.layer.msg("权限菜单项信息修改失败！",{icon:5});
		}
	});
}
//删除部门信息
function removeit(){
	return;
	var selectRow = $('#menuTree').tree('getSelected');
	if(selectRow.id==1){
		top.layer.msg('权限根节点不允许删除！',{icon:0});
		return;
	}
	top.layer.confirm("确定要删除该权限菜单项信息吗？", function(index){
		top.layer.close(index);
		var param = new Object();
		param.method="deleteRoleRight";
		param.id = selectRow.id;
		param.code = selectRow.code;
		doAjax("../json/DataService!editData",param,{},function(rsMap,op){
			var data = rsMap.data;
			if(data>0){
				top.layer.msg("权限菜单项信息删除成功！",{icon:6});
				$("#menuTree").tree("remove",selectRow.target);
				//loadData();
			}
			savelog("权限管理","删除系统权限菜单",JSON.stringify(selectRow));
		});
	});
}