package cz.cron.fileds;

import java.util.List;

import cz.cron.CronParserException;

/**
 * 
 * @author Jiří Nohavec
 *
 */
public class Month  extends Field {
	private final static Integer FROM = 1;
	private final static Integer TO = 12;

	public Month(String cronString) {
		super(FROM, TO, cronString);
	}

	public List<Integer> getAllowedValuesFromString(String cronString) throws CronParserException{
		return super.getAllowedValuesFromString(convertNamesToIntegers(cronString));
	}
	
	/**
	 * Convert months names to its integer representation (for easier processing)
	 * @param cronString
	 * @return string where instead names of months are numbers
	 */
	protected String convertNamesToIntegers(String cronString){
		String str = cronString;
		
		for(MonthsEnum m : MonthsEnum.values()){
			str = str.replaceAll(m.abbrev, m.numStr);
		}
		
		return str;
	}
	
	/**
	 * holds month abbreviation and its number representation
	 * @author rdobra
	 *
	 */
	enum MonthsEnum{
		JANUARY("JAN","1", 1),
		FEBRUARY("FEB", "2",2), 
		MARCH("MAR", "3",3),
		APRIL("APR", "4",4),
		MAY("MAY", "5",5),
		JUNE("JUN", "6",6),
		JULY("JUL", "7",7),
		AUGUST("AUG", "8",8),
		SEPTEMBER("SEP", "9",9),
		OCTOBER("OCT", "10",10),
		NOVEMBER("NOV", "11",11),
		DECEMBER("DEC", "12",12);
		
		String abbrev;
		String numStr;
		Integer num;
		
		private MonthsEnum(String abbrev, String numStr, Integer num) {
			this.abbrev = abbrev;
			this.numStr = numStr;
			this.num = num;
		}
		
		
	}
}
