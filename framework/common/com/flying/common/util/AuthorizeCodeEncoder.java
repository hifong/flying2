package com.flying.common.util;

import java.util.UUID;

public class AuthorizeCodeEncoder {
	
	public static String encode() throws Exception{
		UUID uuid = UUID.randomUUID();
		String md5Str= MD5.encode(uuid.toString()).toUpperCase();
		String rs = null;
		if(md5Str.length()<32){
			rs = md5Str;
			while(rs.length()<32){
				rs += "A";
			}
		}else{
			rs = md5Str.substring(0,32);
		}
		return rs;
	}
}
