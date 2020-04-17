package com.flying.dba.service;

import java.util.Date;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.common.util.Codes;
import com.flying.dba.model.Table;
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

@Service(value="TableService", desc="数据表",
events = {
		@Event(trigger="create",	handlers={@Handler(serviceId="ChangeLogService:createTable")}),
		@Event(trigger="update",	handlers={@Handler(serviceId="ChangeLogService:updateTable")})
})
public class TableService extends AbstractService{
    private ChangeLogService logService;
    public void setChangeLogService(ChangeLogService s) {
    	this.logService = s;
    }
	@MethodInfo("新增")
	@DaoCreate(entity=Table.ENTITY_NAME)
	public Table create(
			@Param(value=Table.TABLE_NAME, required=true,	desc="表名") String table_name,
			@Param(value=Table.TABLE_TITLE, required=true,	desc="中文名") String table_title,
			@Param(value=Table.APP, required=false,	desc="所属应用") String app,
			@Param(value=Table.MODULE, required=false,	desc="所属模块") String module,
			@Param(value=Table.UPDATOR, required=false,	desc="修改人") String updator,
			@Param(value=Table.UPDATE_TIME, required=false,	desc="修改时间") Date update_time,
			@Param(value=Table.PARTITIONED, required=false,	desc="是否分区") String partitioned,
			@Param(value=Table.PARTITION_FIELD, required=false,	desc="分区字段") String partition_field,
			@Param(value=Table.PARTITION_COMMENT, required=false,	desc="分区说明") String partition_comment,
			@Param(value=Table.COMMENT, required=false,	desc="备注") String comment
		
		) throws Exception {
		Principal p = ServiceContext.getContext().getPrincipal();
		return ModelFactory.createModelInstance(Table.class, Table.UPDATOR, p.getName(), Table.UPDATE_TIME, new Date());
	}

	@MethodInfo("修改")
	@DaoUpdate(entity=Table.ENTITY_NAME)
	public ActionResult update(
			@Param(value=Table.TABLE_ID, required=true,	desc="表ID") Long table_id,
			@Param(value=Table.TABLE_TITLE, required=true,	desc="中文名") String table_title,
			@Param(value=Table.APP, required=false,	desc="所属应用") String app,
			@Param(value=Table.MODULE, required=false,	desc="所属模块") String module,
			@Param(value=Table.UPDATOR, required=false,	desc="修改人") String updator,
			@Param(value=Table.UPDATE_TIME, required=false,	desc="修改时间") Date update_time,
			@Param(value=Table.COMMENT, required=false,	desc="备注") String comment
			
		) throws Exception {
		Principal p = ServiceContext.getContext().getPrincipal();
		return ModelFactory.createModelInstance(ActionResult.class, Table.UPDATOR, p.getName(), Table.UPDATE_TIME, new Date());
	}

	@MethodInfo("修改")
	@DaoUpdate(entity=Table.ENTITY_NAME)
	public ActionResult updatePartition(
			@Param(value=Table.TABLE_ID, required=true,	desc="表ID") Long table_id,
			@Param(value=Table.PARTITIONED, required=true,	desc="是否分区") String partitioned,
			@Param(value=Table.PARTITION_FIELD, required=false,	desc="分区字段") String partition_field,
			@Param(value=Table.PARTITION_COMMENT, required=false,	desc="分区说明") String partition_comment,
			@Param(value=Table.UPDATOR, required=false,	desc="修改人") String updator,
			@Param(value=Table.UPDATE_TIME, required=false,	desc="修改时间") Date update_time,
			@Param(value=Table.COMMENT, required=false,	desc="备注") String comment
			
		) throws Exception {
		Principal p = ServiceContext.getContext().getPrincipal();
		return ModelFactory.createModelInstance(ActionResult.class, Table.UPDATOR, p.getName(), Table.UPDATE_TIME, new Date());
	}

	@MethodInfo("删除")
	@DaoRemove(entity=Table.ENTITY_NAME)
	public ActionResult remove(
			@Param(value=Table.TABLE_ID, required=true,	desc="表ID") Long table_id
		) throws Exception {
		Principal p = ServiceContext.getContext().getPrincipal();
		this.logService.dropTable(table_id, p.getName(), new Date());
		return null;
	}

	@SuppressWarnings("unchecked")
	@MethodInfo("搜索")
	@DaoQuery(entity=Table.ENTITY_NAME, pageable=true, modelClass=Table.class, osql="order by table_name")
	public QueryResult<Table> findAll(
			@Param(value=Table.TABLE_NAME, required=false,	desc="表名", tag = "like") String table_name,
			@Param(value=Table.TABLE_TITLE, required=false,	desc="中文名", tag = "like") String table_title,
			@Param(value=Table.APP, required=false,	desc="所属应用") String app,
			@Param(value=Table.MODULE, required=false,	desc="所属模块") String module,
			@Param(value=Codes.PAGE_NUM, 	required=false, desc="分页页号，默认0") int page,
			@Param(value=Codes.PAGE_SIZE, 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		return ModelFactory.createModelInstance(QueryResult.class, 
				Codes.PAGE_NUM, page - 1,
				Table.TABLE_NAME, table_name == null || "null".equals(table_name)?null: "%" + table_name + "%",
				Table.TABLE_TITLE, table_title == null || "null".equals(table_title)?null: "%" + table_title + "%");
	}

	@MethodInfo("查询")
	@DaoQuery(entity=Table.ENTITY_NAME, single=true, throwsNotFoundException=true )
	public Table findById(
			@Param(value=Table.TABLE_ID, required=true,	desc="表ID") Long table_id
		) throws Exception {
		return null;
	}

	@MethodInfo("查询")
	@DaoQuery(entity=Table.ENTITY_NAME, single=true, throwsNotFoundException=true )
	public Table findByTableName(
			@Param(value=Table.TABLE_NAME, required=true,	desc="表名") String table_name
		) throws Exception {
		return null;
	}

}