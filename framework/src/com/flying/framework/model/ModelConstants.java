package com.flying.framework.model;

import com.flying.common.util.Codes;

public abstract class ModelConstants {
	public final static ActionResult SUCCESS = ModelFactory.createModelInstance(ActionResult.class, Codes.CODE, Codes.SUCCESS);
	public final static ActionResult FAIL = ModelFactory.createModelInstance(ActionResult.class, Codes.CODE, Codes.FAIL);
	
}
