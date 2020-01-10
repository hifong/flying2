package com.flying.security.service;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;
import com.flying.framework.service.AbstractService;

@Service("PermService")
public class PermService  extends AbstractService{

	@DaoCreate(entity="security.perm")
	public Data create(
			@Param(value="name", required=true)String name,
			@Param(value="tag",required=true)String tag,
			@Param(value="remarks",required=true)String remarks) throws Exception {
		return null;
	}

	@DaoUpdate(entity="security.perm")
	public Data update(
			@Param(value="name",required=true)String name,
			@Param(value="tag",required=true)String tag,
			@Param(value="remarks",required=true)String remarks,
			@Param(value="perm_id",required=true)long perm_id) throws Exception {
		return null;
	}

	@DaoRemove(entity="security.perm")
	public Data remove(
			@Param(value="perm_id",required=true)long perm_id) throws Exception {
		return null;
	}

	@DaoQuery(entity="security.perm")
	public Data findAll(Data request) throws Exception {
		return request;
	}

	@DaoQuery(entity="security.perm", 
			qsql="select * from (select p.*,ru.user_id from t_perm p, t_role_perm rp, t_role_user ru where p.perm_id=rp.perm_id and rp.role_id=ru.role_id) t")
	public Data findByUser(
			@Param(value="user_id",required=true)long user_id) throws Exception {
		return null;
	}
	
	@DaoQuery(entity="security.perm", 
			qsql="select * from (select p.*,rp.role_id from t_perm p, t_role_perm rp where p.perm_id=rp.perm_id) t")
	public Data findByRole(@Param(value="role_id",required=true)long role_id) throws Exception {
		return null;
	}
	
}
