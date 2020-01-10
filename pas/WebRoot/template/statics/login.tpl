<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1"/>
    <title>风华正茂投资管理平台</title>
    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/easyui/themes/{$page_theme | default: 'default'}/easyui.css">
	<link rel="stylesheet" type="text/css" href="{$contextPath}/pas/easyui/themes/icon.css">
	<link rel="stylesheet" type="text/css" href="{$contextPath}/pas/easyui/themes/color.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/pas/easyui/css/style.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/style/login.css">
    <script type="text/javascript" src="{$contextPath}/pas/easyui/jquery.min.js"></script>
	<script type="text/javascript" src="{$contextPath}/pas/easyui/jquery.easyui.min.js"></script>
</head>
<body>

    {literal}
  <div id="header" >
    <div class="logo">
      <strong>风华正茂投资管理平台</strong>
    </div>
  </div>
  <div id="picture" style="background:url(images/index.jpg) no-repeat; background-size: cover;">
  <div data-options=" region:'east',split:true,style:{position:'absolute',right:50,top:150}" class="easyui-panel " title="用户登录" style="width:400px;text-align: center;">
      <div style="padding:10px 60px 20px 60px">
        <form id="ff" class="easyui-form" method="post" data-options="novalidate:true">
          <table cellpadding="5">
            <tr>
              <td style="width:30%;height:32px">&nbsp;用户名：</td>
              <td><input id="username" name="username" class="easyui-textbox" data-options="prompt:'账号',validType:'username'" iconCls="icon-man" iconAlign=left style="width:100%;height:32px"/></td>
            </tr>
            <tr>
              <td>&nbsp;密码：</td>
              <td><input type="password" id="password" name="password" class="easyui-textbox" data-options="prompt:'密码',validType:'password'" iconCls="icon-lock" iconAlign=left style="width:100%;height:32px"></input></td>
            </tr>
          </table>
        </form>
        <div style="text-align:center;padding:5px; ">
          <a href="javascript:login()" rel="external nofollow" rel="external nofollow" class="easyui-linkbutton" style="width:45%;height:32px">登录</a>    
          <a href="javascript:clearForm()" rel="external nofollow" rel="external nofollow" class="easyui-linkbutton" style="width:45%;height:32px">清空</a>
        </div>
      </div>
    </div>
  </div>
  <div id="bootom">
    <div id="bootom_content">
      <p><strong>关于我们      法律声明      服务条款     联系我们</strong></p>
      <p> 
        地址：广州开发区    邮箱：hifong@outlook.com
           Copyright © 2017 - 2018    hifong@outlook.com 版权所有
      </p>
      <p>
        建议使用IE8以上版本浏览器    E-mail：hifong@outlook.com
      </p>
    </div>
  </div>
    {/literal}

    <script type="text/javascript">
    {literal}
		function clearForm(){
			$('#ff').form('clear');
		}
		function login() {
			var username = document.getElementById("username").value;
			var password = document.getElementById("password").value;
			if(username =='' || password == '') {
				alert('请输入用户名和密码！');
				return;
			}
			$.ajax({
				type: 'POST',
				url: '/pas/UserService/findByUsernamePassword.do',
				//contentType:'application/json;charset=utf-8',
				//data: JSON.stringify(jsondata),
				data: {username: username, password: password},
				success: function(res){
					if(res.user_id)
						window.location.href = '/pas/main.shtml';
					else
						alert("用户名或密码错误！");
				},
				dataType: 'json'
			});
		}
    {/literal}
    </script>
</body>
</html>