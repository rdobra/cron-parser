package cz.cron;

import cz.cron.fileds.DayOfMonth;
import cz.cron.fileds.DayOfWeek;
import cz.cron.fileds.Hour;
import cz.cron.fileds.Minute;
import cz.cron.fileds.Month;

public class CronParser {

	/**
	 * parses cron string
	 * @param cronString - string to be parsed
	 * @return cron filled with fields constructed from input string
	 * @throws CronParserException - when exception during occur
	 */
	public Cron parse(String cronString) throws CronParserException{
		if(cronString == null || cronString.isEmpty()){
			throw new CronParserException("empty input");
		}
		
		CronConstants c = CronConstants.getConstantFromString(cronString);
		
		if(c != null){
			cronString = c.cronExpresssion;
		}
		
		String[] fields = cronString.split(" ");
		
		if(fields.length != 5){
			throw new CronParserException("wrong number of input parameters");
		}
		
		Minute minute = new Minute(fields[0]);
		Hour hour = new Hour(fields[1]);
		DayOfMonth dayOfMonth = null;
		if(!fields[2].equals("*") || fields[4].equals("*")){
			dayOfMonth = new DayOfMonth(fields[2]);
		}
		Month month = new Month(fields[3]);
		
		DayOfWeek dayOfWeek = null;
		if(!fields[4].equals("*") ){
			dayOfWeek = new DayOfWeek(fields[4]);
		}
		
		
		return new Cron(minute, hour, dayOfWeek, month, dayOfMonth);
	}
	
	/**
	 * string constants used instead of cron strings
	 * 
	 * @author rdobra
	 *
	 */
	enum CronConstants{
		HOURLY("@hourly", "0 * * * *"),
		DAILY("@daily","0 0 * * *"),
		MONTHLY("@monthly","0 0 1 * *"),
		WEEKLY("@weekly","0 0 * * 0"),
		YEARLY("", "0 0 1 1 *"),
		ANNUALY("@hourly", "0 0 1 1 *"),
		REBOOT("@reboot","");
		
		String name;
		String cronExpresssion;
		
		private CronConstants(String name, String cronExpression){
			this.name = name;
			this.cronExpresssion = cronExpression;
		}
		
		static CronConstants getConstantFromString(String str){
			for(CronConstants c : CronConstants.values()){
				if(c.name.equals(str)){
					return c;
				}
			}
			
			return null;
		}
	}
}
