$(function(){
	$("#tbForm td").css("padding", "10px");
	$("#dicForm td").css("padding", "10px");
	loadDic();
});

//加载数据
function loadDic(){
	var param = {};
	doAjax("../json/Admin!getDicType",{},{},function(json,op){
		if(json.code==0){
			var data = json.data;
			cache["dic_data"]=data;
			$("#dg").datagrid("loadData",data);
			$("#dg").datagrid("selectRow",0);
			$("#dicdata_diccode").val(data[0].code);
			getDicdata(data[0].code);
		}
	});
}
//点击字典获取字典明细数据
function onClickRow(index, row){
	var diccode = row.code;
	getDicdata(diccode);
}
//加载字典明细
function getDicdata(diccode){
	$("#dicdataWin").window("close");
	$("#dicdata_diccode").val(diccode);
	var param = {code:diccode};
	doAjax("../json/Admin!getDicData",param,{},
	function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			cache["dicdata_data"]=data;
			$("#dglist").datagrid("loadData",data);
			var code = diccode+"-1";
			if(data.length>0){
				var lastdata = data[data.length-1];
				var maxno = lastdata.code.split("-")[1];
				maxno = parseInt(maxno) + 1;
				code = diccode+"-"+maxno;
			}
			$("#dicdata_code").val(code);
		}
	});
}

//保存字典明细数据
function saveOrUpdateDicData(){
	var id = $("#dicdata_id").val();
	var diccode = $("#dicdata_diccode").val();
	var code = $("#dicdata_code").textbox('getValue');
	var name = $("#dicdata_name").textbox('getValue');
	var rank = $("#dicdata_rank").numberbox('getValue');
	if(name == null|| name == ""){
		top.layer.msg("字典名称不能为空！");
		return;
	}
	var param = {};
	param.action="saveOrUpdate";
	param.method="addDicData";
	var url = "../json/Admin!saveDicData";
	if(id!=null&&id!=""){
		param.method="editDicData";
		param.id=id;
		url = "../json/Admin!editDicData";
	}else{
		isExist = checkExist(cache["dicdata_data"],"name",name);
		if(isExist){
			top.layer.msg("字典明细名称【"+name+"】已存在！");
			return;
		}
	}
	param.diccode = diccode;
	param.code = code;
	param.name = name;
	param.rank = rank;
	doAjax(url,param,{},
	function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			if(parseInt(data,10)==1){
				$("#dicdataWin").window("close");
				top.layer.msg("字典数据保存成功！",{icon:6});
				getDicdata(diccode);
				cacheDicData();
			}else{
				top.layer.msg("字典数据保存失败！",{icon:5});
			}
		}else{
			top.layer.msg("字典数据保存失败！",{icon:5});
		}
	});
}
//点击添加字典明细事件
function addDicAction(){
	$("#dicdata_id").val("");
	$("#dicdata_code").textbox("setValue","");
	$("#dicdata_name").textbox("setValue","");
	var data = cache["dicdata_data"];
	if(data!=null){
		$("#dicdata_rank").val(data.length+1*1);
	}
	$("#dicdataWin").window("open");
}
//点击编辑字典明细事件
function editDicAction(){
	var selectRow = $("#dglist").datagrid("getSelected");
	if(selectRow==null){
		top.layer.msg("请从列表中选择您要修改的数据！");
	}else{
		$("#dicdata_id").val(selectRow.id);
		$("#dicdata_diccode").val(selectRow.diccode);
		$("#dicdata_code").textbox('setValue',selectRow.code);
		$("#dicdata_name").textbox('setValue',selectRow.name);
		$("#dicdata_rank").numberbox('setValue',selectRow.rank);
		$("#dicdataWin").window("open");
	}
}
//点击删除字典明细事件
function removeDicAction(){
	var selectRow = $("#dglist").datagrid("getSelected");
	if(selectRow==null){
		top.layer.msg("请从列表中选择您要删除的数据！");
	}else{
		top.layer.confirm('确定要删除该字典数据吗？',{icon: 3, title:'提示'}, function(index){
			top.layer.close(index);
			var param = new Object();
			param.action="saveOrUpdate";
			param.method="deleteDicData";
			param.id = selectRow.id;
			doAjax("../json/Admin!deleteDicData",param,{},function(rsMap,op){
				var data = rsMap.data;
				if(data>0){
					top.layer.msg('数据删除成功！',{icon:6});
					var diccode = $("#dicdata_diccode").val();
					getDicdata(diccode);
					cacheDicData();
				}
			});
		});
	}
}
//检查是否有重复的数据
function checkExist(data,attr,value){
	var isExist = false;
	var count = data.length;
	var rcd = null;
	for(var i=0;i<count;i++){
		rcd = data[i];
		if(rcd[attr]==value){
			isExist = true;
			break;
		}
	}
	return isExist;
}
//缓存数据字典信息
function cacheDicData(){
	return;
	var param = new Object();
	doAjax("../json/Admin!cacheDic",param,{},function(rsMap,op){});
}