package cz.cron.fileds;

/**
 * field of hours
 * @author rdobra
 *
 */
public class Hour extends Field{
	
	private final static Integer FROM = 0;
	private final static Integer TO = 23;

	public Hour(String cronString) {
		super(FROM, TO,cronString);
	}
}
