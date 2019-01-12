/**
 * 
 */
var token = "Albedo-Requst-Token";
//执行ajax请求
function doAjax(url,param,op,callback){
	$.ajax({
		type:"POST",
		url:url,
		data:param,
		dataType:"json",
		beforeSend:function(xhr){
			xhr.setRequestHeader("token",token);
		},
		success: function(json){
			if(callback){
				callback(json,op);
			}
		},error:function(req,status,e){
			alert("调用ajax请求失败！"+e);
		}
	});
}
//记录用户操作日志
function savelog(moduleName,optype,opcontent){
	var param = {
		name:moduleName,
		optype:optype,
		opcontent:opcontent
	};
	var url = basepath+"service/api?action=savelog";
	$.ajax({
		type:"POST",
		url:url,
		data:param,
		dataType:"json",
		success: function(rsMap){
			
		},error:function(req,status,e){
			//alert("调用ajax请求失败！"+e);
		}
	});
}
//获取页面请求参数
function getPageUrlParam(key){
	var _pageurl = window.location.href;
	var param = {};
	var val = null;
	if(_pageurl.indexOf("?")!=-1){
		var pstring = _pageurl.split("?")[1];
		var tArr = pstring.split("&");
		var pArr = null;
		for(var i=0;i<tArr.length;i++){
			pArr = tArr[i].split("=");
			if(pArr[0]==key){
				val = pArr[1];
			}
		}
	}
	return val;
}

//获取url中的参数
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}
/**
 * 将form表单返回的数组对象转换JSON对象格式数据
 * @param formArray 表单数组
 * @returns JSON对象
 */
function formArrayToJson(formArray){
	var json = {};
	if(formArray != null && formArray.length>0){
		var key = null;
		var val = null;
		for(var i=0;i<formArray.length;i++){
			key = formArray[i].name;
			val = formArray[i].value;
			json[key] = val;
		}
	}
	return json;
}

//获取页面请求地址
var pageurl = window.location.href;
var index = pageurl.indexOf("/fhy/")+5*1;
//项目基地址
var basepath = pageurl.substring(0,index);

//根据字典分类加载字典数据
function getDicData(dictype,isNull){
	var count = dicdata.length;
	var data = [];
	if(isNull){
		data.push({id:0,val:"",text:"全部"})
	}
	for(var i=0;i<count;i++){
		if(dicdata[i].dictype==dictype){
			data.push({id:dicdata[i].id,val:dicdata[i].name,text:dicdata[i].name});
		}
	}
	return data;
}

