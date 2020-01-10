{
	{foreach from=$tables item=table key=t}
	"view-{$table.tableName}":
	{
		"title":"{$table.comment}",
		"entity":"{$table.tableName}",
		"fields":
		[
			{foreach from=$table.fields item=field key=f}
			"{$field.fieldName}"{if $f < $table.fieldCount - 1},{/if}{/foreach}
		]
	}{if $t < $tableCount - 1},{/if}{/foreach}
}