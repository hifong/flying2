package com.flying.security.service;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.common.annotation.Transaction;
import com.flying.common.util.Codes;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;
import com.flying.framework.service.AbstractService;

@Service("RoleService")
public class RoleService  extends AbstractService{

	@DaoCreate(entity="security.role")
	public Data create(
			@Param(value="name", required=true)String name,
			@Param(value="remarks",required=true)String remarks) throws Exception {
		return null;
	}

	@DaoUpdate(entity="security.role")
	public Data update(
			@Param(value="name",required=true)String name,
			@Param(value="remarks",required=true)String remarks,
			@Param(value="role_id",required=true)long role_id) throws Exception {
		return null;
	}

	@DaoRemove(entity="security.role")
	public Data remove(
			@Param(value="role_id",required=true)long role_id) throws Exception {
		return null;
	}

	@DaoQuery(entity="security.role")
	public Data findAll(Data request) throws Exception {
		return request;
	}
	
	@DaoQuery(entity="security.role", 
			qsql="select * from (select r.*,ru.user_id from t_role r, t_role_user ru where r.role_id=ru.role_id) t")
	public Data findByUser(@Param(value="user_id",required=true)long user_id) throws Exception {
		return null;
	}
	
	//
	@DaoCreate(entity="security.role_user")
	public Data addRoleToUser(
			@Param(value="role_id", required=true)long role_id,
			@Param(value="user_id",required=true)long user_id) throws Exception {
		return null;
	}

	@DaoRemove(entity="security.role_user")
	public Data removeRoleUser(
			@Param(value="role_id", required=true)long role_id,
			@Param(value="user_id",required=true)long user_id) throws Exception {
		return null;
	}

	@DaoRemove(entity="security.role_user")
	public Data removeByUser(
			@Param(value="user_id",required=true)long user_id) throws Exception {
		return null;
	}

	@DaoCreate(entity="security.role_perm")
	public Data addPermToRole(
			@Param(value="role_id", required=true)long role_id,
			@Param(value="perm_id",required=true)long perm_id) throws Exception {
		return null;
	}

	@DaoRemove(entity="security.role_perm")
	public Data removeRolePerm(
			@Param(value="role_id", required=true)long role_id,
			@Param(value="perm_id",required=true)long perm_id) throws Exception {
		return null;
	}

	@DaoRemove(entity="security.role_perm")
	public Data removeByRole(
			@Param(value="role_id", required=true)long role_id) throws Exception {
		return null;
	}
	//
	@Transaction
	public Data createWithPerms(
			@Param(value="name", required=true)String name,
			@Param(value="remarks",required=true)String remarks,
			@Param(value="perm_ids",required=true)long[] perm_ids) throws Exception {
		Data d = this.create(name, remarks);
		long role_id = d.getLong("role_id", 0);
		if(perm_ids != null)
			for(long perm_id: perm_ids) {
				this.addPermToRole(role_id, perm_id);
			}
		return new Data(Codes.CODE, Codes.SUCCESS);
	}

	@Transaction
	public Data updateWithPerms(
			@Param(value="name", required=true)String name,
			@Param(value="remarks",required=true)String remarks,
			@Param(value="perm_ids",required=true)long[] perm_ids,
			@Param(value="role_id",required=true)long role_id) throws Exception {
		this.update(name, remarks, role_id);
		this.removeByRole(role_id);
		if(perm_ids != null)
			for(long perm_id: perm_ids) {
				this.addPermToRole(role_id, perm_id);
			}
		return new Data(Codes.CODE, Codes.SUCCESS);
	}

	@Transaction
	public Data removeWithPerms(
			@Param(value="role_id",required=true)long role_id) throws Exception{
		this.removeByRole(role_id);
		this.remove(role_id);
		return new Data(Codes.CODE, Codes.SUCCESS);
	}
}
