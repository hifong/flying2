package com.flying.dba.model;

import java.util.Date;

import com.flying.framework.data.Data;
import com.flying.framework.annotation.ConstantEnum;
import com.flying.framework.annotation.Param;
import com.flying.framework.data.DataSerializable;
import com.flying.framework.metadata.Meta;

@SuppressWarnings("serial")
@Meta(id="db_change_log",title="", table="db_change_log", primaryKeys={"log_id"})
public class ChangeLog extends Data implements DataSerializable {
	public final static String ENTITY_NAME = "db_change_log";
	
	public final static String LOG_ID = "log_id";
	public final static String TABLE_NAME = "table_name";
	public final static String FIELD_NAME = "field_name";
	public final static String ACTION_TYPE = "action_type";
	public final static String CONTENT = "content";
	public final static String SQL = "sql_log";
	public final static String UPDATOR = "updator";
	public final static String UPDATE_TIME = "update_time";

	public enum ActionType implements ConstantEnum<String> {
		TABLE_CREATE("TABLE_CREATE", "创建表"), 
		TABLE_DROP("TABLE_DROP", "删除表"), 
		TABLE_UPDATE("TABLE_UPDATE", "修改表"), 
		
		COLUMN_CREATE("COLUMN_CREATE", "创建字段"), 
		COLUMN_DROP("COLUMN_DROP", "删除字段"), 
		COLUMN_UPDATE("COLUMN_UPDATE", "修改字段"), 
		
		INDEX_CREATE("INDEX_CREATE", "创建索引"),
		INDEX_DROP("INDEX_DROP", "删除索引");
		
		public final String actionType;
		public final String remarks;
		
		ActionType(String actionType, String remarks) {
			this.actionType = actionType;
			this.remarks = remarks;
		}
		
		@Override
		public String value() {
			return actionType;
		}
		@Override
		public String text() {
			return remarks;
		}
	}
	
	@Param(value=LOG_ID,   required=true,   maxlength=0,	desc="日志ID" , generator="auto_increment")
	private Long logId;
	
	@Param(value=TABLE_NAME,   required=true,   maxlength=50,	desc="表名" )
	private String tableName;
	
	@Param(value=FIELD_NAME,   required=false,   maxlength=50,	desc="字段名" )
	private String fieldName;
	
	@Param(value=ACTION_TYPE,   required=true,   maxlength=100,	desc="操作类型" )
	private String actionType;
	
	@Param(value=CONTENT,   required=true,   maxlength=1000,	desc="修改内容" )
	private String content;
	
	@Param(value=SQL,   required=false,   maxlength=1000,	desc="生成SQL" )
	private String sql;
	
	@Param(value=UPDATOR,   required=true,   maxlength=100,	desc="修改人" )
	private String updator;
	
	@Param(value=UPDATE_TIME,   required=true,   maxlength=0,	desc="修改时间" )
	private Date updateTime;
	
	
	public Long getLogId() {
		return this.logId;
	}
	
	public void setLogId(Long logId) {
		this.logId = logId;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getFieldName() {
		return this.fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getActionType() {
		return this.actionType;
	}
	
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getSql() {
		return this.sql;
	}
	
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public String getUpdator() {
		return this.updator;
	}
	
	public void setUpdator(String updator) {
		this.updator = updator;
	}
	
	public Date getUpdateTime() {
		return this.updateTime;
	}
	
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	

}