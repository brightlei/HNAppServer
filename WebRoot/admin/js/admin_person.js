$(function(){
	$("#tbForm td").css("padding","10px 5px");
	initUploader();
});

function initUploader(){
	$("#uploadify").uploadify({      
        'debug': false, //开启调试  
        'auto': true, //是否自动上传     
        'swf': '../jslib/uploadify/uploadify.swf',  //引入uploadify.swf    
        'uploader': '../json/Upload!uploadUserImg',//请求路径    
        'queueID': 'fileQueue',//队列id,用来展示上传进度的  
        'cancelImg': '../jslib/uploadify/uploadify-cancel.png',
        'width':'130',  //按钮宽度    
        'height':'30',  //按钮高度  
        'queueSizeLimit':1,//同时上传文件的个数    
        'fileTypeDesc':'支持格式:jpg/jpeg/png',//可选择文件类型说明
        'fileTypeExts':'*.jpg;*.png;*.gif', //控制可上传文件的扩展名
        'multi':false,  //允许多文件上传    
        'buttonText':'头像上传',//按钮上的文字    
        'fileSizeLimit':'2MB', //设置单个文件大小限制
        'fileObjName':'uploadify',  //<input type="file"/>的name    
        'method':'post',    
        'removeCompleted':true,//上传完成后自动删除队列    
        'onFallback':function(){      
            alert("您未安装FLASH控件，无法上传图片！请安装FLASH控件后再试。");
        },   
        'onUploadSuccess' : function(file, data, response){//单个文件上传成功触发    
        	//alert(data);
        	//data就是action中返回来的数据
        	var rsMap = eval("("+data+")");
        	if(rsMap.state){
        		$('#'+file.id).find('.data').html('　上传成功');
        		$("#user-photo").attr("src","../"+rsMap.data);
        		$("#userimg").val(rsMap.data);
        	}else{
        		$('#'+file.id).find('.data').html('　上传失败！'+rsMap.error);
        	}
        },'onQueueComplete' : function(){ //所有文件上传完成
            //alert("文件上传成功!");
        },'onUploadError':function(file, errorCode, errorMsg, errorString){
        	//$('#'+file.id).find('.data').html('　上传失败！上传图片大小尺寸不符合1440*400！');
        } 
    });
}

//页面加载完成事件
function onloadComplete(){
	initDataTable();
	loadPersonData();
	loadDeptData();
	loadDuty();
}
//初始化数据表格
function initDataTable(){
	$("#dg").datagrid({
		title:"人员信息列表",
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
		columns:[[
			{field:'tid',title:'编号',width:'40',hidden:true},
			{field:'headimg',title:'用户头像',width:'60',align:"center",formatter:function(value,row,index){
				return '<img src="../'+value+'" onerror="this.src=\'../images/people.png\'" style="height:36px;border:0px solid #AAA;margin-top:5px;"/>';
			}},
			{field:'name',title:'姓名',width:'70',align:"center"},
			{field:'department',title:'所属部门',width:'110',sortable:true,align:"center"},
			{field:'duty',title:'职务名称',width:'110',sortable:true,align:"center"},   
			{field:'telephone',title:'联系方式',width:'120',align:"center"},
			{field:'createtime',title:'创建时间',width:170,align:"center"},
			{field:'edittime',title:'修改时间',width:170,align:"center"}
		]]
	});
}
//加载职务字典数据
function loadDuty(){
	doAjax("../json/DataService!listAll",{method:"queryDicData",diccode:"DUTY"},{},function(rsMap,op){
		if(rsMap.state){
			var data = rsMap.data;
			$("#duty").empty();
			for(var i=0;i<data.length;i++){
				$("#duty").append('<option value="'+data[i].id+'">'+data[i].name+'</option>');
			}
		}else{
			
		}
	});
}
//加载数据
function loadDeptData(){
	//showLoading("正在加载数据，请稍候......");
	var param = {method:"queryDept"};
	doAjax("../json/DataService!listAll",param,{},function(rsMap,op){
		//hideLoading();
		if(rsMap.state){
			var data = rsMap.data;
			cache["treeData"]=data;
			initTree();
		}else{
			
		}
		//savelog("人员管理","查询数据",JSON.stringify(param));
	});
}

//创建树结构
function initTree(){
	var data = cache["treeData"];
	var treeData = getTreeChildren("0");
	$("#department").combotree("loadData",treeData);
}
//获取树节点孩子节点
function getTreeChildren(pcode){
	var treeArr = new Array();
	var treeData = cache["treeData"];
	var count = treeData.length;
	for(var i=0;i<count;i++){
		var record = treeData[i];
		if(record.pcode==pcode){
			var treeNode = new Object();
			treeNode.id = record.id;
			treeNode.code = record.code;
			treeNode.text = record.name;
			treeNode.pcode = record.pcode;
			treeNode.children = getTreeChildren(record.code);
			treeArr.push(treeNode);
		}
	}
	return treeArr;
}
//上传用户图片
function selectUserHeadImg(){
	var img_upload = document.getElementById("imgfile");
	img_upload.addEventListener("change",readImgFile,false);
	$("#imgfile").trigger("click");
}
//读取选择的图片
function readImgFile(){
	var that = this;
	lrz(that.files[0],{
        width: 400
	}).then(function(rst){
		var imgsrc = rst.base64;
		$("#user-photo").attr("src",imgsrc);
		Ajax.uploadUserPhoto(imgsrc,function(data){
			var imgsrc = data;
			$("#user-photo").attr("data",data);
			$("#user-photo").attr("src",basePath+imgsrc);
			savelog("人员管理","上传头像",basePath+imgsrc);
		});
	});
}

