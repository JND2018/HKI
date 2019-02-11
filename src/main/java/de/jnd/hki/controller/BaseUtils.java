package de.jnd.hki.controller;

import javax.swing.*;
import java.io.File;
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

	public static File fileChose() {
		JFileChooser fc = new JFileChooser();
		int ret = fc.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file;
		} else {
			return null;
		}
	}
}
