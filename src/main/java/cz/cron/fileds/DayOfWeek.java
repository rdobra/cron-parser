package cz.cron.fileds;

import java.util.List;

import cz.cron.CronParserException;

/**
 * field day of week
 * @author rdobra
 *
 */
public class DayOfWeek  extends Field {
	private final static Integer FROM = 1;
	private final static Integer TO = 7;

	public DayOfWeek(String cronString) {
		super(FROM, TO, cronString);
	}

	public List<Integer> getAllowedValuesFromString(String cronString) throws CronParserException{
		//Saturday could be both 0 and 7 -> we will use only one value - 7
		String normalizedCronString = convertNamesToIntegers(cronString).replaceAll("0", "7");
		List<Integer> vals = super.getAllowedValuesFromString(normalizedCronString);
		return vals;
		
	}
	
	/**
	 * Convert days names to its integer representation (for easier processing)
	 * @param cronString
	 * @return string where instead names of days are numbers
	 */
	protected String convertNamesToIntegers(String cronString){
		String str = cronString;
		
		for(DaysEnum m : DaysEnum.values()){
			str = str.replaceAll(m.abbrev, m.numStr);
		}
		
		return str;
	}
	
	enum DaysEnum{
		
		MONDAY("MON","1"),
		TUESDAY("TUE","2"),
		WEDNESDAY("WED","3"),
		THURSDAY("THU","4"),
		FRIDAY("FRI","5"),
		SATURDAY("SAT","6"),
		SUNDAY("SUN","7");
		
		String abbrev;
		String numStr;
		Integer num;
		
		private DaysEnum(String abbrev, String numStr) {
			this.abbrev = abbrev;
			this.numStr = numStr;
		}
		
		
	}

}