//初始化下拉列表
function initCombobox(id,data,valueField,textField,changeEvent){
	$("#"+id).combobox({
		valueField:valueField,
		textField:textField,
		editable:false,
		onShowPanel:onShowComboPanel,
		onChange:changeEvent
	});
	$("#"+id).combobox("loadData",data);
	if(data.length>0){
		$("#"+id).combobox('setValue', data[0][valueField]);
	}
}
/**
//加载所有字典数据
function loadDicData(callback){
	doAjax(basepath+"service/api",{action:"loaddata",method:"getAllDic"},{},function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			cache["dicdata"]=data;
			localStorage.setItem("dicdata",JSON.stringify(data));
			if(callback!=null && typeof(callback)=="function"){
				callback(data);
			}
		}
	});
}
//根据字典ID获取字典名称
function getDicTextById(id){
	var jsonstr = localStorage.getItem("dicdata");
	var data = JSON.parse(jsonstr);
	var count = data.length;
	var record = null;
	var text = "";
	for(var i=0;i<count;i++){
		record = data[i];
		if(record.id==id){
			text = record.name;
			break;
		}
	}
	return text;
}

//根据字典分类加载字典数据
function getDicData(dictype){
	var jsonstr = localStorage.getItem("dicdata");
	var data = JSON.parse(jsonstr);
	var count = data.length;
	var dicdata = [];
	for(var i=0;i<count;i++){
		if(data[i].dictype==dictype){
			dicdata.push(data[i]);
		}
	}
	return dicdata;
}
//初始化数据字典项下拉列表
function initDicDataCombobox(id,dictype,valueField,textField,callback){
	var dicdata = getDicData(dictype);
	if(dicdata==null){
		dicdata = [];
	}
	$("#"+id).combobox({
		valueField:valueField,
		textField:textField,
		editable:false,
		onShowPanel:onShowComboPanel
	});
	$("#"+id).combobox("loadData",dicdata);
	if(dicdata.length>0){
		$("#"+id).combobox('setValue', dicdata[0][valueField]);
	}
}

//初始化下拉列表
function initCombobox(id,data,valueField,textField,callback){
	$("#"+id).combobox({
		valueField:valueField,
		textField:textField,
		editable:false,
		onShowPanel:onShowComboPanel
	});
	$("#"+id).combobox("loadData",data);
	if(data.length>0){
		$("#"+id).combobox('setValue', data[0][valueField]);
	}
}
*/
//加载部门信息
function loadDeptData(callback){
	var param = {method:"queryDept"};
	doAjax(basepath+"service/api",{action:"loaddata",method:"queryDept"},{},function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			//存入到缓存中
			localStorage.setItem("deptdata",JSON.stringify(data));
			if(callback!=null && typeof(callback)=="function"){
				callback(data);
			}
		}
	});
}
//加载车间数据
function loadCheJianList(isNull){
	var dataIdArr = user.dataright.split(",");
	var jsonstr = localStorage.getItem("deptdata");
	var data = [];
	if(jsonstr != null){
		data = JSON.parse(jsonstr);
	}
	var chejianPid = 9;
	var chejianArr = [];
	if(isNull){
		chejianArr.push({id:0,val:"",text:"全部"});
		for(var i=0;i<data.length;i++){
			if(data[i].pid==chejianPid){
				chejianArr.push({id:data[i].id,val:data[i].name,text:data[i].name});
			}
		}
	}else{
		for(var i=0;i<data.length;i++){
			if(data[i].pid==chejianPid && $.inArray(data[i].id,dataIdArr)!=-1){
				chejianArr.push({id:data[i].id,val:data[i].name,text:data[i].name});
			}
		}
	}
	return chejianArr;
}
//加载车间工区信息
function loadCheJianGongQu(chejianId,isNull){
	var jsonstr = localStorage.getItem("deptdata");
	var data = [];
	if(jsonstr != null){
		data = JSON.parse(jsonstr);
	}
	var gongquArr = [];
	if(isNull){
		gongquArr.push({id:0,val:"",text:"全部"});
	}
	for(var i=0;i<data.length;i++){
		if(data[i].pid==chejianId){
			gongquArr.push({id:data[i].id,val:data[i].name,text:data[i].name});
		}
	}
	return gongquArr;
}

//根据车间名称获取车间ID
function getCheJianIdByName(name){
	var jsonstr = localStorage.getItem("deptdata");
	var data = [];
	if(jsonstr != null){
		data = JSON.parse(jsonstr);
	}
	var chejianId = 0;
	for(var i=0;i<data.length;i++){
		if(data[i].name==name){
			chejianId = data[i].id;
			break;
		}
	}
	return chejianId;
}

/**
 * easyui-combo下拉控件设置自适应高度，最大高度为240
 */
function onShowComboPanel(){
	$(".combo-panel").css("height","auto");
	$(".combo-panel").css("max-height","240px");
}

/**
 * 按某属性进行排序
 * @param attr 对象中需要排序的字段
 * @param order 排序类型：asc-升序|desc-降序
 * @returns {Function}
 */
function sortByAtt(attr,order){
	return function(a,b){
		var v1 = parseInt(a[attr],10);
		var v2 = parseInt(b[attr],10);
		if(order=="asc"){
			return v1 - v2;
		}else if(order=="desc"){
			return v2 - v1;
		}
	}
}

var data = [{"name":"aaaaa","rank":"0"},{"name":"bbbbb","rank":"9"},{"name":"ccccc","rank":"2"},{"name":"ddddd","rank":"1"},{"name":"eeeee","rank":"10"}]

console.log(data.sort(sortByAtt("rank","asc")));

