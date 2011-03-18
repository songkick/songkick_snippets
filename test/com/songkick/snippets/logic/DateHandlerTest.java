package com.songkick.snippets.logic;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

public class DateHandlerTest {

	private boolean checkWeek(String dateString, Long expectedWeek) {
		DateTime date = new DateTime(dateString);
		Long week = DateHandler.getWeekNumber(date);
		
		return week==expectedWeek;
	}
	
	@Test
	public void testWeekIncrement() {
		assertTrue(checkWeek("2011-01-17T23:58", 1L));
		assertTrue(checkWeek("2011-01-17T23:59", 2L));
		assertTrue(checkWeek("2011-01-17T12:30", 1L));
		assertTrue(checkWeek("2011-01-18T00:00", 2L));
	}
}
