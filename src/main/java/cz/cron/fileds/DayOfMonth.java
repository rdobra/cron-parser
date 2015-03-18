package cz.cron.fileds;

import cz.cron.CronParserException;



/**
 * field day of month
 * @author rdobra
 *
 */
public class DayOfMonth  extends Field {
	private final static Integer FROM = 0;
	private final static Integer TO = 31;

	public DayOfMonth(String cronString) throws CronParserException {
		super(FROM, TO, cronString);
	}
	
}
