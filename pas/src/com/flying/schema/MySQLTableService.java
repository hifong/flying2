package com.flying.schema;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.flying.common.annotation.DaoQuery;
import com.flying.common.util.Codes;
import com.flying.common.util.StringUtils;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;
import com.flying.framework.data.JSONData;
import com.flying.framework.service.AbstractService;
import com.flying.framework.util.SmartyOutputUtils;

@Service("MySQLTable")
public class MySQLTableService extends AbstractService {
	@DaoQuery(entity="table", connectionTag="schema.jdbcTemplate")
	protected Data getTables(@Param("table_schema")String table_schema) {
		return null;
	}
	
	@DaoQuery(entity="column", connectionTag="schema.jdbcTemplate")
	protected Data getColumns(@Param("table_schema")String table_schema, @Param("table_name")String table_name) {
		return null;
	}
	
	@MethodInfo("生成配置文件")
	protected Data buildDescriptors(
			@Param(value="table_schema", desc="数据库名称")String table_schema,
			@Param(value="table_name_prefix", desc="表前缀")String table_name_prefix,
			@Param(value="action", desc="操作")String action) {
		List<Map<String, String>> tables = this.getTables(table_schema).get("rows");
		Data result = new Data(Codes.CODE, Codes.SUCCESS);
		
		for(Map<String, String> t: tables) {
			String table_name = t.get("table_name");
			String table_comment = t.get("table_comment");
			if(table_name == null || table_name_prefix != null && !table_name.startsWith(table_name_prefix)) continue;
			if(table_comment == null) table_comment = "";
			if(table_comment.endsWith("表"))
				table_comment = table_comment.substring(0, table_comment.length() - 1);
			table_comment = table_comment.replaceAll("\n", "");
			//
			Data table = new Data("table-name", table_name, "title", table_comment);
			String pks = "";
			
			List<Map<String, Object>> columns = this.getColumns(table_schema, table_name).get("rows");
			List<Map<String, Object>> fields = Utils.newArrayList();
			for(Map<String, Object> col: columns) {
				String comment = col.get("column_comment") == null?"":col.get("column_comment").toString();
				if(StringUtils.isEmpty(comment)) {
					comment = (String)col.get("column_name");
					comment = comment.replaceAll("_", " ").toUpperCase();
				}
				comment = comment.replaceAll("\n", "");
				
				String data_type = col.get("data_type") == null?"":col.get("data_type").toString();
				if("date".equals(data_type)) data_type = "Date";
				if("datetime".equalsIgnoreCase(data_type)) data_type = "Date";
				if("timestamp".equalsIgnoreCase(data_type)) data_type = "Date";
				if("decimal".equalsIgnoreCase(data_type)) data_type = "float";
				if(data_type.indexOf("int") >= 0) data_type  = "int";
				if("varchar".equalsIgnoreCase(data_type)) data_type = "String";
				if(data_type.toLowerCase().indexOf("text") >= 0) data_type  = "String";
				if(data_type.toLowerCase().indexOf("char") >= 0) data_type  = "String";
				
				Data f = new Data(
						"name", 		col.get("column_name"), 
						"data_type", 	data_type, 
						"title", 		comment, 
						"required", 	"NO".equalsIgnoreCase(col.get("is_nullable").toString())
						);
				if("code".equals(action))
					f.put("xname", "\""+col.get("column_name") + "\",");
				if(col.get("character_maximum_length") != null) 
					f.put("maxlength", 	col.get("character_maximum_length"));
				if(col.get("column_key") != null && "PRI".equalsIgnoreCase(col.get("column_key").toString()))  {
					if(pks.length() > 0) pks += ",";
					pks += col.get("column_name").toString();
					
					String extra = (String)col.get("extra");
					//
					if("int".equalsIgnoreCase(data_type))
						f.put("generator", StringUtils.isEmpty(extra)?table_name:extra);
				}
				fields.add(f.getValues());
			}
			table.put("fields", fields); 
			//
			if(pks.indexOf(",") > 0)
				table.put("primary-key", pks.split(","));
			else
				table.put("primary-key", pks);
			//
			result.put(table_name, table);
		}
		return result;
	}

