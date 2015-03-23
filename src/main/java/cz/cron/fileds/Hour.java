package cz.cron.fileds;

import cz.cron.CronParserException;

/**
 * Field for hours.
 * @author rdobra
 *
 */
public class Hour extends Field{

	private static final Integer FROM = 0;
	private static final Integer TO = 23;

	public Hour(String cronString) throws CronParserException {
		super(FROM, TO,cronString);
	}
}
