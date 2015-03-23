package cz.cron;

/**
 * Exception which can occur during processing of cron string.
 *
 * @author rdobra
 *
 */
public class CronParserException extends Exception{

    // what is the following empty comment for?
	/**
	 *
	 */
	private static final long serialVersionUID = 8822076407263129730L;

	public CronParserException(){
		super();
	}

	public CronParserException(String message){
		super(message);
	}

	public CronParserException(Throwable t){
		super(t);
	}

	public CronParserException(String msg, Throwable t){
		super(msg, t);
	}

}
