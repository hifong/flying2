{if $Msg }{$Msg}{/if}
{if $fields }
<table border="0" width="100%" cellpadding=1>
<thead>
	<tr bgcolor="#ccccff">
		{foreach from=$fields item=f}
		<td><strong>{$f}</strong></td>
		{/foreach}
	</tr>
</thead>
<tbody>
	{foreach from=$rows item=r key=key}
	<tr bgcolor="#eeeee">
		{foreach from=$fields item=f}
		<td>{$r.`$f`}</td>
		{/foreach}
	</tr>
	{/foreach}
</tbody>
</table>
{/if}