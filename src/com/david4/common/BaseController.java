package com.david4.common;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.david4.console.Code;


public class BaseController extends GlobalBase{
	protected static final String PARAMETER = "common/parameter";
	protected static final String RESULT = "result";
	
	public String getRtnMsg(String code,String message){
		Map<String,String> map = new HashMap<String,String>();
		map.put(Code.CODE, code);
		map.put(Code.MESSAGE, message);
		return JSON.toJSONString(map);
	}
}
