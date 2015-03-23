package cz.cron;


import java.util.Calendar;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CronCheckerTest {

	private String getDateAsTimestampString(int year, int month, int day, int hour, int minute){
		Calendar cFrom =  Calendar.getInstance();
		cFrom.set(year,month-1, day,hour, minute, 0);
        // what is this line for? Apparently completely redundant ... definitely the double call of getTime()
		cFrom.getTime().getTime();

		return Long.toString(cFrom.getTimeInMillis());
	}

	@Test
	public void testWillRun() throws CronParserException{

		Assert.assertTrue(CronChecker.getWillRun("5 0 * * *",
				getDateAsTimestampString(2012, 01, 02, 00, 00),
				getDateAsTimestampString(2012, 01, 02, 12, 10)));

		Assert.assertTrue(CronChecker.getWillRun("5 0 1 1 *",
				getDateAsTimestampString(2012, 01, 02, 00, 00),
				getDateAsTimestampString(2013, 01, 02, 12, 10)));

		//every 1st at 14:15
		Assert.assertTrue(CronChecker.getWillRun("15 14 1 * *",
				getDateAsTimestampString(2012, 01, 01, 00, 00),
				getDateAsTimestampString(2012, 01, 02, 12, 10)));

		Assert.assertFalse(CronChecker.getWillRun("15 14 1 * *",
				getDateAsTimestampString(2012, 1, 3, 9, 0),
				getDateAsTimestampString(2012, 1, 4, 12, 10)));

		Assert.assertTrue(CronChecker.getWillRun("0 0 12 * *",
				getDateAsTimestampString(2015, 2, 15, 10, 5),
				getDateAsTimestampString(2015, 3, 16, 9, 10)));

		Assert.assertTrue(CronChecker.getWillRun("0 0 12 * *",
				getDateAsTimestampString(2015, 2, 20, 10, 5),
				getDateAsTimestampString(2015, 3, 14, 9, 10)));

		Assert.assertFalse(CronChecker.getWillRun("0 0 12 * *",
				getDateAsTimestampString(2015, 2, 15, 10, 5),
				getDateAsTimestampString(2015, 3, 2, 12, 10)));


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

		Assert.assertTrue(CronChecker.getWillRun("0 22 2-10/2 * *",
				getDateAsTimestampString(2015, 02, 24, 00, 00),
				getDateAsTimestampString(2015, 03, 6, 12, 10)));

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
		Assert.assertFalse(CronChecker.getWillRun("5 4 * * Sun",
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


		Assert.assertTrue(CronChecker.getWillRun("30 */4 * * Mon-Fri",
				getDateAsTimestampString(2015, 3, 13, 0, 0),
				getDateAsTimestampString(2015, 3, 14, 12, 10)));

		Assert.assertFalse(CronChecker.getWillRun("30 */4 * * Mon-Fri",
				getDateAsTimestampString(2015, 3, 14, 0, 0),
				getDateAsTimestampString(2015, 3, 14, 12, 10)));

		Assert.assertFalse(CronChecker.getWillRun("30 */4 * * Mon-Fri",
				getDateAsTimestampString(2015, 3, 16, 2, 30),
				getDateAsTimestampString(2015, 3, 16, 3, 10)));

		Assert.assertTrue(CronChecker.getWillRun("30 */4 * * Mon-Fri",
				getDateAsTimestampString(2015, 3, 16, 2, 30),
				getDateAsTimestampString(2015, 3, 16, 5, 10)));

	}

	@Test
	public void testWillRunEnumeration() throws CronParserException{

		Assert.assertTrue(CronChecker.getWillRun("10,20,30 * * * *",
				getDateAsTimestampString(2015, 02, 24, 00, 10),
				getDateAsTimestampString(2015, 02, 24, 00, 10)));

		Assert.assertTrue(CronChecker.getWillRun("10,20,30 * * * *",
				getDateAsTimestampString(2015, 02, 24, 00, 10),
				getDateAsTimestampString(2015, 02, 24, 00, 20)));

		Assert.assertTrue(CronChecker.getWillRun("10,20,30 * * * *",
				getDateAsTimestampString(2015, 02, 24, 00, 5),
				getDateAsTimestampString(2015, 02, 24, 00, 15)));

		Assert.assertFalse(CronChecker.getWillRun("10,20,30 * * * *",
				getDateAsTimestampString(2015, 02, 24, 00, 12),
				getDateAsTimestampString(2015, 02, 24, 00, 15)));

		Assert.assertFalse(CronChecker.getWillRun("10,20,30 * * * *",
				getDateAsTimestampString(2015, 02, 24, 00, 40),
				getDateAsTimestampString(2015, 02, 24, 00, 50)));


	}
    
    @Test
    public void testShouldRun() throws CronParserException {
        Assert.assertTrue(CronChecker.getWillRun("* * * Feb *",
                getDateAsTimestampString(2014, 1, 1, 0, 0),
                getDateAsTimestampString(2015, 1, 1, 0, 0)));
    }

    @Test
    public void testShouldNotRun() throws CronParserException {
        // fails even when the line if(valuesBetween.size() >= 2){ isn't triggered - as 'from' and 'to' are mistakenly
        // swapped
        Assert.assertFalse(CronChecker.getWillRun("1 * * * *",
                getDateAsTimestampString(2015, 1, 1, 0, 59),
                getDateAsTimestampString(2015, 1, 1, 1, 0)));
    }

    // should go to CronParserTest
	/* =========== wrong, invalid inputs ========== */
	@Test(expectedExceptions = CronParserException.class)
	public void testDayOfMonthAndDayOfWeekSet() throws CronParserException{
		Assert.assertFalse(CronChecker.getWillRun("1 1 1 1 1",
				getDateAsTimestampString(2015, 02, 24, 00, 12),
				getDateAsTimestampString(2015, 02, 24, 00, 15)));

	}

	@Test(expectedExceptions = CronParserException.class)
	public void testTooBigDayOfWeek () throws CronParserException{

		Assert.assertFalse(CronChecker.getWillRun("1 1 * 3 9",
				getDateAsTimestampString(2015, 02, 24, 00, 12),
				getDateAsTimestampString(2015, 02, 24, 00, 15)));
	}

	@Test(expectedExceptions = CronParserException.class)
	public void testTooBigDayOfMonth () throws CronParserException{

		Assert.assertFalse(CronChecker.getWillRun("1 1 40 3 *",
				getDateAsTimestampString(2015, 02, 24, 00, 12),
				getDateAsTimestampString(2015, 02, 24, 00, 15)));
	}

	@Test(expectedExceptions = CronParserException.class)
	public void testTooBigMonth () throws CronParserException{

		Assert.assertFalse(CronChecker.getWillRun("1 1 * 18 *",
				getDateAsTimestampString(2015, 02, 24, 00, 12),
				getDateAsTimestampString(2015, 02, 24, 00, 15)));
	}

	@Test(expectedExceptions = CronParserException.class)
	public void testTooBigMinute () throws CronParserException{

		Assert.assertFalse(CronChecker.getWillRun("80 1 40 2 *",
				getDateAsTimestampString(2015, 02, 24, 00, 12),
				getDateAsTimestampString(2015, 02, 24, 00, 15)));
	}

	@Test(expectedExceptions = CronParserException.class)
	public void testWrongNumberOfArguments () throws CronParserException{

		Assert.assertFalse(CronChecker.getWillRun("1 1 ",
				getDateAsTimestampString(2015, 02, 24, 00, 12),
				getDateAsTimestampString(2015, 02, 24, 00, 15)));
	}
}
