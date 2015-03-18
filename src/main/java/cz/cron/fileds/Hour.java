package cz.cron.fileds;

import cz.cron.CronParserException;

/**
 * field of hours
 * @author rdobra
 *
 */
public class Hour extends Field{
	
	private final static Integer FROM = 0;
	private final static Integer TO = 23;

	public Hour(String cronString) throws CronParserException {
		super(FROM, TO,cronString);
	}
}
