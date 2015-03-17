package cz.cron.fileds;

import java.util.List;

import cz.cron.CronParserException;

/**
 * field day of month
 * @author rdobra
 *
 */
public class DayOfMonth  extends Field {
	private final static Integer FROM = 0;
	private final static Integer TO = 31;

	public DayOfMonth(String cronString) {
		super(FROM, TO, cronString);
	}

	public List<Integer> getAllowedValuesFromString(String cronString) throws CronParserException{
		return super.getAllowedValuesFromString(cronString);
	}
	
}
