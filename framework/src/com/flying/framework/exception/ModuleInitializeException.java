package com.flying.framework.exception;

@SuppressWarnings("serial")
public class ModuleInitializeException extends AppException {
	private final String moduleHome;

	public ModuleInitializeException(Throwable t, String moduleId, String moduleHome) {
		super(t, "Module[" + moduleId +"@" + moduleHome +"] init fail!");
		this.moduleHome = moduleHome;
	}

	public String getModuleHome() {
		return moduleHome;
	}
}
