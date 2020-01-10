package com.flying.security.service;

import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.common.annotation.Transaction;
import com.flying.common.util.Codes;
import com.flying.common.util.MD5;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;
import com.flying.framework.service.AbstractService;

@Service("UserService")
public class UserService  extends AbstractService{
	
	@MethodInfo("创建新用户")
	@DaoCreate(entity="security.user")
	public Data create(
			@Param(value="username", required=true,maxlength=30, desc="登录用户名")String username,
			@Param(value="password",required=true, desc="密码")String password,
			@Param(value="org_name",required=true, desc="组织机构")String org_name,
			@Param(value="mail",required=false, desc="邮箱")String mail,
			@Param(value="real_name",required=true, maxlength=30, desc="用户实名")String real_name) throws Exception {
		return new Data("password", MD5.encode(password));
	}

	@DaoUpdate(entity="security.user")
	public Data update(
			@Param(value="org_name",required=true, desc="组织机构")String org_name,
			@Param(value="mail",required=false, desc="邮箱")String mail,
			@Param(value="real_name",required=true, maxlength=30, desc="用户实名")String real_name,
			@Param(value="user_id",required=true, desc="用户ID")long user_id) throws Exception {
		return null;
	}

	@DaoUpdate(entity="security.user")
	public Data changePassword(
			@Param(value="password",required=true)String password,
			@Param(value="user_id",required=true)long user_id) throws Exception {
		return new Data("password", MD5.encode(password), "user_id", user_id);
	}

	@DaoRemove(entity="security.user")
	public Data remove(
			@Param(value="user_id",required=true)long user_id) throws Exception {
		return null;
	}

	@DaoRemove(entity="security.user")
	public Data removeByUsername(
			@Param(value="username",required=true)String username) throws Exception {
		return null;
	}
	
	@DaoQuery(single=true, entity="security.user.perms")
	public Data findByUsernamePassword(@Param(value="username",required=true)String username,
			@Param(value="password",required=true)String password) throws Exception {
		return new Data("password", MD5.encode(password), "username", username);
	}
	
	@DaoQuery(entity="security.user")
	public Data findByUsernameRealname(
			@Param(value="username")String username,
			@Param(value="real_name", tag="like")String realname) throws Exception {
		return null;
	}
	
	@DaoQuery(entity="security.user", single=true)
	public Data findById(
			@Param(value="user_id",required=true)long user_id) throws Exception {
		return null;
	}
	
	@DaoQuery(entity="security.user")
	public Data findAll(Data request) throws Exception {
		return request;
	}
	
	//
	@Transaction
	public Data createWithRoles(
			@Param(value="username", required=true)String username,
			@Param(value="password",required=true)String password,
			@Param(value="org_name",required=true)String org_name,
			@Param(value="mail",required=false, desc="邮箱")String mail,
			@Param(value="real_name",required=true)String real_name, 
			@Param(value="role_ids",required=true)long[] role_ids
			) throws Exception {
		Data d = this.create(username, password, org_name, mail, real_name);
		RoleService rs = this.module.getService(RoleService.class);
		if(role_ids != null)
			for(long r: role_ids) {
				rs.addRoleToUser(r, d.getLong("user_id",0));
			}
		return new Data(Codes.CODE, Codes.SUCCESS);
	}

	@Transaction
	public Data updateWithRoles(
			@Param(value="org_name",required=true)String org_name,
			@Param(value="mail",required=false, desc="邮箱")String mail,
			@Param(value="real_name",required=true)String real_name,
			@Param(value="role_ids",required=true)long[] role_ids,
			@Param(value="user_id",required=true)long user_id
			) throws Exception {
		this.update(org_name, mail, real_name, user_id);
		RoleService rs = this.module.getService(RoleService.class);
		rs.removeByUser(user_id);
		if(role_ids != null)
			for(long r: role_ids) {
				rs.addRoleToUser(r, user_id);
			}
		return new Data(Codes.CODE, Codes.SUCCESS);
	}

	@Transaction
	public Data removeWithRoles(
			@Param(value="user_id",required=true)long user_id) throws Exception{
		RoleService rs = this.module.getService(RoleService.class);
		rs.removeByUser(user_id);
		
		this.remove(user_id);
		return new Data(Codes.CODE, Codes.SUCCESS);
	}
}
