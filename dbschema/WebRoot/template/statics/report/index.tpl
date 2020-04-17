<table border="1" width="98%">
<tr>
	<td>索引名称</td>
	<td>索引类型</td>
	<td>数据表</td>
	<td>数据字段</td>
	<td>唯一性</td>
</tr>
{foreach from=$rows item=r}
<tr>
	<td>{$r.indexName}</td>
	<td>{$r.indexType}</td>
	<td>{$r.tableName}</td>
	<td>{$r.columnName}</td>
	<td>{$r.uniqueness}</td>
</tr>
{/foreach}
</table>