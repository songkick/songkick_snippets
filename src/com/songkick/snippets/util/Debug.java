package com.songkick.snippets.util;

import java.util.logging.Logger;

public class Debug {
	private static final Logger log = Logger.getLogger(Debug.class.getName());
	private static final boolean ENABLE = true;
	
	public static void log(String message) {
		if (ENABLE) {
			System.out.println(message);
		}
	}

	public static void error(String message) {
		log.severe(message);
	}
}
