package com.flying.project.service;

import com.flying.common.util.ServiceHelper;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;
import com.flying.framework.service.AbstractService;

@Service(value="SequenceService")
public class SequenceService extends AbstractService {
	public Data nextValue(@Param(value = "category", required=true) String category) throws Exception {
		return ServiceHelper.invoke("pas", "SequenceService:nextValue", new Data("category",category));
	}
	
}
