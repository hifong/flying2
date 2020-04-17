package com.flying.dba.service;

import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.common.util.Codes;
import com.flying.common.util.DateUtils;
import com.flying.common.util.Utils;
import com.flying.dba.model.ChangeLog;
import com.flying.dba.model.ChangeLog.ActionType;
import com.flying.dba.model.Column;
import com.flying.dba.model.Index;
import com.flying.dba.model.Table;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;
import com.flying.framework.model.ActionResult;
import com.flying.framework.model.ModelFactory;
import com.flying.framework.model.QueryResult;
import com.flying.framework.service.AbstractService;
import com.flying.framework.util.SmartyOutputUtils;

@Service(value="ChangeLogService", desc="")
public class ChangeLogService extends AbstractService{
    private TableService tableService;
    private ColumnService columnService;
    private IndexService indexService;

	public void setTableService(TableService tableService) {
		this.tableService = tableService;
	}

	public void setColumnService(ColumnService columnService) {
		this.columnService = columnService;
	}

	public void setIndexService(IndexService indexService) {
		this.indexService = indexService;
	}
	//
	public String buildSql(ActionType actionType, Data data) {
		try(StringWriter w = new StringWriter()){
			SmartyOutputUtils.output(super.getModule(), data, "sql/"+actionType.actionType+".tpl", w);
			return w.getBuffer().toString();
		} catch (Exception e) {
			return "SQL error " + e;
		}
	}
	
	public ChangeLog createTable(
			@Param(value=ChangeLog.TABLE_NAME, required=true,	desc="表名") String table_name,
			@Param(value=ChangeLog.UPDATOR, required=true,	desc="修改人") String updator,
			@Param(value=ChangeLog.UPDATE_TIME, required=true,	desc="修改时间") Date update_time) throws Exception {
		Data data = new Data();
		data.put("Table", this.tableService.findByTableName(table_name));
		final String sql = buildSql(ActionType.TABLE_CREATE, data);
		return this.create(table_name, "-", 
				ActionType.TABLE_CREATE.actionType, 
				ActionType.TABLE_CREATE.remarks + " \""+table_name+"\"", sql, updator, update_time);
	}
	
	public ChangeLog updateTable(
			@Param(value=Table.TABLE_ID, required=true,	desc="表ID") Long table_id) throws Exception {
		Table t = this.tableService.findById(table_id);
		final String table_name = t.getTableName();
		final String comment = t.getComment();
		Data data = new Data();
		data.put("Table", t);
		final String sql = buildSql(ActionType.TABLE_UPDATE, data);
		return this.create(table_name, "-", 
				ActionType.TABLE_UPDATE.actionType, 
				ActionType.TABLE_UPDATE.remarks + " \""+table_name+":"+comment+"\"", sql, t.getUpdator(), t.getUpdateTime());
	}
	
	public ChangeLog dropTable(
			@Param(value=Table.TABLE_ID, required=true,	desc="表ID") Long table_id,
			@Param(value=ChangeLog.UPDATOR, required=true,	desc="修改人") String updator,
			@Param(value=ChangeLog.UPDATE_TIME, required=true,	desc="修改时间") Date update_time) throws Exception {
		Table t = this.tableService.findById(table_id);
		final String table_name = t.getTableName();
		Data data = new Data();
		data.put("Table", t);
		final String sql = buildSql(ActionType.TABLE_DROP, data);
		return this.create(table_name, "-", 
				ActionType.TABLE_DROP.actionType, 
				ActionType.TABLE_DROP.remarks + " \""+table_name+"\"", sql, updator, update_time);
	}
	
	public ChangeLog createColumn(
			@Param(value=ChangeLog.TABLE_NAME, required=true,	desc="表名") String table_name,
			@Param(value=ChangeLog.FIELD_NAME, required=false,	desc="字段名") String field_name,
			@Param(value=ChangeLog.UPDATOR, required=true,	desc="修改人") String updator,
			@Param(value=ChangeLog.UPDATE_TIME, required=true,	desc="修改时间") Date update_time) throws Exception {
		Data data = new Data();
		data.put("Column", this.columnService.findByTableAndColumn(table_name, field_name));
		final String sql = buildSql(ActionType.COLUMN_CREATE, data);
		return this.create(table_name, "-", 
				ActionType.COLUMN_CREATE.actionType, 
				ActionType.COLUMN_CREATE.remarks + " \""+table_name+"."+field_name+"\"", sql, updator, update_time);
	}
	
