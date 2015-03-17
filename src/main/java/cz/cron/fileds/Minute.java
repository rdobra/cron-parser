package cz.cron.fileds;

/**
 * field minutes
 * 
 * @author rdobra
 *
 */
public class Minute extends Field{
	
	private final static Integer FROM = 0;
	private final static Integer TO = 59;

	public Minute(String cronString) {
		super(FROM, TO,cronString);
	}
}
