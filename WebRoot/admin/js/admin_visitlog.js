$(function(){
	initDataGrid();
});

//初始化表格,获取用户操作日志
function initDataGrid(){
	$("#dg").datagrid({
		border:false,
		url:"../service/api?action=visitlog",
		title:"系统访问日志",
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		nowrap:true,
		fit:true,
		pagination:true,
		pageNumber:1,
		//toolbar:'#tb',
		loadMsg:"正在加载数据，请稍候……",
		columns:[[    	       
		    {field:'ip',title:'访问IP地址',width:'120',align:'center'},
		    {field:'sessionid',title:'会话ID',width:'320',align:'center'},
			{field:'createtime',title:'访问时间',width:'190',align:'center'},
			{field:'leavetime',title:'离开时间',width:'190',align:'center'}
		]],
		onClickRow:function(index, row){
			showMoreInfo(row);
		}
	});
}