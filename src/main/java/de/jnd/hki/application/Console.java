package de.jnd.hki.application;

import org.apache.log4j.Logger;

public class Console {
	private static Logger log = Logger.getLogger(Console.class);

	public static void main(String[] args) {
		log.info("Console app loaded.");
		while (true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.error("Thread sleep failed",e);
			}
		}
	}
}
