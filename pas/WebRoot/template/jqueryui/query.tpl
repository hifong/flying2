{if $metadata.conditions }
 	<div class="easyui-panel" title="查询条件" style="width:100%" data-options="collapsible:true">
 	<form name="{$id}_query_form" id="{$id}_query_form">
		{foreach from=$metadata.conditions item=field key=k}
    	<div class="fitem" style="display:inline-block; width:46%; padding:5px">
        	<label style="width:20%;text-align:right;">{$field.title}：</label>
    		{if $field.type eq "combobox"}
            <input field="{$id}_query" id="{$field.name}" name="{$field.name}" style="cursor:hand" class="easyui-combobox" {if $field.required eq 'true'}required{/if}
		        data-options="   
			        valueField: '{$field.valueField}',   
			        textField: '{$field.textField}', 
			        value:'{$field.value | default:''}',
			        url: '{$field.url}',   
			        onSelect: function(rec){  
	                    this.value=rec.id;
			        }" />
			{elseif $field.type eq "combotree"}
            <input field="{$id}_query" id="{$field.name}" name="{$field.name}" style="cursor:hand" class="easyui-combotree" {if $field.required eq 'true'}required{/if}
		        data-options="   
			        valueField: '{$field.valueField}',   
			        textField: '{$field.textField}', 
			        value:'{$field.value | default:''}',
			        url: '{$field.url}',   
			        onSelect: function(rec){  
	                    this.value=rec.id;
			        }" />
			{elseif $field.type eq "checkbox"}
            {foreach from=$field.options item=option key=k}
            <span style='display:inline-block'><input type="checkbox" value="{$option.valueField}" name="{$field.name}" style="width:10px">{$option.textField}</span> &nbsp;
            {/foreach}
			{else}
			<input field="{$id}_query" id="{$field.name}" name="{$field.name}"  class="easyui-textbox" style="width:75%" >
			{/if}
    	</div>
    	{/foreach}
    	<div class="fitem" style="padding:5px; text-align:center;">
    		<input type="button" value="确认查询" onclick="search('{$id}')">
    	</div>
    </form>
    </div>
 {/if}
 