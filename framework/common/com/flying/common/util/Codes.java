package com.flying.common.util;

public interface Codes {
	public final static String HEADS="$.headers";
	public final static String HEAD_USERID="x-userid";
	
	public final static String CODE 		= "ReturnCode";
	public final static String ROWS 		= "rows";
	public final static String TOTAL_ROWS 	= "total";
	public final static String EFFECT_ROWS 	= "EffectRows";
	public final static String MSG 			= "Msg";
	public final static String TID 			= "TransactionID";
	public final static String SERVICE_NOTICE		= "$Notice";
	//翻页参数
	public final static String PAGE_NUM 	= "page";
	public final static String PAGE_SIZE 	= "rows";
	
	public final static int SUCCESS 					= 0;
	public final static int WORKING_NONE				= 9999;
	public final static int FAIL 						= 1;
	public final static int INTERNAL_ERROR 				= 2;
	public final static int INVALID_PARAM 				= 3;
	public final static int UPDATING_OBJECT_NOT_EXISTS 	= 4;	//正在修改一个不存在的对象
	public final static int MSG_NOT_EXISTS				= 5;
	public final static int OBJECT_NOT_EXISTS			= 6;

	public final static int AUTH_FAIL 			= 300;
	public final static int TOKEN_EXPIRES 		= 301;
	public final static int TOKEN_NOT_EXISTS 	= 302;
	
	public final static int TASK_NOT_EXISTS = 401;
	public final static int TAG_NOT_EXISTS = 402;
}
