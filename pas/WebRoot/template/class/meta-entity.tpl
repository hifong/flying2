{
	{foreach from=$tables item=table key=t}
	"{$table.tableName}":
	{
		"title":"{$table.comment}",
		"table-name":"{$table.tableName}",
		"primary-key":"{$table.primaryFieldNames}",
		"fields":
		[
			{foreach from=$table.fields item=field key=f}
			{
				"name": "{$field.fieldName}",
				"title": "{$field.comment}",
				{if $field.primaryKey}"generator": "{$field.generator}",{/if}
				"maxLength": {$field.maxLength},
				"required": {$field.required}
			}{if $f < $table.fieldCount - 1},{/if}{/foreach}
		]
	}{if $t < $tableCount - 1},{/if}{/foreach}
}