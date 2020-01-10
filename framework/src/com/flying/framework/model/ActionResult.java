package com.flying.framework.model;

import com.flying.common.util.Codes;
import com.flying.framework.annotation.Param;
import com.flying.framework.data.Data;

@SuppressWarnings("serial")
public class ActionResult extends Data  {

	@Param(Codes.CODE)
	private String returnCode;

	@Param(Codes.MSG)
	private String message;

	@Param(Codes.EFFECT_ROWS)
	private int EffectRows;

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

	public int getEffectRows() {
		return EffectRows;
	}

	public void setEffectRows(int effectRows) {
		EffectRows = effectRows;
	}
}
