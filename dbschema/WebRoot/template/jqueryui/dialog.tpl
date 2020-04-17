    <div id="{$id}" class="easyui-dialog" style="width:650px; height:480px;padding:10px 20px"
            closed="true" buttons="#{$id}-buttons">
        <div class="ftitle">{$title}</div>
        <form id="{$id}-form" method="post" novalidate>
		{foreach from=$metadata.fields item=field key=k}
		{if $field.type eq "hidden"}
        	<input type="{$field.type}" id="{$field.name}" name="{$field.name}">
		{elseif $field.type eq "password"}
            <div class="fitem" id="div-{$id}-{$field.name}">
                <label>{$field.title}:</label>
                <input type='password' id="{$field.name}" name="{$field.name}"  class="easyui-textbox" {if $field.required eq 'true'}required{/if} style="width:{$field.name | default:'300px'}">
            </div>
		{elseif $field.type eq "combobox"}
            <div class="fitem" id="div-{$id}-{$field.name}">
                <label>{$field.title}:</label>	
                <input id="{$field.name}" name="{$field.name}" style="cursor:hand;width:400px" class="easyui-combobox" {if $field.required eq 'true'}required{/if}
			        data-options="valueField:'{$field.valueField}',textField:'{$field.textField}',url:'{$field.url}',value:'{$field.value | default:''}',
				        onSelect: function(rec){  
		                    this.value=rec.id;
				        }" />
            </div>
		{elseif $field.type eq "combotree"}
            <div class="fitem" id="div-{$id}-{$field.name}">
                <label>{$field.title}:</label>
                <input id="{$field.name}" name="{$field.name}" style="cursor:hand;width:400px" class="easyui-combotree" {if $field.required eq 'true'}required{/if}
			        data-options="valueField:'{$field.valueField}',textField:'{$field.textField}',url:'{$field.url}',value:'{$field.value | default:''}',  
				        onSelect: function(rec){  
		                    this.value=rec.id;
				        }" />
            </div>
		{elseif $field.type eq "checkbox"}
            <div class="fitem" id="div-{$id}-{$field.name}">
                <label>{$field.title}:</label>
                {foreach from=$field.options item=option key=k}
                <span style='display:inline-block'><input type="checkbox" value="{$option.valueField}" name="{$field.name}" style="width:10px">{$option.textField}</span> &nbsp;
                {/foreach}
            </div>
        {elseif $field.type eq "radio"}
            <div class="fitem" id="div-{$id}-{$field.name}">
                <label>{$field.title}:</label>
                {foreach from=$field.options item=option key=k}
                <span style='display:inline-block'><input type="radio" value="{$option.value}" name="{$field.name}" style="width:10px">{$option.text}</span> &nbsp;
                {/foreach}
            </div>
		{elseif $field.type eq "textarea"}
            <div class="fitem" id="div-{$id}-{$field.name}">
                <label>{$field.title}:</label>
                <textarea id="{$field.name}" name="{$field.name}" class="easyui-validatebox" rows={$field.rows|default:'5'} cols={$field.rows|default:'50'} {if $field.required eq 'true'}required{/if}></textarea>
            </div>
        {elseif $field.type eq "datebox"}
            <div class="fitem" id="div-{$id}-{$field.name}">
                <label>{$field.title}:</label>
                <input id="{$field.name}" name="{$field.name}"  class="easyui-datebox" data-options="formatter:date_formatter,parser:date_parser" {if $field.required eq 'true'}required{/if} style="width:{$field.width | default:'75%'}" {if $field.readonly eq 'true'}editable=false{/if}>
            </div>
		{elseif $field.type neq "reference" and $field.isField neq "false"}
            <div class="fitem" id="div-{$id}-{$field.name}">
                <label>{$field.title}:</label>
                <input id="{$field.name}" name="{$field.name}"  class="easyui-textbox" {if $field.required eq 'true'}required{/if} style="width:{$field.width | default:'75%'}" {if $field.readonly eq 'true'}editable=false{/if}>
            </div>
		{/if}
		{/foreach}
        </form>
    </div>
    <div id="{$id}-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="saveOne('{$id}')" style="width:90px">保存</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#{$id}').dialog('close')" style="width:90px">取消</a>
    </div>