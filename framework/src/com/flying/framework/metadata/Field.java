package com.flying.framework.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flying.common.log.Logger;
import com.flying.common.util.Codes;
import com.flying.common.util.ServiceHelper;
import com.flying.common.util.Utils;
import com.flying.framework.data.Data;

public class Field extends HashMap<String, Object> {
	private static final long serialVersionUID = 1151724231670651224L;
	private static Logger log = Logger.getLogger(Field.class);

	public final static String TITLE = "title";
	public final static String FIELD_NAME = "name";
	public final static String ALIAS = "alias";
	public final static String FIELD_TYPE = "type";
	public final static String DATA_TYPE = "data_type";
	public final static String GENERATOR = "generator";
	public final static String GENERATOR_AUTO_INCREMENT = "auto_increment";

	//展现类型字段
	public final static String FIELD_OPTIONS = "options";
	public final static String OPTION_SERVICE_ID = "optionServiceId";
	public final static String OPTION_SERVICE_PARAMS = "optionParams";
	public final static String OPTION_VALUE_FIELD = "optionValueField";
	public final static String OPTION_TEXT_FIELD = "optionTextField";

	//翻译字典字段，适用于将ID等转成对象名称
	//效果：select reference-value from reference-entity where ref-rel-field=rel-field
	public final static String DICTIONARY_TYPE = "reference";
	public final static String DICTIONARY_RELATION_FIELD = "relField";
	public final static String DICTIONARY_ENTITY = "refEntity";
	public final static String DICTIONARY_REFERENCE_FIELD = "refRelField";
	public final static String DICTIONARY_VALUE_FIELD = "valueField";
	
	//服务类型字段
	public final static String SERVICE_ID = "serviceId";
	public final static String SERVICE_PARAMS = "params";
	public final static String SERVICE_VALUE_FIELD = "valueField";
	public final static String SERVICE_TEXT_FIELD = "textField";
	
	private Metadata metadata;
	
	public Field(Metadata metadata, Map<String, Object> data) {
		this.metadata = metadata;
		this.putAll(data);
	}
	
	public String getName() {
		return (String)super.get(FIELD_NAME);
	}
	
	public String getAlias() {
		return (String)super.get(ALIAS);
	}
	
	public String getType() {
		return (String)super.get(FIELD_TYPE);
	}
	
	public String getDataType() {
		return (String)super.get(DATA_TYPE);
	}
	
	public String getGenerator() {
		return (String)super.get(GENERATOR);
	}
	
	public boolean isAutoIncrement() {
		return GENERATOR_AUTO_INCREMENT.equalsIgnoreCase(getGenerator());
	}
	
	public String getValueField() {
		return (String)super.get(SERVICE_VALUE_FIELD);
	}
	
	public String getTextField() {
		return (String)super.get(SERVICE_TEXT_FIELD);
	}
	
	public String getRefEntity() {
		return (String)super.get(DICTIONARY_ENTITY);
	}
	
	public String getRefRelField() {
		return (String)super.get(DICTIONARY_REFERENCE_FIELD);
	}
	
	public String getRelField() {
		return (String)super.get(DICTIONARY_RELATION_FIELD);
	}
	//data source, combobox,combotree
	public String getUrl() {
		return (String)super.get("url");
	}
	//checkbox
	public String getModuleId() {
		return super.containsKey("moduleId")?(String)super.get("moduleId"): metadata.getRepository().getModule().getId();
	}

	public String getServiceId() {
		return (String)super.get(SERVICE_ID);
	}

	public String getOptionServiceId() {
		return (String)super.get(OPTION_SERVICE_ID);
	}
	
	public boolean isCondition() {
		return "true".equalsIgnoreCase((String)super.get("condition"));
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getOptions() {
		if(this.containsKey(FIELD_OPTIONS) && this.getOptionServiceId() == null)
			return (List<Map<String, Object>>)super.get(FIELD_OPTIONS);
		if(this.getOptionServiceId() == null) return null;
		
		synchronized(this) {
			if(this.containsKey(FIELD_OPTIONS) && this.getOptionServiceId() == null)
				return (List<Map<String, Object>>)super.get(FIELD_OPTIONS);
			
			List<Map<String, Object>> options = Utils.newArrayList();
			try {
				Map<String, Object> serviceParams = (Map<String, Object>)super.get(OPTION_SERVICE_PARAMS);
				Data params = new Data(serviceParams);
				
				List<Map<String, Object>> rows = ServiceHelper.invoke(getModuleId(), getOptionServiceId(), params).get(Codes.ROWS);
				final String valueField = (String)this.get(OPTION_VALUE_FIELD);
				final String textField  = (String)this.get(OPTION_TEXT_FIELD);
				for(Map<String, Object> r: rows) {
					Map<String, Object> row = Utils.newHashMap();
					row.put(SERVICE_VALUE_FIELD, r.get(valueField));
					row.put(SERVICE_TEXT_FIELD , r.get(textField));
					options.add(row);
				}
				put(FIELD_OPTIONS, options);
			} catch (Exception e) {
				log.warn("Metadata field[" + this.getName() +"] getOptions fail for reason:" + e, e);
			}
			return options;
		}
	}
}