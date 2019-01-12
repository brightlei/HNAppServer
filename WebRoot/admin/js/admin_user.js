$(function(){
	$("#tbForm td").css("padding","10px 5px");
});

//页面加载完成事件
function onloadComplete(){
	initDataTable();
	loadPersonData();
	loadRoleInfo();
}
//初始化数据表格
function initDataTable(){
	$("#dg").datagrid({
		title:"用户信息列表",
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		nowrap:false,
		fit:true,
		remoteSort:false,
		toolbar:'#tb',
		loadMsg:"正在加载数据，请稍候……",
		columns:[[
			{field:'id',title:'编号',width:'40',hidden:true},
			{field:'rolename',title:'用户角色',width:'120'},
			{field:'username',title:'用户名',width:'130'},
			{field:'name',title:'姓名',width:'70'},
			{field:'sex',title:'性别',width:'60'},
			{field:'phone',title:'联系方式',width:'120'},
			{field:'zzmm',title:'政治面貌',width:'100'},
			{field:'deptname',title:'所属部门',width:'140',sortable:true},
			{field:'createtime',title:'创建时间',width:170},
			{field:'edittime',title:'修改时间',width:170}
		]]
	});
	loadUserData();
}
//加截用户角色权限
function loadRoleInfo(){
	doAjax("../service/api",{action:"loaddata",method:"queryRole"},{},function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			var count  = data.length;
			var sb = new Array();
			var rcd = null;
			for(var i=0;i<count;i++){
				rcd = data[i];
				sb.push('<option value="'+rcd.id+'">'+rcd.name+'</option>');
			}
			$("#roleinfo").append(sb.join(""));
		}
	});
}
//加载人员数据
function loadPersonData(){
	var param = new Object();
	param.action = "loaddata";
	param.method = "loadNoUserPerson";
	doAjax("../service/api",param,{},function(rsMap,op){
		console.log(rsMap,"rsMap");
		if(rsMap.code==0){
			var data = rsMap.data;
			cache["person"]=data;
			initPersonList();
		}
	});
}
//初始化人员下接列表
function initPersonList(){
	var data = cache["person"];
	var count = data.length;
	$("#personid").empty();
	var text = "";
	var rcd = null;
	var isUser = false;
	for(var i=0;i<count;i++){
		rcd = data[i];
		isUser = checkPersonIsUser(rcd.id);
		if(!isUser){
			text = rcd.name+"(所属部门:"+rcd.deptname+"，联系电话:"+rcd.phone+")";
			$("#personid").append('<option name="'+rcd.name+'" value="'+rcd.id+'">'+text+'</option>');
		}
	}
}

//检查该人员是否已经为用户
function checkPersonIsUser(personid){
	var userdata = cache["user"];
	if(typeof(userdata)=="undefined"){
		userdata = [];
	}
	var isUser = false;
	for(var i=0;i<userdata.length;i++){
		if(userdata[i].personid==personid){
			isUser = true;
			break;
		}
	}
	return isUser;
}

//加载用户数据
function loadUserData(){
	var param = new Object();
	param.action = "loaddata";
	param.method = "getUserData";
	$('#dg').datagrid('loading');
	doAjax("../service/api",param,{},function(rsMap,op){
		$('#dg').datagrid('loaded');
		if(rsMap.code==0){
			var data = rsMap.data;
			console.log(data);
			cache["user"]=data;
			$('#dg').datagrid('loadData', data);
		}
	});
}

