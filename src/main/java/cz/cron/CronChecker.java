package cz.cron;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.log4j.Logger;

/**
 * 
 * http://unixhelp.ed.ac.uk/CGI/man-cgi?crontab+5
 * 
 * expected arguments:
 * 
 * <code><cron string> <from timestamp> <to timestamp></code>
 * 
 * 
 * 
 * @author rdobra
 *
 */
public class CronChecker {
	
	private final static Logger LOG = Logger.getLogger(CronChecker.class.getName());
	
	public static void main(String[] args) {
		if (args.length != 3){
			LOG.info("bad number of arguments\n" + "usage: <cron string> <from timestamp> <to timestamp>");
			return;	
		}
		
		try {
			boolean willRun = getWillRun(args[0], args[1], args[2]);
			if(willRun){
				LOG.info("process will not run between given interval");
			}else{
				LOG.info("process will run between given interval");
			}
		} catch (CronParserException e) {
			LOG.error("error while processing crong string, error message " + e.getMessage());
		}
		
		
	}
	
	public static boolean getWillRun(String cronString,String from, String to) throws CronParserException{
		
		long fromLong = Long.parseLong(from);
		Long toLong = Long.parseLong(to);
		LocalDateTime dtFrom =  LocalDateTime.ofInstant(Instant.ofEpochMilli(fromLong), ZoneId.systemDefault());
		LocalDateTime dtTo =  LocalDateTime.ofInstant(Instant.ofEpochMilli(toLong), ZoneId.systemDefault());
		
		if(dtTo.isBefore(dtFrom)){
			throw new CronParserException("date to is before date from");
		}
		
		return getWillRun(cronString, dtFrom, dtTo);
		
	}
	
	public static boolean getWillRun(String cronString,LocalDateTime from, LocalDateTime to) throws CronParserException{
		if(LOG.isDebugEnabled()){
			LOG.debug("getWillRun(" +  cronString + ", " + from + ", " + to + ")");
		}

		CronParser parser = new CronParser();
		Cron c = parser.parse(cronString);
		
		return c.getWillRun(from, to);
		
	}
}
