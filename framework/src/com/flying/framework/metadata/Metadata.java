package com.flying.framework.metadata;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.flying.common.log.Logger;
import com.flying.common.util.StringUtils;
import com.flying.common.util.Utils;

public class Metadata {
	private static Logger log = Logger.getLogger(Field.class);

	public final static String TITLE = "title";
	public final static String TABLE_NAME = "table-name";
	public final static String FIELDS = "fields";
	public final static String PRIMARY_KEY = "primary-key";
	public final static String ENTITY = "entity";

	private final Repository repository;
	private final Map<String, Object> data;
	private List<Field> fields;
	private Set<String> primaryKeys;
	
	public Metadata(Map<String, Object> data, Repository repository) {
		this.repository = repository;
		this.data = data;
		this.getFields();
		if(!StringUtils.isEmpty(getEntity())) {
			Metadata refmd = this.repository.getMetadata(this.getEntity());
			if(refmd == null) return;
			for(String k: refmd.data.keySet()) {
				if(!this.data.containsKey(k)) {
					this.data.put(k, refmd.data.get(k));
				}
			}
		}
	}
	
	public Repository getRepository() {
		return this.repository;
	}
	
	public String getString(String key) {
		return (String)data.get(key);
	}
	
	public String getTable() {
		return getString(TABLE_NAME);
	}
	
	public String getEntity() {
		return getString(ENTITY);
	}

	@SuppressWarnings("unchecked")
	public Set<String> getPrimaryKeys() {
		if(this.primaryKeys != null) return this.primaryKeys;
		this.primaryKeys = Utils.newHashSet();
		//
		Object pk = this.data.get(PRIMARY_KEY);
		if(pk instanceof String) {
			String pks = getString(PRIMARY_KEY);
			if(pks != null) {
				String[] fs = StringUtils.split(pks, ",");
				for(String f: fs) this.primaryKeys.add(f.toLowerCase());
			}
		} else if(pk instanceof List) {
			List<String> pks = (List<String>)pk;
			this.primaryKeys.addAll(pks);
		}
		return this.primaryKeys;
	}
	
	public boolean isPrimaryKey(String field) {
		return this.getPrimaryKeys().contains(field.toLowerCase());
	}
	
	public Map<String, Object> getData() {
		return data;
	}

	private List<Field> serviceFields;
	public List<Field> getServiceFields() {
		if(this.serviceFields != null) return this.serviceFields;
		
		synchronized(this) {
			if(this.serviceFields != null) return this.serviceFields;
			
			this.serviceFields = Utils.newArrayList();
			for(Field f: this.getFields()) {
				if(!StringUtils.isEmpty(f.getServiceId())) {
					serviceFields.add(f);
				}
			}
			return serviceFields;
		}
	}
	
	private List<Field> simpleFields;
	public List<Field> getSimpleFields() {
		if(this.simpleFields != null) return this.simpleFields;
		
		synchronized(this) {
			if(this.simpleFields != null) return this.simpleFields;
			
			this.simpleFields = Utils.newArrayList();
			for(Field f: this.getFields()) {
				if(StringUtils.isEmpty(f.getServiceId()) && !Field.DICTIONARY_TYPE.equalsIgnoreCase((String)f.get(Field.FIELD_TYPE))) {
					simpleFields.add(f);
				}
			}
			return this.simpleFields;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Field> getFields() {
		if(fields != null) return fields;
		
		synchronized(this) {
			if(fields != null) return fields;
			//
			fields = Utils.newArrayList();
			List<Object>fs = (List<Object>)this.data.get(FIELDS);
			for(Object f: fs) {
				if(f instanceof String) {
					Metadata md = this.repository.getMetadata(this.getEntity());
					if(md.getField((String)f) == null)
						log.error("Metadata error, Field [" + f +"] in " + this.getEntity() + " not exists, ignored!");
					else
						fields.add(md.getField((String)f));
				} else {
					Map<String, Object> fmap = (Map<String, Object>)f;
					Field fd = new Field(this, fmap);
//					if(fd.containsKey("inherited-entity")) {
//						String entity = fd.getInheritedEntity();
//						String name = fd.getInheritedField();
//						Metadata md = this.repository.getMetadata(entity);
//						Field refField = md.getField(name);
//						for(String k: refField.keySet()) {
//							if(!fd.containsKey(k)) 
//								fd.put(k, refField.get(k));
//						}
//					}
					fields.add(fd);
				}
			}
			this.data.put(FIELDS, fields);
			return fields;
		}
	}

	private List<Field> aliasFields;
	public List<Field> getAliasField() {
		if(this.aliasFields != null) return this.aliasFields;
		synchronized(this){
			if(this.aliasFields != null) return this.aliasFields;
			aliasFields = Utils.newArrayList();
			for(Field f: this.getFields()) {
				if(!StringUtils.isEmpty(f.getAlias()))
					aliasFields.add(f);
			}
			return this.aliasFields;
		}
	}
	
	public List<Field> getConditions() {
		List<Field> conditions = Utils.newArrayList();
		for(Field f: this.getFields()) {
			if(f.isCondition()) 
				conditions.add(f);
		}
		if(conditions.isEmpty())
			return null;
		else
			return conditions;
	}
	
	public Field getField(String fieldName) {
		List<Field> fields = this.getFields();
		for(Field f: fields) {
			if(StringUtils.equalsIgnoreCase(fieldName, f.getName()) || 
					StringUtils.equalsIgnoreCase(fieldName, f.getAlias())) {
				return f;
			}
		}
		return null;
	}
	
}