//提交保存数据
function doSubmitAction(){
	var personid = $("#personid").val();
	var username = $("#username").val();
	var roleid = $("#roleinfo").val();
	var param = {};
	param.action = "saveOrUpdate";
	param.user_name = username;
	param.personid = personid;
	param.roleid = roleid;
	param.method="addUser";
	if(personid==null || personid==""){
		top.layer.msg("请选择分配用户账号的人员！");
		return;
	}
	if(username==null || username==""){
		top.layer.msg("系统登录账号不能为空！");
		return;
	}else if(username.length<6){
		top.layer.msg("系统登录账号不能少于6位！");
		return;
	}
	doAjax("../service/api",param,{},
	function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			if(parseInt(data,10)==1){
				$("#formWin").window("close");
				loadUserData();
				top.layer.alert("用户【"+username+"】信息添加成功！默认密码为123456");
			}else{
				top.layer.msg("用户【"+username+"】信息添加失败！");
			}
		}else{
			top.layer.msg("保存用户【"+username+"】信息异常！"+rsMap.error);
		}
	});
}
//修改用户信息
function doEditAction(){
	var roleid = $("#roleinfo").val();
	var param = {};
	param.method="setUserRole";
	param.roleid=roleid;
	param.id=$("#id").val();
	top.layerIndex = top.showLayerLoading("正在设置用户角色权限，请稍候……");
	doAjax("../json/DataService!editData",param,{},
	function(rsMap,op){
		top.layer.close(top.layerIndex);
		if(rsMap.state){
			var data = rsMap.data;
			if(parseInt(data,10)==1){
				$("#formWin").window("close");
				loadUserData();
				top.layer.msg("设置用户角色权限信息成功！");
			}else{
				top.layer.msg("设置用户角色权限信息失败！");
			}
		}else{
			top.layer.msg("设置用户角色权限信息异常！"+rsMap.error);
		}
	});
}
//添加按钮
function addAction(){
	$("#id").val("");
	$('#username').removeAttr("disabled");
	$('#username').val("");
	$("#personName").html("");
	$("#personName").css("display","none");
	$("#personid").css("display","");
	$("#submitBtn").css("display","block");
	$("#editBtn").css("display","none");
	$("#formWin").window("open");
}
//修改数据
function editAction(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.msg("请从下面列表中选择您要修改的数据！");
	}else{
		$("#id").val(selectRow.id);
		$("#username").val(selectRow.username);
		$("#username").attr("disabled","disabled");
		$("#userpwd").val(selectRow.userpwd);
		$("#personName").html(selectRow.name);
		$("#roleinfo").val(selectRow.roleid);
		$("#personName").css("display","");
		$("#personid").css("display","none");
		$("#submitBtn").css("display","none");
		$("#editBtn").css("display","block");
		$("#formWin").window("open");
	}
}
//删除数据
function deleteAction(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.msg("请从下面列表中选择您要删除的数据！");
	}else{
		var rowIndex = $("#dg").datagrid("getRowIndex",selectRow);
		top.layer.confirm("确定要删除该数据吗？",{
			title:"提示信息",
			icon:3
		},function(index){
			top.layer.close(index);
			var param = new Object();
			param.action = "saveOrUpdate";
			param.method="deleteUser";
			param.id = selectRow.id;
			top.layerIndex = top.showLayerLoading("正在执行删除请求，请稍候……");
			doAjax("../service/api",param,{},function(rsMap,op){
				top.layer.close(top.layerIndex);
				var data = rsMap.data;
				if(data>0){
					$("#dg").datagrid("deleteRow",rowIndex);
					top.layer.msg("人员信息删除成功！",{icon:6});
				}
			});
		});
	}
}

//显示地图窗口
function showMapWin(id,x,y){
	$("#userid").val(id);
	$("#mapWin").window("open");
	$("#mapFrame").attr("src","bdmap-position.html?t="+Math.random()+"&x="+x+"&y="+y);
	if(x!=null&&x!=""&&y!=null&&y!=""){
		setUserPosition(x,y);
	}
}
//设置用户坐标
function setUserPosition(lon,lat){
	$("#l-lon").html(lon);
	$("#l-lat").html(lat);
	$("#pointinfo").css("display","block");
}

//保存用户位置信息
function saveUserPosition(){
	var userid = $("#userid").val();
	var lon = $("#l-lon").text();
	var lat = $("#l-lat").text();
	if(lon==""&&lat==""){
		top.layer.msg("请在地图上点击选择定位点！",{icon:0});
	}
	var param = {
		method:"editUser",
		id:userid,
		longitude:lon,
		latitude:lat
	};
	top.layerIndex = top.showLayerLoading("正在保存用户位置信息，请稍候……");
	doAjax("../json/DataService!saveData",param,{},
	function(rsMap,op){
		top.layer.close(top.layerIndex);
		if(rsMap.state){
			var data = rsMap.data;
			if(parseInt(data,10)==1){
				$("#mapWin").window("close");
				loadUserData();
				top.layer.msg("用户位置信息设置成功！",{icon:6});
			}else{
				top.layer.msg("用户位置信息设置失败！",{icon:5});
			}
		}else{
			top.layer.msg("用户位置信息设置失败！"+rsMap.error,{icon:5});
		}
	});
}

//取消
function cancelSave(){
	$("#mapWin").window("close");
}

