/**
 * 显示提示信息
 * @param msg 提示信息
 * @param iconIndex 图标序号：0-警告|1-正确|2-错误|3-问号|4-锁|5-哭脸|6-笑脸
 * @param callback
 */
function showAlertMsg(msg,iconIndex,callback){
	msg = '<p style="font-size:16px;font-weight:bold;">'+msg+'</p>';
	layer.alert(msg,{title:'提示信息',icon:iconIndex,closeBtn:0,area:['600px','auto']},function(index){
		layer.close(index);
		if(callback!=null&&typeof(callback)=='function'){
			callback();
		}
	});
}

function showLayerLoading(msg){
	return layer.msg(msg, {icon: 16});
}
function hideLayerLoading(index){
	layer.close(index);
}
//显示进度条
function showloadLayer(msg){
    return layer.msg(msg,{icon:16,shade:[0.5,'#f5f5f5'],scrollbar:false,time:30000}) ;  
}
//关闭进度条
function hideloadLayer(index){
    layer.close(index);  
}
//显示提示信息
function showtipLayer(msg){
    layer.msg(msg,{time:1000});  
}
//弹出窗口
function openLayerWindow(title,shade,area,type,content,cancel_fun,success_fun){
	var layerIndex = 0;
	layerIndex = layer.open({
		type : type,
		shade : shade,
		title : [ title, 'height:40px;background:#0481D1;border-width:1px;color:#fff;font-weight:bold;' ],
		area : area,
		content : content,
		success : function(layero, index) {
			layerIndex = index;
			success_fun(index);
		},
		cancel:function(){
			cancel_fun(layerIndex);
		}
	});
	return layerIndex;
}