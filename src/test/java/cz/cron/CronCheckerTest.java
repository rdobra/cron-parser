package cz.cron;


import java.util.Calendar;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CronCheckerTest {

	private String getDateAsTimestampString(int year, int month, int day, int hour, int minute){
		Calendar cFrom =  Calendar.getInstance();
		cFrom.set(year,month-1, day,hour, minute, 0);
		cFrom.getTime().getTime();
		
		return "" + cFrom.getTimeInMillis();
	}
	

	
	@Test
	public void testWillRun() throws CronParserException{

		Assert.assertTrue(CronChecker.getWillRun("5 0 * * *", 
				getDateAsTimestampString(2012, 01, 02, 00, 00), 
				getDateAsTimestampString(2012, 01, 02, 12, 10)));
		
		//every 1st at 14:15
		Assert.assertTrue(CronChecker.getWillRun("15 14 1 * *", 
				getDateAsTimestampString(2012, 01, 01, 00, 00), 
				getDateAsTimestampString(2012, 01, 02, 12, 10)));
		
		Assert.assertFalse(CronChecker.getWillRun("15 14 1 * *", 
				getDateAsTimestampString(2012, 01, 03, 00, 00), 
				getDateAsTimestampString(2012, 01, 04, 12, 10)));

		
	}
	
	@Test
	public void testWillRunWeekDay() throws CronParserException{
		Assert.assertTrue(CronChecker.getWillRun("0 22 * * 1-5", 
				getDateAsTimestampString(2015, 03, 15, 00, 00), 
				getDateAsTimestampString(2015, 03, 18, 12, 10)));
		
		Assert.assertFalse(CronChecker.getWillRun("0 22 * * 1-5", 
				getDateAsTimestampString(2015, 03, 14, 00, 00), 
				getDateAsTimestampString(2015, 03, 15, 12, 10)));
		
		Assert.assertFalse(CronChecker.getWillRun("0 22 * * 1-5", 
				getDateAsTimestampString(2015, 03, 14, 00, 00), 
				getDateAsTimestampString(2015, 03, 16, 12, 10)));

		Assert.assertTrue(CronChecker.getWillRun("0 22 * * 1-5", 
				getDateAsTimestampString(2015, 03, 14, 00, 00), 
				getDateAsTimestampString(2015, 03, 17, 12, 10)));
		
	}
	
	@Test
	public void testWillRunIntervalWithStep() throws CronParserException{
		
//		Assert.assertTrue(CronChecker.getWillRun("0 22 2-10/2 * *", 
//				getDateAsTimestampString(2015, 02, 24, 00, 00), 
//				getDateAsTimestampString(2015, 03, 6, 12, 10)));
		
		Assert.assertTrue(CronChecker.getWillRun("0 22 2-10/2 * *", 
				getDateAsTimestampString(2015, 02, 24, 00, 00), 
				getDateAsTimestampString(2015, 03, 3, 12, 10)));
		
		Assert.assertFalse(CronChecker.getWillRun("0 22 2-10/2 * *", 
				getDateAsTimestampString(2015, 02, 24, 00, 00), 
				getDateAsTimestampString(2015, 03, 2, 12, 10)));
	}
	
	@Test
	public void testWillRunSundays() throws CronParserException{
		
		//sunday, bad hours
		Assert.assertFalse(CronChecker.getWillRun("5 4 * * sun", 
				getDateAsTimestampString(2015, 03, 15, 00, 00), 
				getDateAsTimestampString(2015, 03, 15, 01, 00)));
		
		//saturday
		Assert.assertFalse(CronChecker.getWillRun("5 4 * * sun", 
				getDateAsTimestampString(2015, 03, 14, 00, 00), 
				getDateAsTimestampString(2015, 03, 14, 10, 00)));
		
		Assert.assertTrue(CronChecker.getWillRun("5 4 * * sun", 
				getDateAsTimestampString(2015, 03, 15, 00, 00), 
				getDateAsTimestampString(2015, 03, 15, 9, 00)));
	}
	
	@Test
	public void testNickNames() throws CronParserException{
		Assert.assertTrue(CronChecker.getWillRun("@monthly", 
				getDateAsTimestampString(2015, 03, 1, 00, 00), 
				getDateAsTimestampString(2015, 03, 15, 9, 00)));
		
		Assert.assertTrue(CronChecker.getWillRun("@monthly", 
				getDateAsTimestampString(2015, 03, 29, 00, 00), 
				getDateAsTimestampString(2015, 04, 15, 9, 00)));
		
		Assert.assertFalse(CronChecker.getWillRun("@monthly", 
				getDateAsTimestampString(2015, 03, 1, 10, 00), 
				getDateAsTimestampString(2015, 03, 15, 9, 00)));
		
		Assert.assertFalse(CronChecker.getWillRun("@monthly", 
				getDateAsTimestampString(2015, 03, 15, 00, 00), 
				getDateAsTimestampString(2015, 03, 15, 9, 00)));
		
	}
	
	@Test
	public void testWillRunMoreIntervals() throws CronParserException{
		
		Assert.assertTrue(CronChecker.getWillRun("0 22 1-3,7-9 * *", 
				getDateAsTimestampString(2015, 02, 24, 00, 00), 
				getDateAsTimestampString(2015, 03, 6, 12, 10)));
		
		Assert.assertTrue(CronChecker.getWillRun("0 22 1-3,7-9 * *", 
				getDateAsTimestampString(2015, 02, 8, 00, 00), 
				getDateAsTimestampString(2015, 02, 10, 12, 10)));
		
		Assert.assertFalse(CronChecker.getWillRun("0 22 1-3,7-9 * *", 
				getDateAsTimestampString(2015, 02, 5, 00, 00), 
				getDateAsTimestampString(2015, 02, 6, 12, 10)));
		
		Assert.assertFalse(CronChecker.getWillRun("0 22 1-3,7-9 * *", 
				getDateAsTimestampString(2015, 02, 10, 00, 00), 
				getDateAsTimestampString(2015, 02, 12, 12, 10)));
		
		Assert.assertFalse(CronChecker.getWillRun("0 22 7-9,10-15 * *", 
				getDateAsTimestampString(2015, 02, 16, 00, 00), 
				getDateAsTimestampString(2015, 03, 5, 12, 10)));
	
	}
}
