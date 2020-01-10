package com.flying.framework.exception;


/**
 * @author wanghaifeng
 *
 */
@SuppressWarnings("serial")
public class ObjectNotFoundException extends AppException {
	private final String entity;
	protected String id;
	
	public ObjectNotFoundException(String entity, String id) {
		super(ErrorCode.OBJECT_NOT_FOUND);
		this.entity = entity;
		this.id = id;
	}
	
	public ObjectNotFoundException(String entity, String id, String message) {
		this(entity, id);
	}
	
	public ObjectNotFoundException(Throwable t, String entity, String id) {
		super(t, id);
		this.entity = entity;
		this.id = id;
	}

	@Override
	public String getMessage() {
		return this.entity+" id[" + id +"] not found in module[" + moduleId + "]";
	}

	@Override
	public String getLocalizedMessage() {
		return this.entity+" id[" + id +"] not found in module[" + moduleId + "]";
	}
	
}
