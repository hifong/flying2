package {$packageName};

import java.util.Date;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.common.util.Codes;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.service.AbstractService;
import com.flying.framework.model.ActionResult;
import com.flying.framework.model.QueryResult;

import {$parentPackage}.model.{$table.className};

@Service(value="{$table.className}{$postfix}", desc="{$table.comment}")
public class {$table.className}{$postfix} extends AbstractService{
        
	@MethodInfo("新增")
	@DaoCreate(entity={$table.className}.ENTITY_NAME)
	public {$table.className} create(
		{foreach from=$table.fields item=field key=k}
			@Param(value={$table.className}.{$field.fieldName | uppercase}, required={$field.required},	desc="{$field.comment}") {$field.dataType} {$field.fieldName}{if $k < $table.fieldCount - 1},{/if}{/foreach}
		
		) throws Exception {
		
		return null;
	}

	@MethodInfo("修改")
	@DaoUpdate(entity={$table.className}.ENTITY_NAME)
	public ActionResult update(
		{foreach from=$table.fields item=field key=k}
			@Param(value={$table.className}.{$field.fieldName | uppercase}, required={$field.required},	desc="{$field.comment}") {$field.dataType} {$field.fieldName}{if $k < $table.fieldCount - 1},{/if}{/foreach}
			
		) throws Exception {
		
		return null;
	}

	@MethodInfo("删除")
	@DaoRemove(entity={$table.className}.ENTITY_NAME)
	public ActionResult remove(
		{foreach from=$table.primaryFields item=field key=k}
			@Param(value={$table.className}.{$field.fieldName | uppercase}, required={$field.required},	desc="{$field.comment}") {$field.dataType} {$field.fieldName}{if $k < $table.primaryFieldCount - 1},{/if}{/foreach}
		
		) throws Exception {
		
		return null;
	}

	@MethodInfo("搜索")
	@DaoQuery(entity={$table.className}.ENTITY_NAME, pageable=true, modelClass={$table.className}.class)
	public QueryResult<{$table.className}> findAll(
			@Param(value=Codes.PAGE_NUM, 	required=false, desc="分页页号，默认0") int page,
			@Param(value=Codes.PAGE_SIZE, 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		return null;
	}

	@MethodInfo("查询")
	@DaoQuery(entity={$table.className}.ENTITY_NAME, single=true, throwsNotFoundException=true )
	public {$table.className} findById(
		{foreach from=$table.primaryFields item=field key=k}
			@Param(value={$table.className}.{$field.fieldName | uppercase}, required={$field.required},	desc="{$field.comment}") {$field.dataType} {$field.fieldName}{if $k < $table.primaryFieldCount - 1},{/if}{/foreach}
		) throws Exception {
		return null;
	}

}