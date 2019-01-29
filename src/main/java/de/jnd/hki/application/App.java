package de.jnd.hki.application;

import de.jnd.hki.controller.BaseUtils;

import java.util.Map;

public class App {

	//mode=gui,console(default)
	public static void main(String[] args) {
		Map<String, String> argsMap = BaseUtils.convertArgsToMap(args);

		switch (argsMap.get("mode") == null ? "" : argsMap.get("mode")) {
		case "gui":
			Gui.main(null);
			break;
		case "console":
			break;
		default:
			System.out.println("default");
			break;
		}
	}

}
