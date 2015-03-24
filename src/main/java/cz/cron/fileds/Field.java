package cz.cron.fileds; // typo - should be 'fields'

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import cz.cron.CronParserException;

// why such a random usage of whitespace? (Everywhere in this class - or, better, everywhere in the whole project)
/**
 * Abstract class for one field from cron input string.
 */
public abstract class Field {
    private final static Logger LOG = Logger.getLogger(Field.class);

    private final int from;
    private final int to;
    protected List<Integer> allowedValues;
    private final String cronString;

    /**
     * @param from       min allowed value by this field
     * @param to         max allowed value by this field
     * @param cronString cron string for this field
     *
     * @throws CronParserException error when parsing <code>cronString</code>
     */
    public Field(int from, int to, String cronString) throws CronParserException {
        this.from = from;
        this.to = to;
        this.cronString = cronString == null ? "" : cronString.toUpperCase();

        init(); // why this method? Called just from here
    }

    public List<Integer> getAllowedValues() {
        return allowedValues;
    }

    private void init() throws CronParserException {
        if (allowedValues == null) {  // why? Given where this method is called from it's always true
            allowedValues = getAllowedValuesFromString(cronString);
        }
    }

    // why not 'isNumber'?
    protected boolean getIsNumber(String cronString) {
        return cronString.matches("\\d+");
    }

    protected boolean getIsNumberInterval(String cronString) {
        return cronString.matches("\\d+-\\d+");
    }

    // wrong name: it's an enumeration of cron substrings (not necessarily numbers)
    protected boolean getIsNumberEnumeration(String cronString) {
        return cronString.contains(",") && !cronString.matches("\\w");  // the second part doesn't make sense
    }

    protected boolean getIsEvery(String cronString) {
        return cronString.equals("*");
    }

    // why suddenly no 'number' in the name?
    protected boolean getIsIncrement(String cronString) {
        return cronString.matches("\\d+/\\d+");
    }

    // why suddenly no 'number' in the name?
    protected boolean getIsIntervalIncrement(String cronString) {
        return cronString.matches("\\d+-\\d+/\\d+");
    }

    /**
     * Constructs a list with allowed values from a given <code>cronString</code>.
     *
     * @return list with allowed values
     */
    protected List<Integer> getAllowedValuesFromString(String cronString) throws CronParserException {
        // if this is to assure that '*/<step>' interprets as stepping through all the allowed values, then it works
        // only for fields with 'from' == 0. See FieldTest.testFieldStepOnStar()
        if (cronString.startsWith("*/")) {
            cronString = cronString.replace("*/", "0/");
        }

        // well, this distribution of whitespace is really something!
        if (getIsEvery(cronString)) {
            return createIntegerInterval(from, to, 1);
        } else if (getIsNumberEnumeration(cronString)) {
            return getAllowedValuesFromEnumeration(cronString);
        } else if (getIsNumberInterval(cronString)) {
            return getAllowedValuesFromStringInterval(cronString);
        } else if (getIsNumber(cronString)) {
            return getAllowedSingleNumber(cronString);
        } else if (getIsIncrement(cronString)) {
            return getAllowedValuesFromIncrement(cronString);
        } else if (getIsIntervalIncrement(cronString)) {
            return getAllowedValuesFromIntervalIncrement(cronString);
        }

        // CronParserException should be reported instead. If you return null, the Field instance is constructed and
        // just contains null 'allowedValues'; a subsequent call to getWillRun will throw a NullPointerException. See
        // CronParserTest.testInvalidCronString() & CronParserTest.testInvalidCronString2()
        return null;
    }

