package com.songkick.snippets.model;

import javax.persistence.Id;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LogEntry implements Comparable<LogEntry> {
	@Id
	private Long id = null;
	private String entry = null;
	private String date = null;

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public Long getId() {
		return id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public void timestamp() {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:MM:SS");
		
		date = new DateTime().toString(formatter);
	}
	
	public DateTime getDateTime() {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:MM:SS");
		
		return formatter.parseDateTime(date);
	}

	@Override
	public int compareTo(LogEntry o) {
		DateTimeComparator comparator = DateTimeComparator.getInstance();
		
		return comparator.compare(o.getDateTime(), getDateTime());
	}

}
