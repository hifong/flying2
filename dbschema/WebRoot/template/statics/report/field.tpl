<table border="1" width="98%">
<tr>
	<td>排序</td>
	<td>字段名</td>
	<td>标题</td>
	<td>数据类型</td>
	<td>长度</td>
	<td>允许空</td>
	<td>备注</td>
</tr>
{foreach from=$rows item=r}
<tr>
	<td>{$r.sortOrder}</td>
	<td>{$r.fieldName}</td>
	<td>{$r.fieldTitle}</td>
	<td>{$r.dataType}</td>
	<td>{$r.dataLength}</td>
	<td>{$r.nullable}</td>
	<td>{$r.comment | default: "-"}</td>
</tr>
{/foreach}
</table>