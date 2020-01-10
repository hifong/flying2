package com.flying.schema;

import java.util.List;
import java.util.Map;

import com.flying.common.annotation.DaoQuery;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;

@Service("MySQLCodeGenerator")
public class MySQLCodeGenerator extends CodeGenerator {
	
	@MethodInfo("生成Service代码")
	public Data generateCodes(
			@Param(value = "tableSchema", desc = "数据库名称") String tableSchema,
			@Param(value = "tableNamePrefix", desc = "表前缀") String tableNamePrefix,
			@Param(value = "path", desc = "文件路径，包含package部分") String path,
			@Param(value = "postfix", desc = "文件名后缀") String postfix,
			@Param(value = "template", desc = "代码模板，默认class/code.tpl") String template) {
		return super.generateCodes(tableSchema, tableNamePrefix, path, postfix, template);
	}
	
	@MethodInfo("生成Java代码")
	public Data generateJavaCodes(
			@Param(value = "tableSchema", desc = "数据库名称") String tableSchema,
			@Param(value = "tableNamePrefix", desc = "表前缀") String tableNamePrefix,
			@Param(value = "path", desc = "文件路径，包含package部分") String path) {
		Data models = super.generateCodes(tableSchema, tableNamePrefix, path+"\\model", null, "class/model.tpl");
		Data services = super.generateCodes(tableSchema, tableNamePrefix, path+"\\service", "Service", "class/service.tpl");
		return new Data("models", models, "services", services);
	}

	@Override
	public Data generateConfiguration(
			@Param(value = "tableSchema", desc = "数据库名称") String tableSchema,
			@Param(value = "tableNamePrefix", desc = "表前缀") String tableNamePrefix,
			@Param(value = "path", desc = "文件路径，包含package部分") String path,
			@Param(value = "template", desc = "配置模板，默认class/entity.tpl") String template) throws Exception {
		return super.generateConfiguration(tableSchema, tableNamePrefix, path, template);
	}

	@Override
	@MethodInfo("生成配置文件")
	protected List<Table> getTables(String tableSchema, String tableNamePrefix) {
		List<Map<String, String>> tbs = this.findTables(tableSchema).get("rows");
		List<Table> tables = Utils.newArrayList();
		
		for(Map<String, String> t: tbs) {
			String tableName = t.get("table_name");
			String tableComment = t.get("table_comment");
			if(tableName == null || tableNamePrefix != null && !tableName.startsWith(tableNamePrefix)) continue;
			
			List<Field> fields = Utils.newArrayList();
			List<Map<String, Object>> columns = this.findColumns(tableSchema, tableName).get("rows");
			columns.stream().forEach(col -> {
				
				String comment = col.get("column_comment") == null?"":col.get("column_comment").toString();
				String data_type = col.get("data_type") == null?"":col.get("data_type").toString();
				
				int maxLength = 0;
				try {
				if(col.get("character_maximum_length") != null) 
					maxLength = Integer.parseInt(col.get("character_maximum_length").toString());
				} catch(Exception e) {
					maxLength = 10000;
				}
				boolean isPrimaryKey = col.get("column_key") != null && "PRI".equalsIgnoreCase(col.get("column_key").toString());
				String generator = tableName;
				if(col.get("extra") != null) {
					generator = col.get("extra").toString();
				}
				fields.add(new Field(col.get("column_name").toString(), data_type, "NO".equalsIgnoreCase(col.get("is_nullable").toString()), comment, maxLength, isPrimaryKey, generator ));
			});
			tables.add(new Table(tableSchema, tableName, tableComment, fields));
		}
		return tables;
	}
	
	@DaoQuery(entity="mysql.table", connectionTag="schema.jdbcTemplate")
	public Data findTables(@Param("table_schema")String table_schema) {
		return null;
	}
	
	@DaoQuery(entity="mysql.column", connectionTag="schema.jdbcTemplate")
	public Data findColumns(@Param("table_schema")String table_schema, @Param("table_name")String table_name) {
		return null;
	}
	
}