<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>企业信息管理平台</title>

    <link rel="stylesheet" type="text/css" href="{$contextPath}/pas/easyui/themes/{$page_theme | default: 'default'}/easyui.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/pas/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/pas/style/my.css">
    
    <script type="text/javascript" src="{$contextPath}/pas/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="{$contextPath}/pas/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="{$contextPath}/pas/javascript/my.js"></script>
    
</head> 
<body onpageshow="resetPagination('dg')">
    {jqueryui_grid id="dg" url="/project/ProjectCompileLogService/findAll.do" view="v.pm_project_compile_log" }
    {jqueryui_dialog id="dlg" view="v.pm_project_compile_log" }
</body>
    <script>
    field_formatters.state=function(value, row, index){
        if(!value) value ="";
		if(row.state == 1)
			return '<font color="green">成功</font>';
		else
			return '<font color="red">失败</font>';
    }
    field_formatters.url=function(value, row, index){
        if(!value) value ="";
		if(row.state == 1)
			return '<a target=\"_blank\" href=\"'+row.url+'\" >下载安装包</a>';
		else
			return "-";
    }
    field_formatters.info=function(value, row, index){
        if(!value) value ="";
        return '<a target=\"_blank\" href=\"'+row.info+'\" >查看编译详情</a>';
    }
    field_formatters.project_id=function(value, row, index){
        return row.projectName;
    }
    field_formatters.version_id=function(value, row, index){
        return row.versionName;
    }
    </script>
</html>