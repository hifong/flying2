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
    {jqueryui_grid id="dg" url="/project/ProjectService/findAll.do" view="v.pm_project" }
    {jqueryui_dialog id="dlg" view="v.pm_project" }
</body>
</html>