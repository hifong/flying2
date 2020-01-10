package com.flying.security.service;

import java.util.Date;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;
import com.flying.framework.service.AbstractService;

@Service("TokenService")
public class TokenService extends AbstractService {
	@DaoCreate(entity="security.token")
	public Data create(@Param(value = "user_id", required=true)long user_id,
			@Param(value = "token", required=true)String token,
			@Param(value = "expired", required=true)Date expired) {
		return null;
	}
	
	@DaoQuery(entity="security.token")
	public Data findByToken(@Param(value = "token", required=true)String token) {
		return null;
	}
}
