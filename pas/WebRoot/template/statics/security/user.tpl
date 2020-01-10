<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>企业信息管理平台</title>

    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/easyui/themes/{$page_theme | default: 'default'}/easyui.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/style/my.css">
    
    <script type="text/javascript" src="{$contextPath}/{$Module.id}/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="{$contextPath}/{$Module.id}/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="{$contextPath}/{$Module.id}/javascript/my.js"></script>
</head> 
<body onpageshow="resetPagination('dg')">
    {jqueryui_grid id="dg" url="/pas/UserService/findByUsernameRealname.do" view="security.user.view" }
    {jqueryui_dialog id="dlg" view="security.user.view" }
    {jqueryui_dialog id="dlg2" action="savePassword" view="security.user.changepassword" }
</body>
</html>