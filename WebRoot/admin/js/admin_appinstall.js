$(function(){
	initDataGrid();
});

//初始化表格
function initDataGrid(){
	$("#dg").datagrid({
		border:false,
		url:"../json/Admin!getAppInstall",
		title:"App手机安装信息",
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		nowrap:true,
		fit:true,
		pagination:true,
		pageNumber:1,
		pageSize:10,
		//toolbar:'#tb',
		loadMsg:"正在加载数据，请稍候……",
		columns:[[    	       
		    {field:'deviceid',title:'设备编号',width:'200',align:'center'},
		    {field:'brand',title:'设备品牌',width:'220',align:'center'},
			{field:'model',title:'设备型号',width:'190',align:'center'},
			{field:'osversion',title:'操作系统',width:'190',align:'center'},
			{field:'createtime',title:'安装时间',width:'190',align:'center'}
		]],
		onClickRow:function(index, row){
			//showMoreInfo(row);
		}
	});
}