package cz.cron.fileds;

import java.util.List;

import cz.cron.CronParserException;

/**
 * Field for month.
 *
 * @author rdobra
 *
 */
public class Month  extends Field {
    // there is a recommended canonical order of the field modifiers defined by the Java language specification. It's
    // good to follow it
    private static final Integer FROM = 1;
    private static final Integer TO = 12;

	public Month(String cronString) throws CronParserException {
		super(FROM, TO, cronString);
	}

    // making this method public completely confuses the users of the class ... seeing this method, they would wonder if
    // on an instance of the class (during whose construction they had to pass a cron string as an argument) they should
    // call getAllowedValues() or this method. And if this method, which cron string will matter? That from the
    // constructor or this one?
	@Override
	protected List<Integer> getAllowedValuesFromString(String cronString) throws CronParserException{
		return super.getAllowedValuesFromString(convertNamesToIntegers(cronString));
	}

	/**
	 * Converts month names to their integer representation.
	 * @param cronString
	 * @return string where instead of names of months are numbers
	 */
	protected String convertNamesToIntegers(String cronString){
		String str = cronString;

        // the following can, for instance, change (an invalid) "JANFEB" to (a valid) "12". See
        // FieldTest.testMontInvalid()
		for(MonthsEnum m : MonthsEnum.values()){
			str = str.replaceAll(m.abbrev, m.numStr);
		}

		return str;
	}

	/**
	 * Holds month abbreviation and its number representation.
	 * @author rdobra
	 *
	 */
	enum MonthsEnum{
		JANUARY("JAN","1", 1),
		FEBRUARY("FEB", "2",2),
		MARCH("MAR", "3",3),
		APRIL("APR", "4",4),
		MAY("MAY", "5",5),
		JUNE("JUN", "6",6),
		JULY("JUL", "7",7),
		AUGUST("AUG", "8",8),
		SEPTEMBER("SEP", "9",9),
		OCTOBER("OCT", "10",10),
		NOVEMBER("NOV", "11",11),
		DECEMBER("DEC", "12",12);

		final String abbrev;
		final String numStr;
		Integer num;    // not used anywhere

		private MonthsEnum(String abbrev, String numStr, Integer num) {
			this.abbrev = abbrev;
			this.numStr = numStr;
			this.num = num;
		}


	}
}
