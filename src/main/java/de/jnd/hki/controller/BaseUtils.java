package de.jnd.hki.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BaseUtils {
	public static Map<String,String> convertArgsToMap(String ... args ){
		Map<String,String> map = new HashMap<>();
		Arrays.stream(args).forEach((String x) ->{
			if(x.contains("=")){
				map.put(x.split("=")[0],x.split("=")[1]);
			}
		});
		return map;
	}
}
