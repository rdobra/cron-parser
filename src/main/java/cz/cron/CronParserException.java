package cz.cron;

/**
 * exception which can occur during processing cron string
 * 
 * @author rdobra
 *
 */
public class CronParserException extends Exception{

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
