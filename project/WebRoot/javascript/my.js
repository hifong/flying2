var field_formatters = {};

function fieldFormatter(field) {
	return field_formatters[field];
}

function date_formatter(date){
	var y = date.getFullYear();
	var m = date.getMonth()+1;
	var d = date.getDate();
	return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
}
function date_parser(s){
	if (!s) return new Date();
	var ss = (s.split('-'));
	var y = parseInt(ss[0],10);
	var m = parseInt(ss[1],10);
	var d = parseInt(ss[2],10);
	if (!isNaN(y) && !isNaN(m) && !isNaN(d)){
		return new Date(y,m-1,d);
	} else {
		return new Date();
	}
}

function getDatagrid(dg) {
	return $('#'+dg);
}

function getDialog(dlg) {
	return $('#'+dlg);
}

function getForm(dlg) {
	return $('#'+dlg+'-form');
}

function getFieldDiv(dlg, field) {
	return document.getElementById("div-"+dlg+"-"+field);
}

function openActionDialog(dlg, title){
	if(title) {
    	getDialog(dlg).dialog('open').dialog('setTitle',title);
    }
    getForm(dlg).form('clear');
	var row = {};
	getForm(dlg).form('load',row);
}

function is_array(param) {
	if(Object.prototype.toString.call(param) == '[object Array]')
	    return true;
	else
	    return false;
}

//---------------------------------------------------------------------------------

function createOne(dialog, url, dgrid){
    var dlg = 'dlg';
    if(dialog) dlg = dialog;
    
	var dg = 'dg';
    if(dgrid) dg = dgrid;
    //
    getDialog(dlg).dialog('open').dialog('setTitle','新建');
    getForm(dlg).form('clear');
    getForm(dlg).attr('action', url);
    getForm(dlg).attr('dg', dg);
    var row = {};
    getForm(dlg).form('load',row);
}

function updateOne(dialog, url, dgrid){
	var dlg = 'dlg';
    if(dialog) dlg = dialog;
    
	var dg = 'dg';
    if(dgrid) dg = dgrid;
	
    var row = getDatagrid(dg).datagrid('getSelected');
    getForm(dlg).form('clear');
    if (row){
        getDialog(dlg).dialog('open').dialog('setTitle','修改用户信息');
        getForm(dlg).form('load',row);
        getForm(dlg).attr('action', url);
        getForm(dlg).attr('dg', dg);
    } else {
    	alert("请选择要修改的行！");
    }
}

function saveOne(dlg){
	var url = getForm(dlg).attr('action');
	var dg = getForm(dlg).attr('dg');
	
	$('#js-waiting').show();
    getForm(dlg).form('submit',{
        url: url,
        onSubmit: function(){
            return $(this).form('validate');
        },
        success: function(result){
            var result = eval('('+result+')');
            if (result.ReturnCode == 0){
                $.messager.show({
                    title: '提示',
                    msg: '操作成功！'
                });
                getDialog(dlg).dialog('close');
                $('#js-waiting').hide();
                reload(dg);
            } else {
                $('#js-waiting').hide();
                $.messager.show({
                    title: '错误',
                    msg: result.Msg
                });
            }
        }
    });
}

function removeOne(url, fields, dgrid){
	var dg = 'dg';
    if(dgrid) dg = dgrid;
    
    var row = getDatagrid(dg).datagrid('getSelected');
    if (row){            
        var params = {};
        if(fields) 
	        if(is_array(fields)) {
	            for(i=0;i< fields.length; i++) {
	                params[fields[i]]=row[fields[i]];
	            }
	        } else {
	            params[fields]=row[fields];
	        }
        else
        	params = row;
        
        $.messager.confirm('删除确认！','删除后数据不能恢复，确认删除么?',function(r){
            if (r){
				$('#js-waiting').show();
                $.post(url, params, function(result){
                    $('#js-waiting').hide();
            		if (result.ReturnCode == 0){
                        $.messager.show({
                            title: '提示',
                            msg: '数据删除成功！'
                        });
                        reload(dg);
                    } else {
                        $.messager.show({
                            title: '操作失败',
                            msg: result.Msg
                        });
                    }
                },'json');
            }
        });
    }
}

function handleOne(url, fields, dgrid){
	var dg = 'dg';
    if(dgrid) dg = dgrid;
    
    var row = getDatagrid(dg).datagrid('getSelected');
    if (row){            
        var params = {};
        if(fields) 
	        if(is_array(fields)) {
	            for(i=0;i< fields.length; i++) {
	                params[fields[i]]=row[fields[i]];
	            }
	        } else {
	            params[fields]=row[fields];
	        }
        else
        	params = row;
        
        $.messager.confirm('处理确认！','处理后数据不能恢复，请确认?',function(r){
            if (r){
				$('#js-waiting').show();
                $.post(url, params, function(result){
                    $('#js-waiting').hide();
            		if (result.ReturnCode == 0){
                        $.messager.show({
                            title: '提示',
                            msg: '服务处理成功！'
                        });
                        reload(dg);
                    } else {
                        $.messager.show({
                            title: '服务处理失败',
                            msg: result.Msg
                        });
                    }
                },'json');
            }
        });
    }
}

function reload(dg, queryParams) {
    if("tree" == getDatagrid(dg).attr("gridtype")) {
        getDatagrid(dg).treegrid('reload', queryParams);
    } else {
        getDatagrid(dg).datagrid('reload', queryParams);
    }
}


function search(dg) {
	var queryForm = document.getElementById(dg+'_query_form');
	var dgrid = getDatagrid(dg);
	
	var queryParams = {};
	var inputs = $("input[field="+dg+"_query]");
	console.log(inputs);
	for(var i=0; i< inputs.length; i++) {
		if(inputs[i].value != '') {
			console.log(inputs[i].id + '=' + inputs[i].value );
			queryParams[inputs[i].id] = inputs[i].value;
		}
	}
	reload(dg, queryParams);
}

//------------------------------------------------

function resetPagination(dg){
	if(!dg) dg = 'dg';
	var p = getDatagrid(dg).datagrid('getPager');
	$(p).pagination({             
		pageSize: 15,           
 		pageList: [15,30],
		beforePageText: '第',      
		afterPageText: '页    共 {pages} 页',             
		displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录'                  
	}); 
}