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
    {jqueryui_grid id="dg" url="/pas/ProjectMember/findAll.do" view="v.pm_project_member_user" }
    {jqueryui_dialog id="dlg" view="v.pm_project_member_user" }
    
    <div style="position: fixed; z-index: 10000; top: 0; left: 0; right: 0; bottom: 0; height: 100%; background-color: rgba(0, 0, 0, .3); display:none;" id="js-waiting">
        <img style="position: absolute; z-index: 1000; width: 80px; height 80px; left: 50%; top: 50%; margin-left: -40px; margin-top: -40px;" src="{$contextPath}/pas/images/loading.gif"/>
    </div>
</body>
</html>