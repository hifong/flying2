package com.flying.dba.service;

import java.util.Date;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.common.util.Codes;
import com.flying.dba.model.Column;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.annotation.event.Event;
import com.flying.framework.annotation.event.Handler;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.model.ActionResult;
import com.flying.framework.model.ModelFactory;
import com.flying.framework.model.QueryResult;
import com.flying.framework.security.Principal;
import com.flying.framework.service.AbstractService;

@Service(value="ColumnService", desc="数据库字段",
events = {
		@Event(trigger="create",	handlers={@Handler(serviceId="ChangeLogService:createColumn")}),
		@Event(trigger="update",	handlers={@Handler(serviceId="ChangeLogService:updateColumn")})
})
public class ColumnService extends AbstractService{
    private ChangeLogService logService;
    public void setChangeLogService(ChangeLogService s) {
    	this.logService = s;
    }
        
	@MethodInfo("新增")
	@DaoCreate(entity=Column.ENTITY_NAME)
	public Column create(
			@Param(value=Column.TABLE_NAME, required=true,	desc="表名") String table_name,
			@Param(value=Column.FIELD_NAME, required=true,	desc="字段名") String field_name,
			@Param(value=Column.FIELD_TITLE, required=false,	desc="字段说明") String field_title,
			@Param(value=Column.DATA_TYPE, required=true,	desc="数据类型") String data_type,
			@Param(value=Column.DATA_LENGTH, required=false,	desc="长度") Integer data_length,
			@Param(value=Column.NULLABLE, required=false,	desc="允许空") String nullable,
			@Param(value=Column.SORT_ORDER, required=false,	desc="默认值") int sortOrder,
			@Param(value=Column.COMMENT, required=false,	desc="备注") String comment,
			@Param(value=Column.UPDATOR, required=false,	desc="修改人") String updator,
			@Param(value=Column.UPDATE_TIME, required=false,	desc="修改时间") Date update_time
		
		) throws Exception {
		Principal p = ServiceContext.getContext().getPrincipal();
		return ModelFactory.createModelInstance(Column.class, Column.UPDATOR, p.getName(), Column.UPDATE_TIME, new Date());
	}

	@MethodInfo("修改")
	@DaoUpdate(entity=Column.ENTITY_NAME)
	public ActionResult update(
		
			@Param(value=Column.COLUMN_ID, required=true,	desc="字段ID") Long column_id,
			@Param(value=Column.FIELD_TITLE, required=false,	desc="字段说明") String field_title,
			@Param(value=Column.DATA_TYPE, required=true,	desc="数据类型") String data_type,
			@Param(value=Column.DATA_LENGTH, required=false,	desc="长度") Integer data_length,
			@Param(value=Column.NULLABLE, required=false,	desc="允许空") String nullable,
			@Param(value=Column.SORT_ORDER, required=false,	desc="默认值") int sortOrder,
			@Param(value=Column.COMMENT, required=false,	desc="备注") String comment,
			@Param(value=Column.UPDATOR, required=false,	desc="修改人") String updator,
			@Param(value=Column.UPDATE_TIME, required=false,	desc="修改时间") Date update_time
			
		) throws Exception {
		Principal p = ServiceContext.getContext().getPrincipal();
		return ModelFactory.createModelInstance(ActionResult.class, Column.UPDATOR, p.getName(), Column.UPDATE_TIME, new Date());
	}

	@MethodInfo("删除")
	@DaoRemove(entity=Column.ENTITY_NAME)
	public ActionResult remove(
			@Param(value=Column.COLUMN_ID, required=true,	desc="字段ID") Long column_id
		) throws Exception {
		Principal p = ServiceContext.getContext().getPrincipal();
		this.logService.dropColumn(column_id, p.getName(), new Date());
		return null;
	}

	@SuppressWarnings("unchecked")
	@MethodInfo("搜索")
	@DaoQuery(entity=Column.ENTITY_NAME, pageable=true, modelClass=Column.class, osql="order by sort_order")
	public QueryResult<Column> findByTableName(
			@Param(value=Column.TABLE_NAME, required=true,	desc="表名") String table_name,
			@Param(value=Codes.PAGE_NUM, 	required=false, desc="分页页号，默认0") int page,
			@Param(value=Codes.PAGE_SIZE, 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		return ModelFactory.createModelInstance(QueryResult.class, 
				Codes.PAGE_NUM, page - 1);
	}

	@MethodInfo("搜索")
	@DaoQuery(entity=Column.ENTITY_NAME, pageable=true, modelClass=Column.class)
	public QueryResult<Column> findAll(
			@Param(value=Codes.PAGE_NUM, 	required=false, desc="分页页号，默认0") int page,
			@Param(value=Codes.PAGE_SIZE, 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		return null;
	}

	@MethodInfo("查询")
	@DaoQuery(entity=Column.ENTITY_NAME, single=true, throwsNotFoundException=true )
	public Column findById(
		
			@Param(value=Column.COLUMN_ID, required=true,	desc="字段ID") Long column_id
		) throws Exception {
		return null;
	}

	@MethodInfo("查询")
	@DaoQuery(entity=Column.ENTITY_NAME, single=true, throwsNotFoundException=true )
	public Column findByTableAndColumn(
			@Param(value=Column.TABLE_NAME, required=true,	desc="表名") String table_name,
			@Param(value=Column.FIELD_NAME, required=true,	desc="字段名") String field_name
		) throws Exception {
		return null;
	}

}