	public Data generateEntities(
			@Param(value="table_schema", desc="数据库名称")String table_schema,
			@Param(value="table_name_prefix", desc="表前缀")String table_name_prefix,
			@Param(value="file_name", desc="文件路径")String fileName) throws Exception {
		Data descriptors = this.buildDescriptors(table_schema, table_name_prefix, "entity");
		for(Iterator<Entry<String, Object>> it = descriptors.getValues().entrySet().iterator(); it.hasNext(); ) {
			Entry<String, Object> e = it.next();
			if(!(e.getValue() instanceof Data)) {
				it.remove();
			}
		}
		
		File f = new File(fileName);
		if(f.isDirectory()) fileName = f.getAbsolutePath() + "/" + table_name_prefix + ".json";
		
		JSONData jr = new JSONData(descriptors);
		OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8");
		fw.append(jr.toJSONString());
		fw.close();
		return new Data("file_name", fileName);
	}

	public Data generateViews(
			@Param(value="table_schema", desc="数据库名称")String table_schema,
			@Param(value="table_name_prefix", desc="表前缀")String table_name_prefix,
			@Param(value="file_name", desc="文件路径")String fileName) throws Exception {
		Data descriptors = this.buildDescriptors(table_schema, table_name_prefix, "view");
		for(Iterator<Entry<String, Object>> it = descriptors.getValues().entrySet().iterator(); it.hasNext(); ) {
			Entry<String, Object> e = it.next();
			if(!(e.getValue() instanceof Data)) {
				it.remove();
			}
		}
		
		Data views = new Data();
		for(String tn: descriptors.keys()) {
			if(!(descriptors.get(tn) instanceof Data)) continue;
			
			Map<String, Object> view = Utils.newHashMap();
			Data tab = (Data)descriptors.get(tn);
			
			view.put("entity", tn);
			List<Map<String, Object>> fs = (List<Map<String, Object>>)tab.get("fields");
			
			List<Object> fields = Utils.newArrayList();
			for(Map<String, Object> f: fs) fields.add(f.get("name"));
			view.put("fields", fields);
			
			views.put("v." + tn, view);
		}
		
		File f = new File(fileName);
		if(f.isDirectory()) fileName = f.getAbsolutePath() + "/" + table_name_prefix + ".json";
		//
		JSONData jr = new JSONData(views);
		OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8");
		fw.append(jr.toJSONString());
		fw.close();
		return new Data("file_name", fileName);
	}
	
	@MethodInfo("代码生成机")
	public Data generateCodes(
			@Param(value="table_schema", desc="数据库名称")String table_schema,
			@Param(value="table_name_prefix", desc="表前缀")String table_name_prefix,
			@Param(value="path", desc="文件路径，包含package部分")String path,
			@Param(value="template", desc="代码模板，默认class/code.tpl")String template) throws Exception {
		Data descriptors = this.buildDescriptors(table_schema, table_name_prefix, "code");
		Data result = new Data();
		for(String table_name : descriptors.keys()) {
			if(!(descriptors.get(table_name) instanceof Data)) continue;
			
			String[] tns = table_name.split("_");
			String[] cns = new String[tns.length - 1];
			for(int i=1; i< tns.length; i++) {
				cns[i - 1] = StringUtils.capitalize(tns[i]);
			}
			String className = StringUtils.join(cns, "");
			String fileName = path + "/" + className +".java";
			
			Data table = descriptors.get(table_name);
			List<Map<String, Object>> fields = table.get("fields");
			table.put("class_name", className);
			table.put("table_name", table_name);
			table.put("table_comment", table.getString("title"));
			table.put("field_count", fields.size());
			//
			String[] pks = null;
			if(table.get("primary-key") instanceof String) {
				pks = new String[]{(String)table.get("primary-key")};
			} else {
				pks = (String[])table.get("primary-key");
			}
			List<Map<String, Object>> pFields = Utils.newArrayList();
			for(String p: pks) {
				for(Map<String, Object> f: fields) {
					if(p.equalsIgnoreCase((String)f.get("name"))) {
						pFields.add(f);
					}
				}
			}
			table.put("pfields", pFields);
			table.put("pfield_count", pFields.size());
			
			String tpath = StringUtils.replace(path, "\\", "/");
			if(tpath.indexOf("/com/") >= 0) {
				tpath = tpath.substring(tpath.indexOf("/com/") + 1);
				table.put("package_name", tpath.replaceAll("/", "."));
			}
			//
			OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8");
			SmartyOutputUtils.output(module, table, template, fw);
			fw.flush();
			fw.close();
			result.put(className, fileName);
		}
		return result;
	}
}