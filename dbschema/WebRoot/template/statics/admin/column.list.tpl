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
    {jqueryui_grid id="dg" url="/dba/ColumnService/findByTableName.do?table_name=`$Request.table_name`" view="column.view" }
    {jqueryui_dialog id="dlg" view="column.view" }
    
    <script>
    var table_name='{$Request.table_name}';
    {literal}
    action_prehandlers.createOne = function() {
    	return {"table_name":table_name};
    }
    {/literal}
    </script>
</body>
</html>