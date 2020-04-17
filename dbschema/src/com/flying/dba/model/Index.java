package com.flying.dba.model;

import java.util.Date;

import com.flying.framework.data.Data;
import com.flying.framework.annotation.Param;
import com.flying.framework.data.DataSerializable;
import com.flying.framework.metadata.Meta;

@SuppressWarnings("serial")
@Meta(id="db_index",title="", table="db_index", primaryKeys={"index_id"})
public class Index extends Data implements DataSerializable {
	public final static String ENTITY_NAME = "db_index";
	
	public final static String INDEX_ID = "index_id";
	public final static String INDEX_NAME = "index_name";
	public final static String INDEX_TYPE = "index_type";
	public final static String TABLE_NAME = "table_name";
	public final static String UNIQUENESS = "uniqueness";
	public final static String COLUMN_NAME = "column_name";
	public final static String UPDATOR = "updator";
	public final static String UPDATE_TIME = "update_time";
	
	
	@Param(value=INDEX_ID,   required=true,   maxlength=0,	desc="索引ID" , generator="auto_increment")
	private Long indexId;
	
	@Param(value=INDEX_NAME,   required=true,   maxlength=100,	desc="索引名称" )
	private String indexName;
	
	@Param(value=INDEX_TYPE,   required=true,   maxlength=20,	desc="索引类型" )
	private String indexType;
	
	@Param(value=TABLE_NAME,   required=true,   maxlength=50,	desc="表名" )
	private String tableName;
	
	@Param(value=UNIQUENESS,   required=false,   maxlength=20,	desc="唯一性" )
	private String uniqueness;
	
	@Param(value=COLUMN_NAME,   required=true,   maxlength=100,	desc="索引字段" )
	private String columnName;
	
	@Param(value=UPDATOR,   required=false,   maxlength=100,	desc="修改人" )
	private String updator;
	
	@Param(value=UPDATE_TIME,   required=false,   maxlength=0,	desc="修改时间" )
	private Date updateTime;
	
	
	public Long getIndexId() {
		return this.indexId;
	}
	
	public void setIndexId(Long indexId) {
		this.indexId = indexId;
	}
	
	public String getIndexName() {
		return this.indexName;
	}
	
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	
	public String getIndexType() {
		return this.indexType;
	}
	
	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getUniqueness() {
		return this.uniqueness;
	}
	
	public void setUniqueness(String uniqueness) {
		this.uniqueness = uniqueness;
	}
	
	public String getColumnName() {
		return this.columnName;
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
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