/**
var editNode = null;
function loadMenuTree(){
	showLoading("正在加载数据，请稍候----");
	var paramArr = new Array();
	paramArr.push("method=loadDeptList");
	$.ajax({
		type: "POST",
		url: "QueryData_getQueryResult.do",
		data:paramArr.join("&"),
		dataType:"json",
		success: function(rsMap){
			hideLoading();
			var data = rsMap.data;
			cache["treeData"] = data;
			var treeData = getTreeChildren("0");
			$("#menuTree").tree({
				animate:true,
				lines:true,
				//checkbox:true,
				onContextMenu: function(e,node){
					e.preventDefault();
					$(this).tree('select',node.target);
					$('#mm').menu('show',{
						left: e.pageX,
						top: e.pageY
					});
				},onClick:function(node){
					editNode = node;
					var isLeaf = $(this).tree("isLeaf",node.target);
					if(isLeaf){
						var pid = $(this).tree("getParent",node.target).id;
						$("#id").val(node.personId);
						$("#person_name").val(node.text);
						$("input[name=radioDept][value="+pid+"]").attr("checked",'checked');
						$("input[name=radioDuty][value="+node.dutyNo+"]").attr("checked",'checked');
					}
				}
			});
			$("#menuTree").tree("loadData",treeData);
		},error:function(req,status,e){
			hideLoading();
			$("#menuTree").append("<span style='color:red;'>获取功能树数据异常，异常信息：</span><br/>"+req.responseText);
			//showMessage("获取功能树数据异常："+e);
		}
	});
}
//获取树节点孩子节点
function getTreeChildren(pid){
	var treeArr = new Array();
	var treeData = cache["treeData"];
	var personData = cache["data"];
	var personCount = personData.length;
	var count = treeData.length;
	for(var i=0;i<personCount;i++){
		var record = personData[i];
		if(record.deptNo==pid){
			var treeNode = new Object();
			treeNode.id = "p-"+record.tid;
			treeNode.personId = record.tid;
			treeNode.dutyNo = record.dutyNo;
			treeNode.text = record.name;
			treeNode.iconCls = "icon-hamburg-suppliers";
			treeNode.pid = pid;
			treeNode.type = "person";
			treeArr.push(treeNode);
		}
	}
	for(var i=0;i<count;i++){
		var record = treeData[i];
		if(record.parentId==pid){
			var treeNode = new Object();
			treeNode.id = record.tid;
			treeNode.text = record.name;
			treeNode.pid = record.parentId;
			treeNode.type = "dept";
			treeNode.children = getTreeChildren(record.tid);
			treeArr.push(treeNode);
		}
	}
	return treeArr;
}
//添加人员信息
function append(){
	var selectNode = $("#menuTree").tree("getSelected");
	var deptNo = "";
	if(selectNode.type=="dept"){
		deptNo = selectNode.id;
	}else{
		deptNo = $("#menuTree").tree("getParent",selectNode.target).id;
	}
	$("#id").val("");
	$("#person_name").val("");
	$("input[name=radioDept][value="+deptNo+"]").attr("checked",'checked');
	$("input[name=radioDuty]:eq(0)").attr("checked",'checked');
	showSuccessMsg("opMessage","请在上面表单中填写你要添加的人员信息！");
}
//删除人员信息
function removeit(){
	var selectNode = $("#menuTree").tree("getSelected");
	var personId = "";
	if(selectNode.type=="dept"){
		showErrorMsg("opMessage","请选择您要删除的人员！");
		return;
	}else{
		personId = selectNode.personId;
	}
	$.messager.confirm('确认','您确认想要删除该人员信息吗？',function(r){
	    if (r){
	        var paramArr = new Array();
			paramArr.push("method=deleteRecord");
			paramArr.push("tableName=T_Person");
			paramArr.push("key=tid");
			paramArr.push("value="+personId);
			getAjaxData(paramArr,"QueryData_saveOrUpdate.do",deletaCallBack);
	    }    
	});
}
//删除事件回调方法
function deletaCallBack(rsMap){
	if(rsMap.data>0){
		showSuccessMsg("opMessage","记录删除成功！");
		loadData();
	}
}
//保存表单配置信息
function submitForm(){
	var id = $("#id").val();
	var name = $("#person_name").val();
	if(id==""){
		var isExist = checkDataExist();
		if(isExist){
			showErrorMsg("opMessage","["+name+"]已存在，请不要重复添加！");
			return;
		}
	}
	if(name==null || name==""){
		showErrorMsg("opMessage","人员姓名不能为空，请重新输入！");
		return;
	}
	saveDataInfo();
}

//保存人员信息
function saveDataInfo(){
	var id = $("#id").val();
	var name = $("#person_name").val();
	var deptNo = $("input[name=radioDept]:checked").val();
	var dutyNo = $("input[name=radioDuty]:checked").val();
	var paramArr = new Array();
	var msg = "添加人员信息";
	if(id!=""){
		msg = "修改人员信息";
		paramArr.push("method=editPerson");
		paramArr.push("id="+id);
	}else{
		paramArr.push("method=addPerson");
	}
	paramArr.push("name="+name);
	paramArr.push("deptNo="+deptNo);
	paramArr.push("dutyNo="+dutyNo);
	paramArr.push("phone=");
	showLoadingMsg("opMessage","正在保存数据……");
	$.ajax({
		type: "POST",
		url: "QueryData_saveOrUpdate.do",
		data: paramArr.join("&"),
		dataType:"json",
		success: function(rsMap){
			if(rsMap.data!="-1"){
				showSuccessMsg("opMessage",msg+"成功！");
			}
			loadData();
		},error:function(req,status,e){
			showMessage(msg+"异常，信息："+e);
		}
	});
}
//检查数据是否存在
function checkDataExist(){
	var name = $("#person_name").val();
	var data = cache["data"];
	var length = data.length;
	var rcd = null;
	var isExist = false;
	for(var i=0;i<length;i++){
		rcd = data[i];
		if(rcd.name==name){
			isExist = true;
			break;
		}
	}
	return isExist;
}

*/