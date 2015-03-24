package cz.cron.fileds;

import java.util.List;

import cz.cron.CronParserException;

// the original description is completely incomprehensible, definitely less than the name of the class. Which means the
// whole Javadoc is pointless
/**
 * Field for day of week.
 */
public class DayOfWeek extends Field {
    private final static Integer FROM = 1;
    private final static Integer TO = 7;

    public DayOfWeek(String cronString) throws CronParserException {
        super(FROM, TO, cronString);
    }

    @Override
    protected List<Integer> getAllowedValuesFromString(String cronString) throws CronParserException {
        //Sunday could be both 0 and 7 -> we will use only one value - 7
        // this breaks Sunday handling in ranges: 0-2 -> 7-2 -> [] instead of [0 (or 7), 1, 2]. See
        // FieldTest.testDayOfWeekIntervalWithSunday()
        String normalizedCronString = convertNamesToIntegers(cronString).replaceAll("0", "7");
        List<Integer> vals = super.getAllowedValuesFromString(normalizedCronString);
        return vals;

    }

    /**
     * Converts days names to their integer representation.
     *
     * @return string where instead of names of days are numbers
     */
    protected String convertNamesToIntegers(String cronString) {
        String str = cronString;

        for (DaysEnum m : DaysEnum.values()) {
            str = str.replaceAll(m.abbrev, m.numStr);
        }

        return str;
    }

    enum DaysEnum {
        MONDAY("MON", "1"),
        TUESDAY("TUE", "2"),
        WEDNESDAY("WED", "3"),
        THURSDAY("THU", "4"),
        FRIDAY("FRI", "5"),
        SATURDAY("SAT", "6"),
        SUNDAY("SUN", "7");

        final String abbrev;    // that makes a big difference - without 'final' the field can be changed, which would
                                // change the behavior of the enum constant
        final String numStr;
        Integer num;    // not used anywhere

        private DaysEnum(String abbrev, String numStr) {
            this.abbrev = abbrev;
            this.numStr = numStr;
        }
    }
}
