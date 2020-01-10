package com.flying.framework.async;

import com.flying.framework.data.Data;

public interface Processor {
	Data process(String moduleId, String serviceId, Data data);
}
