package com.flying.security.service;

import java.util.List;

import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;
import com.flying.framework.security.Principal;
import com.flying.framework.service.AbstractService;
import com.flying.pas.User;

@Service("AuthService")
public class AuthService  extends AbstractService{
	
	public Data auth(@Param(value="principal",required=true)Principal principal, 
			@Param(value="url",required=true)String url) throws Exception {
		User user = (User)principal;
		MenuService ms = module.getService(MenuService.class);
		Data menu = ms.findByUrl(url);
		long perm_id = menu.getLong("perm_id", 0);
		if(perm_id == 0) return new Data("authResult", true); //not protected url
		
		List<Long> perm_ids = user.getData().get("perm_ids");
		for(long pid: perm_ids) {
			if(pid == perm_id) 
				return new Data("authResult", true);
		}
		return new Data("authResult", true);
	}
}
