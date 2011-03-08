package com.songkick.snippets.util;

import java.util.logging.Logger;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.songkick.snippets.model.LogEntry;

public class Debug {
	private static final Logger log = Logger.getLogger(Debug.class.getName());
	private static final boolean ENABLE = true;

	
	static {
		ObjectifyService.register(LogEntry.class);
	}
	
	public static void log(String message) {
		if (ENABLE) {
			System.out.println(message);
		}
	}

	public static void error(String message) {
		log.severe(message);
	}
	
	public static void dbLog(String message) {
		LogEntry entry = new LogEntry();
		
		entry.timestamp();
		entry.setEntry(message);
		
		Objectify ofy = ObjectifyService.begin();
		ofy.put(entry);
	}
}
