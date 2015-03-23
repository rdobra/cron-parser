package cz.cron.fileds;

import cz.cron.CronParserException;

/**
 * Field for minutes.
 *
 * @author rdobra
 *
 */
public class Minute extends Field{

	private static final Integer FROM = 0;
	private static final Integer TO = 59;

	public Minute(String cronString) throws CronParserException {
		super(FROM, TO,cronString);
	}
}