//加载人员数据
function loadPersonData(){
	var param = new Object();
	param.method = "getPerson";
	//showLoading("正在加载数据，请稍候----");
	$('#dg').datagrid('loading');
	doAjax("../json/DataService!listAll",param,{},function(rsMap,op){
		//hideLoading();
		$('#dg').datagrid('loaded');
		if(rsMap.state){
			var data = rsMap.data;
			var data = rsMap.data;
			cache["data"]=data;
			$('#dg').datagrid('loadData', data);
			//loadMenuTree();
		}
	});
	savelog("人员管理","查询数据",JSON.stringify(param));
}
//提交保存数据
function doSubmitAction(){
	var id = $("#id").val();
	var name = $("#name").textbox("getValue");
	var department = $("#department").combotree("getValue");
	var duty = $("#duty").val();
	var telephone = $("#telephone").textbox("getValue");
	var description = $("#description").textbox("getValue");
	var headimg = $("#userimg").val();
	//alert(name+","+department+","+duty+","+telephone+","+description+","+headimg);
	if(name==null||name==""){
		top.layer.alert("人员姓名不能为空！",{icon:0});
		//showSuccessMsg("opMessage","人员姓名不能为空！");
		return;
	}
	if(department==null||department==""){
		top.layer.alert("所属部门不能为空！",{icon:0});
		//showSuccessMsg("opMessage","所属部门不能为空！");
		return;
	}
	var param = {};
	param.name=name;
	param.deptcode = department;
	param.diccode = duty;
	param.telephone = telephone;
	param.description = description;
	param.headimg = headimg;
	param.method="addPerson";
	if(id!=null&&id!=""){
		param.method="editPerson";
		param.id=id;
	}
	doAjax("../json/DataService!saveData",param,{},
	function(rsMap,op){
		hideLoading();
		if(rsMap.state){
			var data = rsMap.data;
			if(parseInt(data,10)==1){
				$("#formWin").window("close");
				loadPersonData();
				top.layer.alert("人员信息数据保存成功！",{icon:6});
			}else{
				top.layer.alert("人员信息数据保存失败！",{icon:5});
			}
		}else{
			top.layer.alert("人员信息数据保存失败！"+rsMap.error,{icon:2});
		}
		if(param.method="addPerson"){
			savelog("人员管理","添加数据",JSON.stringify(param));
		}else{
			savelog("人员管理","修改数据",JSON.stringify(param));
		}
	});
}

function addAction(){
	$("#id").val("");
	$("#name").textbox("setValue","");
	//$("#department").combotree("getValue");
	//var duty = $("#duty").val();
	$("#telephone").textbox("setValue","");
	$("#description").textbox("setValue","");
	$("#headimg").val("images/user_head.png");
	$("#user-photo").attr("src","../images/user_head.png");
	$("#formWin").window("open");
}

function editAction(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.alert("请从下面列表中选择您要修改的数据！",{icon:0});
		//showSuccessMsg("opMessage","请从下面列表中选择您要修改的数据！");
	}else{
		$("#id").val(selectRow.id);
		$("#name").textbox("setValue",selectRow.name);
		$("#department").combotree("setValue",selectRow.deptcode);
		$("#duty").val(selectRow.diccode);
		$("#telephone").textbox("setValue",selectRow.telephone);
		$("#description").textbox("setValue",selectRow.description);
		if(selectRow.headimg==""||selectRow.headimg=="null"||selectRow.headimg==null){
			selectRow.headimg="images/user_head.png";
		}
		$("#userimg").val(selectRow.headimg);
		$("#user-photo").attr("src","../"+selectRow.headimg);
		$("#formWin").window("open");
	}
}

//删除数据
function deleteAction(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.alert("请从下面列表中选择您要删除的数据！",{icon:0});
		//showSuccessMsg("opMessage","请从下面列表中选择您要删除的数据！");
	}else{
		top.layer.confirm("确定要删除该数据吗？", function(index){
    		top.layer.close(index);
    		var param = new Object();
			param.method="deletePerson";
			param.id = selectRow.id;
			doAjax("../json/DataService!editData",param,{},function(rsMap,op){
				var data = rsMap.data;
				if(data>0){
					//showSuccessMsg("opMessage","人员信息删除成功！");
					loadPersonData();
					top.layer.alert("人员信息删除成功！",{icon:6});
				}else{
					top.layer.alert("人员信息删除失败！",{icon:5});
				}
				savelog("人员管理","删除数据",JSON.stringify(selectRow));
			});
    	});
	}
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