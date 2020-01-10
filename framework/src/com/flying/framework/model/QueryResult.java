package com.flying.framework.model;

import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;

import com.flying.common.util.Codes;
import com.flying.framework.annotation.Param;
import com.flying.framework.data.Data;

@SuppressWarnings("serial")
public class QueryResult<T extends Data> extends Data implements MappingModelClass<T>, Iterable<T> {
	@Param(Codes.CODE)
	private String returnCode;

	@Param(Codes.MSG)
	private String message;

	@Param("pageNum")
	private int pageNum;

	@Param("pageSize")
	private int pageSize;

	@Param("pageCount")
	private int pageCount;

	@Param(Codes.TOTAL_ROWS)
	private int totalRowCount;

	@Param(Codes.ROWS)
	private List<T> rows;

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public int getTotalRowCount() {
		return totalRowCount;
	}

	public void setTotalRowCount(int totalRowCount) {
		this.totalRowCount = totalRowCount;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getModelClass() {
		Class < T >  entityClass  =  (Class <T> ) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		return entityClass;
	}
	//
	@Override
	public Iterator<T> iterator() {
		return this.rows == null? null: this.rows.iterator();
	}
	
}
