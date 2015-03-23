package cz.cron.field;



import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import cz.cron.CronParserException;
import cz.cron.fileds.DayOfMonth;
import cz.cron.fileds.DayOfWeek;
import cz.cron.fileds.Hour;
import cz.cron.fileds.Minute;
import cz.cron.fileds.Month;
import java.util.Collection;
import java.util.HashSet;

public class FieldTest {
	private final static Logger LOG = Logger.getLogger(FieldTest.class.getName());

	@Test
	public void testParseInvalidString() throws CronParserException{
		Hour h = new Hour("a");
		Assert.assertNull(h.getAllowedValues());
	}

	@Test
	public void testParseEvery() throws CronParserException{
		Hour h = new Hour("*");

		Assert.assertTrue(getAreListEquals(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,
				11,12,13,14,15,16,17,18,19,20,21,22,23),
					h.getAllowedValues()));

	}

	@Test
	public void testParseEnumeration() throws CronParserException{
		Minute m = new Minute("5,10,15");

		Assert.assertTrue(getAreListEquals(Arrays.asList(5,10,15),
					m.getAllowedValues()));
	}

	@Test
	public void testParseMonthEnumeration() throws CronParserException{
		Month m = new Month("FEB,DEC");

		Assert.assertTrue(getAreListEquals(Arrays.asList(2,12),
				m.getAllowedValues()));
	}

	@Test
	public void testParseInterval() throws CronParserException{
		Minute m = new Minute("5-15");

		Assert.assertTrue(getAreListEquals(Arrays.asList(5,6,7,8,9,10,11,12,13,14,15),
					m.getAllowedValues()));
	}

	@Test
	public void testParseMonthInterval() throws CronParserException{
		Month m = new Month("MAR-JUL");

		Assert.assertTrue(getAreListEquals(Arrays.asList(3,4,5,6,7),
				m.getAllowedValues()));
	}

	@Test
	public void testParseIncrement() throws CronParserException{
		Minute m = new Minute("5/15");

		Assert.assertTrue(getAreListEquals(Arrays.asList(5,20,35,50),
					m.getAllowedValues()));

		m = new Minute("*/10");

		Assert.assertTrue(getAreListEquals(Arrays.asList(0,10,20,30,40,50),
					m.getAllowedValues()));
	}



	@Test
	public void testParseIncrementInterval() throws CronParserException{
		Minute m = new Minute("10-16/2");

		Assert.assertTrue(getAreListEquals(Arrays.asList(10,12,14,16),
					m.getAllowedValues()));

	}

	@Test
	public void testParseListOfIntervals() throws CronParserException{
		Minute m = new Minute("10-16,20-22");

		Assert.assertTrue(getAreListEquals(Arrays.asList(10,11,12,13,14,15,16,20,21,22),
					m.getAllowedValues()));

	}

	// what about Assert.assertEquals(actual, expected) instead? (The order of actual & expected is really like that in
    // TestNG as I just (btw.) noticed)
	private boolean getAreListEquals(List<Integer> expected, List<Integer> actual){
		if(expected == null && actual != null){
			return false;
		}

		if(expected != null && actual == null){
			return false;
		}

		if(expected == null && actual == null){
			return true;
		}

		if(expected.size() != actual.size()){
			LOG.error("sizes are not equal, expected " + expected.size() + ", actual " + actual.size());
			return false;
		}

		for(int i = 0; i < expected.size();i++){
			if(!expected.get(i).equals(actual.get(i))){
				LOG.error("values at postion " + i + " are not equals, expected "
						+ expected.get(i) + ", actual " + actual.get(i));
				return false;
			}
		}

		return true;


	}

    @Test
    public void testMonthInvalid() throws CronParserException {
        Assert.assertNull(new Month("JANFEB").getAllowedValues());
    }

    @Test
    public void testMonthInvalid2() throws CronParserException {
        Assert.assertNull(new Month("0").getAllowedValues());
    }

    @Test
    public void testDayOfMonthInvalid() throws CronParserException {
        Assert.assertNull(new DayOfMonth("0").getAllowedValues());
    }

    @Test
    public void testFieldInvalid() throws CronParserException {
        Assert.assertNull(new Minute("0,,,").getAllowedValues());
    }

    @Test
    public void testDayOfWeekIntervalWithSunday() throws CronParserException {
        assertEqualsAsSets(Arrays.asList(1, 2), new DayOfWeek("1-2").getAllowedValues());
        assertEqualsAsSets(Arrays.asList(1, 2, 7), new DayOfWeek("0-2").getAllowedValues());
    }

    @Test
    public void testFieldStepOnSingleValue() throws CronParserException {
        Assert.assertEquals(Arrays.asList(30), new Minute("30-30/2").getAllowedValues());

        List<Integer> allowedValues = new Minute("30/2").getAllowedValues();
        Assert.assertTrue(allowedValues == null || allowedValues.equals(Arrays.asList(30)));
    }

    @Test
    public void testFieldStepOnStar() throws CronParserException {
        List<Integer> expMonths = Arrays.asList(1, 4, 7, 10);
        assertEqualsAsSets(expMonths, new Month("1-12/3").getAllowedValues());
        assertEqualsAsSets(expMonths, new Month("*/3").getAllowedValues());
    }

    private static void assertEqualsAsSets(Collection<?> expected, Collection<?> actual) {
        Assert.assertEquals(new HashSet<>(actual), new HashSet<>(expected));
    }
}
