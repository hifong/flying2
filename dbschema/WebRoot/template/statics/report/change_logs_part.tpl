{foreach from=$rows item=r}
<hr>
<h4>{$r.tableName}</h4>
<table border="1" width="98%">
<tr>
	<td>ID</td>
	<td>表名</td>
	<td>字段名</td>
	<td>操作</td>
	<td>变更内容</td>
	<td>SQL</td>
	<td>变更人</td>
	<td>变更时间</td>
</tr>
{foreach from=$r.logs item=l}
<tr>
	<td>{$l.logId}</td>
	<td>{$l.tableName}</td>
	<td>{$l.fieldName}</td>
	<td>{$l.actionType}</td>
	<td>{$l.content}</td>
	<td>{$l.sql}</td>
	<td>{$l.updator}</td>
	<td>{$l.updateTime}</td>
</tr>
{/foreach}
</table>
{/foreach}