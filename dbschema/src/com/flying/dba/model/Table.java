package com.flying.dba.model;

import java.util.Date;

import com.flying.framework.data.Data;
import com.flying.framework.annotation.Param;
import com.flying.framework.data.DataSerializable;
import com.flying.framework.metadata.Meta;

@SuppressWarnings("serial")
@Meta(id="db_table",title="数据表", table="db_table", primaryKeys={"table_id"})
public class Table extends Data implements DataSerializable {
	public final static String ENTITY_NAME = "db_table";
	
	public final static String TABLE_ID = "table_id";
	public final static String TABLE_NAME = "table_name";
	public final static String TABLE_TITLE = "table_title";
	public final static String APP = "app";
	public final static String MODULE = "module";
	public final static String UPDATOR = "updator";
	public final static String UPDATE_TIME = "update_time";
	public final static String PARTITIONED = "partitioned";
	public final static String PARTITION_FIELD = "partition_field";
	public final static String PARTITION_COMMENT = "partition_comment";
	public final static String COMMENT = "comment";
	
	
	@Param(value=TABLE_ID,   required=true,   maxlength=0,	desc="表ID" , generator="auto_increment")
	private Long tableId;
	
	@Param(value=TABLE_NAME,   required=true,   maxlength=50,	desc="表名" )
	private String tableName;
	
	@Param(value=TABLE_TITLE,   required=true,   maxlength=100,	desc="中文名" )
	private String tableTitle;
	
	@Param(value=APP,   required=false,   maxlength=100,	desc="所属应用" )
	private String app;
	
	@Param(value=MODULE,   required=false,   maxlength=100,	desc="所属模块" )
	private String module;
	
	@Param(value=UPDATOR,   required=false,   maxlength=100,	desc="修改人" )
	private String updator;
	
	@Param(value=UPDATE_TIME,   required=false,   maxlength=0,	desc="修改时间" )
	private Date updateTime;
	
	@Param(value=PARTITIONED,   required=false,   maxlength=10,	desc="是否分区" )
	private String partitioned;
	
	@Param(value=PARTITION_FIELD,   required=false,   maxlength=100,	desc="分区字段" )
	private String partitionField;
	
	@Param(value=PARTITION_COMMENT,   required=false,   maxlength=100,	desc="分区说明" )
	private String partitionComment;
	
	@Param(value=COMMENT,   required=false,   maxlength=200,	desc="备注" )
	private String comment;
	
	
	public Long getTableId() {
		return this.tableId;
	}
	
	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getTableTitle() {
		return this.tableTitle;
	}
	
	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	}
	
	public String getApp() {
		return this.app;
	}
	
	public void setApp(String app) {
		this.app = app;
	}
	
	public String getModule() {
		return this.module;
	}
	
	public void setModule(String module) {
		this.module = module;
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
	
	public String getPartitioned() {
		return this.partitioned;
	}
	
	public void setPartitioned(String partitioned) {
		this.partitioned = partitioned;
	}
	
	public String getPartitionField() {
		return this.partitionField;
	}
	
	public void setPartitionField(String partitionField) {
		this.partitionField = partitionField;
	}
	
	public String getPartitionComment() {
		return this.partitionComment;
	}
	
	public void setPartitionComment(String partitionComment) {
		this.partitionComment = partitionComment;
	}
	
	public String getComment() {
		return this.comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	

}