	public ChangeLog updateColumn(
			@Param(value=Column.COLUMN_ID, required=true,	desc="字段ID") Long column_id) throws Exception {
		Column column = this.columnService.findById(column_id);
		Data data = new Data();
		data.put("Column", column);
		final String sql = buildSql(ActionType.COLUMN_UPDATE, data);
		return this.create(column.getTableName(), "-", 
				ActionType.COLUMN_UPDATE.actionType, 
				ActionType.COLUMN_UPDATE.remarks + " \""+column.getTableName()+"."+column.getFieldName()+"\"", sql, 
				column.getUpdator(), column.getUpdateTime());
	}
	
	public ChangeLog dropColumn(
			@Param(value=Column.COLUMN_ID, required=true,	desc="字段ID") Long column_id,
			@Param(value=ChangeLog.UPDATOR, required=true,	desc="修改人") String updator,
			@Param(value=ChangeLog.UPDATE_TIME, required=true,	desc="修改时间") Date update_time) throws Exception {
		Column column = this.columnService.findById(column_id);
		Data data = new Data();
		data.put("Column", column);
		final String sql = buildSql(ActionType.COLUMN_DROP, data);
		return this.create(column.getTableName(), "-", 
				ActionType.COLUMN_DROP.actionType, 
				ActionType.COLUMN_DROP.remarks + " \""+column.getTableName()+"."+column.getFieldName()+"\"", sql, 
				updator, update_time);
	}
	
	public ChangeLog createIndex(
			@Param(value=Index.INDEX_NAME, required=true,	desc="索引名称") String index_name,
			@Param(value=Index.UPDATOR, required=false,	desc="修改人") String updator,
			@Param(value=Index.UPDATE_TIME, required=false,	desc="修改时间") Date update_time) throws Exception {
		Data data = new Data();
		Index ind = this.indexService.findByIndexName(index_name);
		data.put("Index", ind);
		final String sql = buildSql(ActionType.INDEX_CREATE, data);
		return this.create(ind.getTableName(), "-", 
				ActionType.INDEX_CREATE.actionType, 
				ActionType.INDEX_CREATE.remarks + " \""+ind.getTableName()+"."+ind.getIndexName()+"\"", sql, updator, update_time);
	}
	
	public ChangeLog dropIndex(
			@Param(value=Index.INDEX_ID, required=true,	desc="索引ID") Long index_id,
			@Param(value=Index.UPDATOR, required=false,	desc="修改人") String updator,
			@Param(value=Index.UPDATE_TIME, required=false,	desc="修改时间") Date update_time) throws Exception {
		Index ind = this.indexService.findById(index_id);
		Data data = new Data();
		data.put("Index", ind);
		final String sql = buildSql(ActionType.INDEX_DROP, data);
		return this.create(ind.getTableName(), "-", 
				ActionType.INDEX_DROP.actionType, 
				ActionType.INDEX_DROP.remarks + " \""+ind.getTableName()+"."+ind.getIndexName()+"\"", sql, 
				updator, update_time);
	}
	//
	@MethodInfo("新增")
	@DaoCreate(entity=ChangeLog.ENTITY_NAME)
	public ChangeLog create(
			@Param(value=ChangeLog.TABLE_NAME, required=true,	desc="表名") String table_name,
			@Param(value=ChangeLog.FIELD_NAME, required=false,	desc="字段名") String field_name,
			@Param(value=ChangeLog.ACTION_TYPE, required=true,	desc="操作类型") String action_type,
			@Param(value=ChangeLog.CONTENT, required=true,	desc="修改内容") String content,
			@Param(value=ChangeLog.SQL, required=false,	desc="生成SQL") String sql,
			@Param(value=ChangeLog.UPDATOR, required=true,	desc="修改人") String updator,
			@Param(value=ChangeLog.UPDATE_TIME, required=true,	desc="修改时间") Date update_time
		
		) throws Exception {
		return null;
	}

	@MethodInfo("修改")
	@DaoUpdate(entity=ChangeLog.ENTITY_NAME)
	public ActionResult update(
		
			@Param(value=ChangeLog.LOG_ID, required=true,	desc="日志ID") Long log_id,
			@Param(value=ChangeLog.CONTENT, required=true,	desc="修改内容") String content,
			@Param(value=ChangeLog.SQL, required=false,	desc="生成SQL") String sql,
			@Param(value=ChangeLog.UPDATOR, required=true,	desc="修改人") String updator,
			@Param(value=ChangeLog.UPDATE_TIME, required=true,	desc="修改时间") Date update_time
			
		) throws Exception {
		
		return null;
	}

