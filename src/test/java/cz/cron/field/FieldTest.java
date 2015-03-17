package cz.cron.field;



import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import cz.cron.CronParserException;
import cz.cron.fileds.Hour;
import cz.cron.fileds.Minute;
import cz.cron.fileds.Month;

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
}
