<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
    <title>企业信息管理平台</title>
    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/easyui/themes/{$page_theme | default: 'default'}/easyui.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/style/my.css">
    
    <script type="text/javascript" src="{$contextPath}/{$Module.id}/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="{$contextPath}/{$Module.id}/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="{$contextPath}/{$Module.id}/javascript/my.js"></script>
    
</head>
<body>
	<div class="easyui-panel" style="padding:5px">
      <ul class="easyui-tree">
        {widget template="statics/include/menu.tpl" moduleId="pas" serviceId="MenuService:findMyMenu" params="parent_id=0"}
	  </ul>
	</div>

</body>
</html>