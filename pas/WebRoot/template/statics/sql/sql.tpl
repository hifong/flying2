<html>
<body>
<form action="" method="post">
<table width="100%" border=0>
<tr>
	<td style="width:10%">Token:</td><td style="width:90%"><input name="token" style="width:80%" value="{$Request.token| default:''}"></td>
</tr>
<tr>
	<td>SQL:</td><td><textarea name="sql" rows=10 cols=10 style="width:80%">{$Request.sql| default:''}</textarea></td>
</tr>
</table>
<input type="submit" value="查询">
</form>
<hr>

{widget template="statics/sql/sql.result.tpl" moduleId="pas" serviceId="sql:execute" params="token=`$Request.token`;sql=`$Request.sql`"}

</body>
</html>