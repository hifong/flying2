<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>企业信息管理平台</title>
    <link rel="stylesheet" type="text/css" href="easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="easyui/themes/color.css">
    <script type="text/javascript" src="easyui/jquery.min.js"></script>
    <script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
</head>
<body>
    <h2>栏目管理</h2>
    <p>管理网站栏目.</p>
	<hr>
    
    <table id="dg" title="所有栏目" class="easyui-datagrid" style="width:99%;height:400px"
            url="category/findAll.do"
            toolbar="#toolbar" pagination="true"
            rownumbers="true" fitColumns="true" singleSelect="true">
        <thead>
            <tr>
                <th field="parent_id" width="50">上级</th>
                <th field="category_id" width="40">ID</th>
                <th field="code" width="50">代码</th>
                <th field="name" width="50">名称</th>
                <th field="desc" width="250">说明</th>
                <th field="sort_id" width="30" align="right">排序</th>
            </tr>
        </thead>
    </table>
    <div id="toolbar">
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="createOne()">新建</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="updateOne()">修改</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="removeOne()">删除</a>
    </div>
    
    <div id="dlg" class="easyui-dialog" style="width:600px;height:450px;padding:10px 20px"
            closed="true" buttons="#dlg-buttons">
        <div class="ftitle">栏目信息</div>
        <form id="fm" method="post" novalidate>
            <div class="fitem">
                <label>上级:</label>
				<select name="parent_id" id="parent_id" class="easyui-combotree" style="width:200px;"
						data-options="url:'category/findChildrenTree.do?parent_id=0',required:false">
				</select>
            </div>
            <div class="fitem">
                <label>代码:</label>
                <input name="code" class="easyui-textbox" required="true" width="200">
            </div>
            <div class="fitem">
                <label>名称:</label>
                <input name="name" class="easyui-textbox" required="true" style="width:300px">
            </div>
            <div class="fitem">
                <label>说明:</label>
                <input name="desc" class="easyui-textbox" multiline="true" style="width:300px;height:180px">
            </div>
            <div class="fitem">
                <label>排序:</label>
                <input name="sort_id" class="easyui-numberbox">
            </div>
        </form>
    </div>
    <div id="dlg-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="saveOne()" style="width:90px">保存</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">取消</a>
    </div>
    {literal}
    <script type="text/javascript">
        var url;
        function createOne(){
            $('#dlg').dialog('open').dialog('setTitle','新增');
            $('#fm').form('clear');
            url = 'category/create.do';
        }
        function updateOne(){
            var row = $('#dg').datagrid('getSelected');
            if (row){
                $('#dlg').dialog('open').dialog('setTitle','修改');
                $('#fm').form('load',row);
                url = 'category/update.do?category_id='+row.category_id;
            }
        }
        function saveOne(){
            $('#fm').form('submit',{
                url: url,
                onSubmit: function(){
                    return $(this).form('validate');
                },
                success: function(result){
                    var result = eval('('+result+')');
                    if (result.errorMsg){
                        $.messager.show({
                            title: 'Error',
                            msg: result.errorMsg
                        });
                    } else {
                        $('#dlg').dialog('close');        // close the dialog
                        $('#dg').datagrid('reload');    // reload the user data
						$('#parent_id').combotree('reload');
                    }
                }
            });
        }
        function removeOne(){
            var row = $('#dg').datagrid('getSelected');
            if (row){
                $.messager.confirm('Confirm','删除后数据不能恢复，确认删除么?',function(r){
                    if (r){
                        $.post('category/remove.do',{category_id:row.category_id},function(result){
                            if (result.success){
                                $('#dg').datagrid('reload');    // reload the user data
								$('#parent_id').combotree('reload');
                            } else {
                                $.messager.show({    // show error message
                                    title: 'Error',
                                    msg: result.errorMsg
                                });
                            }
                        },'json');
                    }
                });
            }
        }
    </script>
    <style type="text/css">
        #fm{
            margin:0;
            padding:10px 30px;
        }
        .ftitle{
            font-size:14px;
            font-weight:bold;
            padding:5px 0;
            margin-bottom:10px;
            border-bottom:1px solid #ccc;
        }
        .fitem{
            margin-bottom:5px;
        }
        .fitem label{
            display:inline-block;
            width:80px;
        }
        .fitem input{
            width:160px;
        }
    </style>
    {/literal}
</body>
</html>