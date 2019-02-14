package de.jnd.hki.controller;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.net.URL;
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

	public static File fileChose(Window window) {
		FileChooser fc = new FileChooser();
		return fc.showOpenDialog(window);
	}

	public static String getTargetLocation(){
		URL location = BaseUtils.class.getProtectionDomain().getCodeSource().getLocation();
		return location.getFile().replace("classes/","");
	}

	public static boolean isDebug(){
		return java.lang.management.ManagementFactory.getRuntimeMXBean().
				getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	}

	public static boolean isEclipse() {
		boolean isEclipse = System.getProperty("java.class.path").toLowerCase().contains("eclipse");
		return isEclipse;
	}

	public static boolean isIntelliJ() {
		boolean isIntelliJ = System.getProperty("java.class.path").toLowerCase().contains("idea");
		return isIntelliJ;
	}
}
