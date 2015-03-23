package cz.cron;

import cz.cron.fileds.DayOfMonth;
import cz.cron.fileds.DayOfWeek;
import cz.cron.fileds.Hour;
import cz.cron.fileds.Minute;
import cz.cron.fileds.Month;

public class CronParser {

	/**
	 * Parses cron string.
	 * @param cronString string to be parsed
	 * @return cron filled with fields constructed from input string
	 * @throws CronParserException when an exception occurs in this method (which is btw. a completely redundant
     * comment)
	 */
	public Cron parse(String cronString) throws CronParserException{
		if(cronString == null || cronString.isEmpty()){ // why the case of "" is handled specially here?
			throw new CronParserException("empty input");
		}

		CronConstants c = CronConstants.getConstantFromString(cronString);

		if(c != null){
			cronString = c.cronExpression;
		}

        // doesn't allow more than one space separating two fields or tabs used as separators - the man page you used
        // doesn't say anything about whether this is allowed or not, so you should assume the worse and use, e.g.,
        // .split("[ \t]+") instead
		String[] fields = cronString.split(" ");

        // so '@reboot' leads to an error?
		if(fields.length != 5){
			throw new CronParserException("wrong number of input parameters");
		}

        // this code
        // ---
		Minute minute = new Minute(fields[0]);
		Hour hour = new Hour(fields[1]);
		DayOfMonth dayOfMonth = null;

        // this is wrong, see the man page: in this case, the cron should run when either of the fields matches
		if(!fields[2].equals("*") && !fields[4].equals("*")){
			throw new CronParserException("only one field for days could be set");
		}

		if(!fields[2].equals("*") || fields[4].equals("*")){
			dayOfMonth = new DayOfMonth(fields[2]);
		}
		Month month = new Month(fields[3]); // why this line is visually grouped with the previous code (the handling
        // of the day of month), which is btw. split into several groups?

		DayOfWeek dayOfWeek = null;
		if(!fields[4].equals("*") ){
			dayOfWeek = new DayOfWeek(fields[4]);
		}
        // ---
        // is terrible - what about something like this?
        // ---
        // Minute minute = new Minute(fields[0]);
        // Hour hour = new Hour(fields[1]);
        // DayOfMonth dayOfMonth = fields[2].equals("*") ? null : new DayOfMonth(fields[2]);
        // Month month = new Month(fields[3]);
        // DayOfWeek dayOfWeek = fields[4].equals("*") ? null : new DayOfWeek(fields[4]);

        // if (dayOfWeek != null && dayOfMonth != null) {
        //     throw new CronParserException("only one field for days could be set");
        // } else if (dayOfWeek == null && dayOfMonth == null) {
        //     dayOfWeek = new DayOfWeek("*");
        // }
        // ---

		return new Cron(minute, hour, dayOfWeek, month, dayOfMonth);
	}

	/**
	 * String constants used instead of cron strings.
	 *
	 * @author rdobra
	 *
	 */
	enum CronConstants{
		HOURLY("@hourly", "0 * * * *"),
		DAILY("@daily","0 0 * * *"),
		MONTHLY("@monthly","0 0 1 * *"),
		WEEKLY("@weekly","0 0 * * 0"),
		YEARLY("@yearly", "0 0 1 1 *"),
		ANNUALLY("@annually", "0 0 1 1 *"),
		REBOOT("@reboot","");

		String name;
		String cronExpression;

		private CronConstants(String name, String cronExpression){
			this.name = name;
			this.cronExpression = cronExpression;
		}

		static CronConstants getConstantFromString(String str){
			for(CronConstants c : CronConstants.values()){
				if(c.name.equals(str)){
					return c;
				}
			}

			return null;
		}
	}
}
