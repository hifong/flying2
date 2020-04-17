{foreach from=$rows item=r}
<p>
<hr>
<h3>{$r.tableTitle}({$r.tableName})</h3>
<h4>表信息</h4>
<table border="1" width="98%">
<tr>
	<td width="50%">是否分区：{$r.partitioned}</td><td width="50%">分区字段：{$r.partitionField | default:"--"}</td>
</tr>
<tr>
	<td colspan="2">备注：{$r.comment | default: "--"}</td>
</tr>
</table>
<h4>字段信息</h4>
{widget template="statics/report/field.tpl" serviceId="ColumnService:findByTableName" params="table_name=`$r.tableName`;rows=100"}
<h4>索引信息</h4>
{widget template="statics/report/index.tpl" serviceId="IndexService:findByTableName" params="table_name=`$r.tableName`"}
</p>
{/foreach}