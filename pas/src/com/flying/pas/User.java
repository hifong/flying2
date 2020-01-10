package com.flying.pas;

import com.flying.framework.data.Data;
import com.flying.framework.security.Principal;

public class User implements Principal{
	private static final long serialVersionUID = -4739579737873262575L;
	
	private final Data data;
	
	public User(Data data) {
		this.data = data;
	}
	
	@Override
	public String getId() {
		return data.getString("user_id");
	}

	@Override
	public String getName() {
		return data.getString("real_name");
	}

	@Override
	public boolean isRole(String[] roles) {
		return true;
	}

	@Override
	public boolean hasPermission(String[] permissions) {
		return true;
	}

	public String[] getPerms() {
		return data.getStrings("perms");
	}
	
	public Data getData() {
		return data;
	}
}
