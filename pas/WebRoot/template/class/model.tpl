package {$packageName};

import java.util.Date;

import com.flying.framework.data.Data;
import com.flying.framework.annotation.Param;
import com.flying.framework.data.DataSerializable;
import com.flying.framework.metadata.Meta;

@SuppressWarnings("serial")
@Meta(id="{$table.tableName}",title="{$table.comment}", table="{$table.tableName}", primaryKeys={literal}{{/literal}{foreach from=$table.primaryFields item=field key=k}"{$field.fieldName}"{if $k < $table.primaryFieldCount - 1},{/if}{/foreach}{literal}}{/literal})
public class {$table.className}{$postfix} extends Data implements DataSerializable {
	public final static String ENTITY_NAME = "{$table.tableName}";
	{foreach from=$table.fields item=field key=k}
	public final static String {$field.fieldName | uppercase} = "{$field.fieldName}";{/foreach}
	
	{foreach from=$table.fields item=field key=k}
	@Param(value={$field.fieldName | uppercase},   required={$field.required},   maxlength={$field.maxLength},	desc="{$field.comment}" {if $field.primaryKey}, generator="{$packageName}.{$table.className}{$postfix}.{$field.propertyName}"{/if})
	private {$field.dataType} {$field.propertyName};
	{/foreach}
	{foreach from=$table.fields item=field key=k}
	public {$field.dataType} get{$field.propertyName | capitialize}() {
		return this.{$field.propertyName};
	}
	
	public void set{$field.propertyName | capitialize}({$field.dataType} {$field.propertyName}) {
		this.{$field.propertyName} = {$field.propertyName};
	}
	{/foreach}

}