function DateUtil(){
	var SECOND = 1;//秒
	var MINUTE = 60;//分：60秒
	var HOUR = 60*60;//小时：3600秒
	var DATE = 60*60*24;//天
	//按指定格式格式化日期时间
	this.formatDate = function(format,d){
		var year = d.getFullYear();
		var month = d.getMonth()+1;
		var date = d.getDate();
		var hour = d.getHours();
		var minute = d.getMinutes();
		var second = d.getSeconds();
		month=month<10?"0"+month:month;
		date=date<10?"0"+date:date;
		hour=hour<10?"0"+hour:hour;
		minute=minute<10?"0"+minute:minute;
		second=second<10?"0"+second:second;
		var datetime = format.replace("yyyy",year);
		datetime = datetime.replace("MM",month);
		datetime = datetime.replace("dd",date);
		datetime = datetime.replace("HH",hour);
		datetime = datetime.replace("mm",minute);
		datetime = datetime.replace("ss",second);
		return datetime;
	}
	//将日期时间按指定格式转化成字符串
	this.date2String = function(formater,date){
		if(formater==null || formater==""){
			formater = "yyyy-MM-dd HH:mm:ss";
		}
		if(date==null || date==""){
			date = new Date();
		}
		return this.formatDate(formater,date);
	}
	//将当前日期时间按指定格式转化成字符串
	this.nowDate2String = function(formater){
		return this.formatDate(formater,new Date());
	}
	//将标准日期时间格式(yyyy-MM-dd HH:mm:ss)字符串转成日期对象
	this.dateString2Date = function(dateString){
		var year = parseInt(dateString.substring(0,4),10);
		var month = parseInt(dateString.substring(5,7),10)-1*1;
		var date = parseInt(dateString.substring(8,10),10);
		var hour = parseInt(dateString.substring(11,13),10);
		var minute = parseInt(dateString.substring(14,16),10);
		var second = parseInt(dateString.substring(17,19),10);
		var d = new Date(year,month,date,hour,minute,second);
		return d;
	}
	//将标准日期时间格式(yyyy-MM-dd HH)字符串转成日期对象
	this.dateString2Date2 = function(str){
		var yearMouthDay = str.substring(0,10);
		var hour = parseInt(str.substring(11,13),10);
		var date = yearMouthDay + " " + hour + ":00:00" ;
		return date ;
	}
	//通过秒计算时间差{dateString:标准时间字符串2013-08-07 12:00:00,amount:时间差数值[正数则是计算后面的时间，负数则是计算前面的时间]}
	this.calculateBySecond = function(dateString,amount){
		return this.calculate(dateString,SECOND,amount);
	}
	//通过分钟计算时间差{dateString:标准时间字符串2013-08-07 12:00:00,amount:时间差数值[正数则是计算后面的时间，负数则是计算前面的时间]}
	this.calculateByMinute = function(dateString,amount){
		return this.calculate(dateString,MINUTE,amount);
	}
	//通过小时计算时间差{dateString:标准时间字符串2013-08-07 12:00:00,amount:时间差数值[正数则是计算后面的时间，负数则是计算前面的时间]}
	this.calculateByHour = function(dateString,amount){
		return this.calculate(dateString,HOUR,amount);
	}
	//通过天计算时间差{dateString:标准时间字符串2013-08-07 12:00:00,amount:时间差数值[正数则是计算后面的时间，负数则是计算前面的时间]}
	this.calculateByDate = function(dateString,amount){
		return this.calculate(dateString,DATE,amount);
	}
	//通过指定的类型,时间差值计算时间差
	this.calculate = function(dateString,field,amount){
		var d = this.dateString2Date(dateString);
		var oldtime = d.getTime();
		var space = field*amount*1000;
		var newtime = oldtime + space;
		var newDate = new Date(newtime);
		return this.date2String("yyyy-MM-dd HH:mm:ss",newDate);
	}
	/** 比较两个日期时间大小,返回时间差
	* startDate:开始时间,标准时间字符串2013-08-07 12:00:00
	* endDate:结束时间,标准时间字符串2013-08-07 12:00:00
	* 返回两个时间的差,正数说明开始时间比结束时间小，反之比它大
	*/
	this.diffDateTime = function(startDate,endDate){
		var sDate = this.dateString2Date(startDate);
		var eDate = this.dateString2Date(endDate);
		var time = null;
		if(sDate!=null&&eDate!=null){
			time = eDate.getTime()-sDate.getTime();
		}
		return time;
	}
}

var dateUtil = new DateUtil();