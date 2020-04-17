<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/pas/easyui/themes/{$page_theme | default: 'default'}/easyui.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/pas/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/pas/style/my.css">
    
    <script type="text/javascript" src="{$contextPath}/pas/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="{$contextPath}/pas/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="{$contextPath}/pas/javascript/my.js"></script>
    
</head> 
<body onpageshow="resetPagination('dg')">
    {jqueryui_grid id="dg" url="/dba/TableService/findAll.do" view="table.view" }
    {jqueryui_dialog id="dlg" view="table.view" }
    {jqueryui_dialog id="dlg2" view="table.view2" }
    
    <script>
    function showColumns() {
	    var row = getDatagrid('dg').datagrid('getSelected');
	    if(row) {
	    	window.parent.openTab('字段:'+row.table_title,'/dba/admin/column.list.shtml?table_name='+row.table_name);
	    } else {
			alert('没有选择数据表！');
	    }
    }
    function showIndexes() {
	    var row = getDatagrid('dg').datagrid('getSelected');
	    if(row) {
	    	window.parent.openTab('索引:'+row.table_title,'/dba/admin/index.list.shtml?table_name='+row.table_name);
	    } else {
			alert('没有选择数据表！');
	    }
    }
    function showChangeLogs() {
	    var row = getDatagrid('dg').datagrid('getSelected');
	    if(row) {
	    	window.parent.openTab('变更:'+row.table_title,'/dba/admin/change_log.list.shtml?table_name='+row.table_name);
	    } else {
			alert('没有选择数据表！');
	    }
    }
    </script>
</body>
</html>