	@MethodInfo("删除")
	@DaoRemove(entity=ChangeLog.ENTITY_NAME)
	public ActionResult remove(
		
			@Param(value=ChangeLog.LOG_ID, required=true,	desc="日志ID") Long log_id
		
		) throws Exception {
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@MethodInfo("搜索")
	@DaoQuery(entity=ChangeLog.ENTITY_NAME, pageable=true, modelClass=ChangeLog.class, osql="order by update_time desc")
	public QueryResult<ChangeLog> findAll(
			@Param(value=ChangeLog.TABLE_NAME, required=false,	desc="表名") String table_name,
			@Param(value=ChangeLog.FIELD_NAME, required=false,	desc="字段名") String field_name,
			@Param(value=Codes.PAGE_NUM, 	required=false, desc="分页页号，默认0") int page,
			@Param(value=Codes.PAGE_SIZE, 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		return ModelFactory.createModelInstance(QueryResult.class, 
				Codes.PAGE_NUM, page - 1);
	}

	@SuppressWarnings("unchecked")
	@MethodInfo("搜索")
	@DaoQuery(entity=ChangeLog.ENTITY_NAME, pageable=true, modelClass=ChangeLog.class, osql="order by update_time desc")
	public QueryResult<ChangeLog> findByUpdateTime(
			@Param(value=ChangeLog.UPDATE_TIME, required=true,	desc="修改时间", tag=">") Date update_time,
			@Param(value=ChangeLog.TABLE_NAME, required=false,	desc="表名") String table_name,
			@Param(value=Codes.PAGE_NUM, 	required=false, desc="分页页号，默认0") int page,
			@Param(value=Codes.PAGE_SIZE, 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		return ModelFactory.createModelInstance(QueryResult.class, 
				Codes.PAGE_NUM, page - 1);
	}

	@MethodInfo("搜索")
	public QueryResult<ChangeLog> findByDays(
			@Param(value="days", required=true,	desc="天数", tag=">") int days,
			@Param(value=ChangeLog.TABLE_NAME, required=false,	desc="表名") String table_name,
			@Param(value=Codes.PAGE_NUM, 	required=false, desc="分页页号，默认0") int page,
			@Param(value=Codes.PAGE_SIZE, 	required=false, desc="分页大小，默认10") int rows) throws Exception {
		Date d = DateUtils.add(new Date(), DateUtils.Field.DATE, -1 * days);
		return this.findByUpdateTime(d, table_name, page, rows);
	}

	@MethodInfo("搜索")
	public QueryResult<SummaryChangeLog> findSummariesByDays(
			@Param(value="days", required=true,	desc="天数", tag=">") int days) throws Exception {
		List<ChangeLog> logs = this.findByDays(days, null, 1, Integer.MAX_VALUE).getRows();
		List<SummaryChangeLog> slogs = Utils.newArrayList();
		for(Iterator<ChangeLog> it=logs.iterator(); it.hasNext(); ) {
			ChangeLog log = it.next();
			Optional<SummaryChangeLog> oslog = slogs.stream().filter(x -> x.tableName.equals(log.getTableName())).findFirst();
			if(oslog.isPresent()) {
				oslog.get().logs.add(log);
			} else {
				SummaryChangeLog slog = new SummaryChangeLog(log.getTableName());
				slog.logs.add(log);
				slogs.add(slog);
			}
		}
		@SuppressWarnings("unchecked")
		QueryResult<SummaryChangeLog> result = ModelFactory.createModelInstance(QueryResult.class);
		result.setReturnCode(Codes.SUCCESS+"");
		result.setRows(slogs);
		result.setTotalRowCount(slogs.size());
		return result;
	}

	@MethodInfo("查询")
	@DaoQuery(entity=ChangeLog.ENTITY_NAME, single=true, throwsNotFoundException=true )
	public ChangeLog findById(
			@Param(value=ChangeLog.LOG_ID, required=true,	desc="日志ID") Long log_id
		) throws Exception {
		return null;
	}

	public class SummaryChangeLog extends Data {
		private String tableName;
		private List<ChangeLog> logs = Utils.newArrayList();
		public SummaryChangeLog(String tableName) {
			this.tableName = tableName;
		}
		public String getTableName() {
			return this.tableName;
		}
		public List<ChangeLog> getLogs() {
			return this.logs;
		}
	}
}