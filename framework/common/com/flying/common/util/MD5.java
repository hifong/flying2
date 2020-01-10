package com.flying.common.util;

import java.security.MessageDigest;

import com.flying.framework.exception.AppException;

/**
 * 
 */
public class MD5 {
	private static MessageDigest md5;

	static {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			throw new AppException(e, "Init MD5 Error!");
		}
	}

	public static String encode(String inStr) {
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	public static void main(String[] args) {
		String postString = encode("300000016134");
		System.out.println(postString);
		if (postString.equalsIgnoreCase("900150983cd24fb0d6963f7d28e17f72")) {
			System.out.println("true");
		} else
			System.out.println("false");
	}
}