    private List<Integer> getAllowedSingleNumber(String cronString) throws CronParserException {
        try {
            Integer intVal = Integer.parseInt(cronString);  // use int instead of Integer here (efficiency)

            // no from >= this.from checking? See FieldTest.testMonthInvalid2() & FieldTest.testDayOfMonthInvalid()
            if (intVal > this.to) {
                throw new CronParserException("the value " + intVal + " is greater than max allowed value for this field");
            }

            return Arrays.asList(intVal);
        } catch (NumberFormatException ex) {
            // it shoudn't come (because of meaning getIsNumber())
            // and therefore why to log it? If this branch executes, it's a semantical flaw of your code, which should
            // be reported by throwing IllegalStateException() (and later fixed). It's not an error state depending on
            // the outer world conditions which may or may not occur
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    private List<Integer> getAllowedValuesFromStringInterval(String cronString) throws CronParserException {
        return getAllowedValuesFromStringInterval(cronString, 1);
    }

    private List<Integer> getAllowedValuesFromStringInterval(String cronString, int step) throws CronParserException {
        Integer from;   // variable hides a field (not an error per se, but highly discouraged as it can easily cause
                        // unwanted behavior)
        Integer to;     // variable hides a field
        String[] bounds = cronString.split("-");

        // this check is needless - due to the regular expression leading to evaluation of this method
        if (bounds.length != 2) {
            throw new CronParserException("bad count of parameters during parsing string " + cronString);
        }

        try {
            from = Integer.parseInt(bounds[0]);
            to = Integer.parseInt(bounds[1]);
        } catch (NumberFormatException ex) {
            throw new CronParserException("");
        }

        return createIntegerInterval(from, to, step);
    }

    private List<Integer> getAllowedValuesFromEnumeration(String cronString) throws CronParserException {
        // silently removes trailing commas (which should be reported as an error instead). See
        // FieldTest.testFieldInvalid()
        String[] vals = cronString.split(",");
        List<Integer> intVals = new ArrayList<Integer>();
        for (String s : vals) {
            try {
                // 1. duplication of the allowed values
                // 2. throws NullPointerException on two consecutive commas
                //     (s == "" -> getAllowedValueFromString(s) == null)
                intVals.addAll(getAllowedValuesFromString(s));
            } catch (NumberFormatException ex) {
                throw new CronParserException("error when parsing string " + cronString);
            }
        }
        return intVals;
    }

    // from where came the logic of this method? According to the man page, cron string field definition 'x/y' is not
    // allowed, but even if you want to be liberal in what you accept, I would expect it to behave the same as 'x-x/y'.
    // See FieldTest.testFieldStepOnSingleValue()
    private List<Integer> getAllowedValuesFromIncrement(String cronString) throws CronParserException {
        Integer from;   // variable hides a field
        Integer step;
        String[] vals = cronString.split("/");

        // this check is needless - due to the regular expression leading to evaluation of this method
        if (vals.length != 2) {
            // this exception message, for instance, should make a sense?
            throw new CronParserException("bad count of parameters during parsing string " + cronString);
        }

        try {
            from = Integer.parseInt(vals[0]);
            step = Integer.parseInt(vals[1]);
        } catch (NumberFormatException ex) {
            throw new CronParserException("");
        }

        return createIntegerInterval(from, to, step);
    }

    private List<Integer> getAllowedValuesFromIntervalIncrement(String cronString) throws CronParserException {
        String interval;
        Integer step;
        String[] vals = cronString.split("/");

        // this check is needless - due to the regular expression leading to evaluation of this method
        if (vals.length != 2) {
            throw new CronParserException("bad count of parameters during parsing string " + cronString);
        }

        try {
            interval = vals[0];
            step = Integer.parseInt(vals[1]);
        } catch (NumberFormatException ex) {
            throw new CronParserException("error when parsing string " + cronString);
        }

        return getAllowedValuesFromStringInterval(interval, step);
    }

    protected List<Integer> createIntegerInterval(int from, int to, int step) throws CronParserException {
        // no from >= this.from checking?

        if (to > this.to) {
            throw new CronParserException("the upper boundary " + to + " is greater than max allowed value for this field");
        }

        List<Integer> vals = new ArrayList<>();
        for (int i = from; i <= to; i = i + step) {
            vals.add(i);
        }

        return vals;
    }

    public int getTo() {
        return to;
    }
}
