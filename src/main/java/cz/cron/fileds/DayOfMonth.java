package cz.cron.fileds;

import cz.cron.CronParserException;



/**
 * Field for day of month.
 * @author rdobra
 *
 */
public class DayOfMonth  extends Field {
	private final static Integer FROM = 1;  // that quite changes the behavior ...
	private final static Integer TO = 31;

	public DayOfMonth(String cronString) throws CronParserException {
		super(FROM, TO, cronString);
	}

}
