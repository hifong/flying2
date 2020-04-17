package com.flying.dba.service;

import java.util.Date;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.common.util.Codes;
import com.flying.dba.model.Index;
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

@Service(value="IndexService", desc="",
events = {
		@Event(trigger="create",	handlers={@Handler(serviceId="ChangeLogService:createIndex")})
})
public class IndexService extends AbstractService{
    private ChangeLogService logService;
    public void setChangeLogService(ChangeLogService s) {
    	this.logService = s;
    }
        
	@MethodInfo("新增")
	@DaoCreate(entity=Index.ENTITY_NAME)
	public Index create(
			@Param(value=Index.INDEX_NAME, required=true,	desc="索引名称") String index_name,
			@Param(value=Index.INDEX_TYPE, required=true,	desc="索引类型") String index_type,
			@Param(value=Index.TABLE_NAME, required=true,	desc="表名") String table_name,
			@Param(value=Index.UNIQUENESS, required=false,	desc="唯一性") String uniqueness,
			@Param(value=Index.COLUMN_NAME, required=true,	desc="索引字段") String column_name,
			@Param(value=Index.UPDATOR, required=false,	desc="修改人") String updator,
			@Param(value=Index.UPDATE_TIME, required=false,	desc="修改时间") Date update_time
		
		) throws Exception {
		Principal p = ServiceContext.getContext().getPrincipal();
		return ModelFactory.createModelInstance(Index.class, Index.UPDATOR, p.getName(), Index.UPDATE_TIME, new Date());
	}

	@MethodInfo("修改")
	@DaoUpdate(entity=Index.ENTITY_NAME)
	public ActionResult update(
			@Param(value=Index.INDEX_ID, required=true,	desc="索引ID") Long index_id,
			@Param(value=Index.INDEX_NAME, required=true,	desc="索引名称") String index_name,
			@Param(value=Index.INDEX_TYPE, required=true,	desc="索引类型") String index_type,
			@Param(value=Index.TABLE_NAME, required=true,	desc="表名") String table_name,
			@Param(value=Index.UNIQUENESS, required=false,	desc="唯一性") String uniqueness,
			@Param(value=Index.COLUMN_NAME, required=true,	desc="索引字段") String column_name,
			@Param(value=Index.UPDATOR, required=false,	desc="修改人") String updator,
			@Param(value=Index.UPDATE_TIME, required=false,	desc="修改时间") Date update_time
			
		) throws Exception {
		Principal p = ServiceContext.getContext().getPrincipal();
		return ModelFactory.createModelInstance(ActionResult.class, Index.UPDATOR, p.getName(), Index.UPDATE_TIME, new Date());
	}

	@MethodInfo("删除")
	@DaoRemove(entity=Index.ENTITY_NAME)
	public ActionResult remove(
			@Param(value=Index.INDEX_ID, required=true,	desc="索引ID") Long index_id
		) throws Exception {
		Principal p = ServiceContext.getContext().getPrincipal();
		this.logService.dropIndex(index_id, p.getName(), new Date());
		return null;
	}

	@SuppressWarnings("unchecked")
	@MethodInfo("搜索")
	@DaoQuery(entity=Index.ENTITY_NAME, pageable=true, modelClass=Index.class)
	public QueryResult<Index> findByTableName(
			@Param(value=Index.TABLE_NAME, required=true,	desc="表名") String table_name,
			@Param(value=Codes.PAGE_NUM, 	required=false, desc="分页页号，默认0") int page,
			@Param(value=Codes.PAGE_SIZE, 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		return ModelFactory.createModelInstance(QueryResult.class, 
				Codes.PAGE_NUM, page - 1);
	}

	@MethodInfo("搜索")
	@DaoQuery(entity=Index.ENTITY_NAME, pageable=true, modelClass=Index.class)
	public QueryResult<Index> findAll(
			@Param(value=Codes.PAGE_NUM, 	required=false, desc="分页页号，默认0") int page,
			@Param(value=Codes.PAGE_SIZE, 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		return null;
	}

	@MethodInfo("查询")
	@DaoQuery(entity=Index.ENTITY_NAME, single=true, throwsNotFoundException=true )
	public Index findById(
			@Param(value=Index.INDEX_ID, required=true,	desc="索引ID") Long index_id
		) throws Exception {
		return null;
	}

	@MethodInfo("查询")
	@DaoQuery(entity=Index.ENTITY_NAME, single=true, throwsNotFoundException=true )
	public Index findByIndexName(
			@Param(value=Index.INDEX_NAME, required=true,	desc="索引名称") String index_name
		) throws Exception {
		return null;
	}

}