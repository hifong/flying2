<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1"/>

    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/easyui/themes/{$page_theme | default: 'default'}/easyui.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/easyui/css/style.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/style/my.css">
    
    <script type="text/javascript" src="{$contextPath}/{$Module.id}/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="{$contextPath}/{$Module.id}/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="{$contextPath}/{$Module.id}/javascript/jquery.cookie.js"></script>

    <title>风华正茂资产管理平台</title>
    {literal}
    <script>
		function openTab(title,href){  
		    var e = $('#tabs').tabs('exists',title);  
		    if(e){  
		        $("#tabs").tabs('select',title);  
		  
		        var tab = $("#tabs").tabs('getSelected');  
		        $('#tabs').tabs('update',{  
		            tab:tab,  
		            options:{  
		                title:title,  
		                height:'100%',
		                content:'<iframe name="indextab" scrolling="auto" src="'+href+'" frameborder="0" style="width:100%;height:100%;"></iframe>',  
		                closable:true,  
		                selected:true  
		            }  
		        });  
		    }else{  
		        $('#tabs').tabs('add',{  
		            title:title,  
		            height:'100%',
		            content:'<iframe name="indextab" scrolling="auto" src="'+href+'" frameborder="0" style="width:100%;height:100%;"></iframe>',  
		            iconCls:'',  
		            closable:true  
		        });  
		    }  
		}
		function selectTheme() {
		  var theme = $('#themes').val();
		  $.cookie('page_theme', theme, { expires: 7, path: '/' });
		  window.location.reload();
		}
    </script>
    {/literal}
</head>
<body class="easyui-layout" data-options="fit:true">
	<script>
	    if (self.location != top.location) {
    		top.location = self.location;
    	}
	</script>
    <div class="easyui-layout" style="width:100%;height:100%;">
		<div data-options="region:'north'" style="height:80px">
			<h1 valign="middle"><img style="width:30;height:30" src="{$contextPath}/{$Module.id}/images/logo.jpg">风华正茂资产管理平台</h1>
			<div class="topInfo">
				<span style="padding:5px">欢迎您：{$principal.name} </span>&nbsp;&nbsp;
				<a href="/logout.jsp?url={$contextPath}/{$Module.id}/login.shtml">注销</a>  | 
				<a href="{$contextPath}/{$Module.id}/security/changePwd.shtml">修改密码</a>
			</div>
			<div class="topNav">
				选择风格：
				<select name="themes" id="themes" onchange="selectTheme()">
                    <option value="default" {if $page_theme eq 'default'}selected{/if}>default</option>
			        <option value="black" {if $page_theme eq 'black'}selected{/if}>black</option>
                    <option value="bootstrap" {if $page_theme eq 'bootstrap'}selected{/if}>bootstrap</option>
                    <option value="gray" {if $page_theme eq 'gray'}selected{/if}>gray</option>
                    <option value="material" {if $page_theme eq 'material'}selected{/if}>material</option>
                    <option value="metro" {if $page_theme eq 'metro'}selected{/if}>metro</option>
			   </select>
			</div>
		</div>
		<div data-options="region:'west',split:true" title="菜单" style="width:250px;">
			<iframe src="left.shtml" style="width:100%; height:100%" scrolling="auto" frameborder="0" style="z-index:-1" ></iframe>
		</div>
		<div data-options="region:'center',iconCls:'icon-ok'" style="padding:2px">
		
			<div id="tabs" name="tabs" class="easyui-tabs" style="width:100%; height:100%">
				<div title="我的首页" style="padding:10px; height:100%">
					<iframe src="home.shtml"  id="iframepage"  style="width:100%; height:100%" frameborder="0" name="main"></iframe>
				</div>
			</div>
			
		</div>
	</div>
</body>
