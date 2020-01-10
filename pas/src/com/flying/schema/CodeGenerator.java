package com.flying.schema;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.flying.common.util.StringUtils;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.data.Data;
import com.flying.framework.service.AbstractService;
import com.flying.framework.util.SmartyOutputUtils;

public abstract class CodeGenerator extends AbstractService {

	@MethodInfo("生成Service代码")
	public Data generateCodes(
			@Param(value = "tableSchema", desc = "数据库名称") String tableSchema,
			@Param(value = "tableNamePrefix", desc = "表前缀") String tableNamePrefix,
			@Param(value = "path", desc = "文件路径，包含package部分") String path,
			@Param(value = "postfix", desc = "文件名后缀") String postfix,
			@Param(value = "template", desc = "代码模板，默认class/service.tpl") String template) {
		final String pre = tableNamePrefix == null?"": tableNamePrefix;
		Data result = new Data();
		List<Table> tables = this.getTables(tableSchema, pre);
		tables.stream().filter(x -> x != null).filter(x -> x.tableName.toLowerCase().startsWith(pre.toLowerCase())).forEach(x -> {
			try {
				result.put(x.getTableName(), generateCode(x, path, postfix, template));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return result;
	}

	@MethodInfo("生成配置")
	public Data generateConfiguration(
			@Param(value = "tableSchema", desc = "数据库名称") String tableSchema,
			@Param(value = "tableNamePrefix", desc = "表前缀") String tableNamePrefix,
			@Param(value = "path", desc = "文件路径，包含package部分") String path,
			@Param(value = "template", desc = "代码模板，默认class/service.tpl")String template) throws Exception {
		List<Table> tables = this.getTables(tableSchema, tableNamePrefix);
		Collections.sort(tables, new Comparator<Table>(){
			@Override
			public int compare(Table o1, Table o2) {
				return o1.getTableName().compareTo(o2.tableName);
			}
		});
		final Data data = new Data();
		data.put("tables", tables);
		data.put("tableCount", tables.size());

		File f = new File(path);
		if(f.isDirectory()) path = f.getAbsolutePath() + "/" + tableNamePrefix + ".json";
		
		OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(path),"UTF-8");
		SmartyOutputUtils.output(module, data, template, fw);
		fw.flush();
		fw.close();
		
		return new Data("path", path);
	}
	
	private String generateCode(Table table, 
			@Param(value = "path", desc = "文件路径，包含package部分") String path,
			@Param(value = "postfix", desc = "文件名后缀") String postfix,
			@Param(value = "template", desc = "代码模板，默认class/code.tpl") String template)  throws Exception {
		final Data data = new Data();
		data.put("table", table);

		String tpath = StringUtils.replace(path, "\\", "/");
		if(tpath.indexOf("/com/") >= 0) {
			tpath = tpath.substring(tpath.indexOf("/com/") + 1);
			tpath = tpath.replaceAll("/", ".");
			data.put("packageName", tpath);
			data.put("parentPackage", tpath.substring(0, tpath.lastIndexOf(".")));
		}
		postfix = postfix == null?"": postfix.trim();
		data.put("postfix", postfix);
		//
		File dir = new File(path);
		if(!dir.exists()) dir.mkdirs();
		String fileName = path + "/" + table.getClassName() + postfix +".java";
		OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8");
		SmartyOutputUtils.output(module, data, template, fw);
		fw.flush();
		fw.close();
		return fileName;
	}

	protected abstract List<Table> getTables(String tableSchema,
			String tableNamePrefix);

	public class Table {
		private final String tableSchema;
		private final String tableName;
		private final String comment;
		private final List<Field> fields;

		public Table(String tableSchema, String tableName, String comment,
				List<Field> fields) {
			this.tableSchema = tableSchema;
			this.tableName = tableName;
			this.comment = comment;
			this.fields = fields;
		}

		public String getTableSchema() {
			return tableSchema;
		}

		public String getTableName() {
			return tableName;
		}

		public String getComment() {
			String table_comment = comment;
			if(table_comment == null) table_comment = "";
			if(table_comment.endsWith("表"))
				table_comment = table_comment.substring(0, table_comment.length() - 1);
			table_comment = table_comment.replaceAll("\n", "");
			return table_comment;
		}

		public List<Field> getFields() {
			return fields;
		}

		public int getFieldCount() {
			return this.fields.size();
		}

		public List<Field> getPrimaryFields() {
			return this.fields.stream().filter(x -> x.isPrimaryKey())
					.collect(Collectors.toList());
		}

		public int getPrimaryFieldCount() {
			return this.getPrimaryFields().size();
		}

		public String getPrimaryFieldNames() {
			return this.fields.stream().filter(x -> x.isPrimaryKey())
					.map(x -> x.getFieldName()).reduce((x, y) -> {
						return x + "," + y;
					}).get();
		}
		
		public String getClassName() {
			String[] tns = tableName.split("_");
			String[] cns = new String[tns.length - 1];
			for(int i=1; i< tns.length; i++) {
				cns[i - 1] = StringUtils.capitalize(tns[i]);
			}
			String className = StringUtils.join(cns, "");
			return className;
		}
	}

	public class Field {
		private final String fieldType;
		private final String fieldName;
		private final String comment;
		private final boolean required;
		private final boolean primaryKey;
		private final int maxLength;
		private final String generator;

		public Field(String fieldName, String fieldType, boolean required,
				String comment, int maxLength, boolean primaryKey,
				String generator) {
			this.fieldName = fieldName;
			this.fieldType = fieldType;
			this.required = required;
			this.comment = comment;
			this.maxLength = maxLength;
			this.primaryKey = primaryKey;
			this.generator = generator;
		}

		public String getDataType() {
			String dataType = fieldType;
			if ("date".equalsIgnoreCase(dataType)) {
				dataType = "Date";
			} else if ("datetime".equalsIgnoreCase(dataType)) {
				dataType = "Date";
			} else if ("timestamp".equalsIgnoreCase(dataType)) {
				dataType = "Date";
			} else if ("decimal".equalsIgnoreCase(dataType)) {
				dataType = "Double";
			} else if ("bigint".equalsIgnoreCase(dataType)) {
				dataType = "Long";
			} else if ("smallint".equalsIgnoreCase(dataType)) {
				dataType = "Integer";
			} else if (dataType.indexOf("int") >= 0) {
				dataType = "Integer";
			} else if (dataType.indexOf("number") >= 0) {
				dataType = "Long";
			} else if ("varchar".equalsIgnoreCase(dataType) || "varchar2".equalsIgnoreCase(dataType)) {
				dataType = "String";
			} else if (dataType.toLowerCase().indexOf("text") >= 0) {
				dataType = "String";
			} else if (dataType.toLowerCase().indexOf("char") >= 0) {
				dataType = "String";
			}
			return dataType;
		}

		public String getFieldType() {
			return fieldType;
		}

		public String getFieldName() {
			return fieldName;
		}

		public String getComment() {
			String result = comment;
			;
			if (StringUtils.isEmpty(result)) {
				result = this.fieldName;
				result = result.replaceAll("_", " ").toUpperCase();
			}
			result = result.replaceAll("\n", "");
			return result;
		}

		public boolean isRequired() {
			return required;
		}
		
		public boolean getRequired() {
			return this.required;
		}

		public boolean isPrimaryKey() {
			return primaryKey;
		}

		public boolean getPrimaryKey() {
			return primaryKey;
		}

		public String getGenerator() {
			return generator;
		}

		public String getMaxLength() {
			return this.maxLength > 0 ? String.valueOf(this.maxLength) : "0";
		}

		public String getXName() {
			return "\"" + this.fieldName + "\",";
		}
		
		public String getPropertyName() {
			String[] tns = fieldName.split("_");
			String[] cns = new String[tns.length - 1];
			for(int i=1; i< tns.length; i++) {
				cns[i - 1] = StringUtils.capitalize(tns[i]);
			}
			String propertyName = tns[0].toLowerCase() + StringUtils.join(cns, "");
			return propertyName;
		}
	}
}
