package com.flying.dba.model;

import java.util.Date;

import com.flying.framework.data.Data;
import com.flying.framework.annotation.Param;
import com.flying.framework.data.DataSerializable;
import com.flying.framework.metadata.Meta;

@SuppressWarnings("serial")
@Meta(id="db_column",title="数据库字段", table="db_column", primaryKeys={"column_id"})
public class Column extends Data implements DataSerializable {
	public final static String ENTITY_NAME = "db_column";
	
	public final static String COLUMN_ID = "column_id";
	public final static String TABLE_NAME = "table_name";
	public final static String FIELD_NAME = "field_name";
	public final static String FIELD_TITLE = "field_title";
	public final static String DATA_TYPE = "data_type";
	public final static String DATA_LENGTH = "data_length";
	public final static String NULLABLE = "nullable";
	public final static String DEFAULT_VALUE = "default_value";
	public final static String COMMENT = "comment";
	public final static String SORT_ORDER = "sort_order";
	public final static String UPDATOR = "updator";
	public final static String UPDATE_TIME = "update_time";
	
	
	@Param(value=COLUMN_ID,   required=true,   maxlength=0,	desc="字段ID" , generator="auto_increment")
	private Long columnId;
	
	@Param(value=TABLE_NAME,   required=true,   maxlength=50,	desc="表名" )
	private String tableName;
	
	@Param(value=FIELD_NAME,   required=false,   maxlength=50,	desc="字段名" )
	private String fieldName;
	
	@Param(value=FIELD_TITLE,   required=false,   maxlength=100,	desc="字段标题" )
	private String fieldTitle;
	
	@Param(value=DATA_TYPE,   required=false,   maxlength=50,	desc="数据类型" )
	private String dataType;
	
	@Param(value=DATA_LENGTH,   required=false,   maxlength=0,	desc="长度" )
	private Integer dataLength;
	
	@Param(value=NULLABLE,   required=false,   maxlength=1,	desc="允许空" )
	private String nullable;
	
	@Param(value=DEFAULT_VALUE,   required=false,   maxlength=100,	desc="默认值" )
	private String defaultValue;
	
	@Param(value=COMMENT,   required=false,   maxlength=500,	desc="备注" )
	private String comment;
	
	@Param(value=SORT_ORDER,   required=false,   maxlength=0,	desc="字段顺序" )
	private int sortOrder;
	
	@Param(value=UPDATOR,   required=false,   maxlength=100,	desc="修改人" )
	private String updator;
	
	@Param(value=UPDATE_TIME,   required=false,   maxlength=0,	desc="修改时间" )
	private Date updateTime;
	
	
	public Long getColumnId() {
		return this.columnId;
	}
	
	public void setColumnId(Long columnId) {
		this.columnId = columnId;
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
	
	public String getFieldTitle() {
		return this.fieldTitle;
	}
	
	public void setFieldTitle(String fieldTitle) {
		this.fieldTitle = fieldTitle;
	}
	
	public String getDataType() {
		return this.dataType;
	}
	
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public Integer getDataLength() {
		return this.dataLength;
	}
	
	public void setDataLength(Integer dataLength) {
		this.dataLength = dataLength;
	}
	
	public String getNullable() {
		return this.nullable;
	}
	
	public void setNullable(String nullable) {
		this.nullable = nullable;
	}
	
	public String getDefaultValue() {
		return this.defaultValue;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public String getComment() {
		return this.comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
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