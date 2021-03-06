package cz.cron;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.Months;
import org.joda.time.Years;

import cz.cron.fileds.DayOfMonth;
import cz.cron.fileds.DayOfWeek;
import cz.cron.fileds.Hour;
import cz.cron.fileds.Minute;
import cz.cron.fileds.Month;

// 1. why such a random usage of whitespace? (Everywhere in this class - or, better, everywhere in the whole project)
// 2. Javadoc must begin with a sentence == text finished with a period. (That's what the official Javadoc tool
// requires)
/**
 * One cron line.
 */
public class Cron {
    private final static Logger LOG = Logger.getLogger(Cron.class);

    private Minute minute;
    private Hour hour;
    private DayOfWeek dayOfWeek;
    private Month month;
    private DayOfMonth dayOfMonth;

    public Cron(Minute minute, Hour hour, DayOfWeek dayOfWeek, Month month, DayOfMonth dayOfMonth) {
        this.minute = minute;
        this.hour = hour;
        this.dayOfWeek = dayOfWeek;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    // no '-' between '@param xyz' and the description, and similarly between '@throws XYZ' and the description. There
    // will be a dash added by the Javadoc tool between those two parts, so if you write it explicitly, you will get two
    // dashes next to each other
    /**
     * Determines if the cron job will take place during given interval or not.
     *
     * @param dateStart start of interval
     * @param dateEnd   end of interval
     *
     * @return true the cron job will take place during given interval, false otherwise
     *
     * @throws CronParserException
     */
    // the algorithm this method tries to follow I really couldn't guess. After a long analysis I put together two
    // simple tests showing it's wrong - CronCheckerTest.testShouldRun() & CronCheckerTest.testShouldNotRun()
    public boolean getWillRun(LocalDateTime dateStart, LocalDateTime dateEnd) throws CronParserException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getWillRun(" + dateStart + ", " + dateEnd + ")");
        }

        // years - will be used below
        int startYear = dateStart.getYear();
        int endYear = dateEnd.getYear();
        int yearsInterval = Years.yearsBetween(dateStart, dateEnd).getYears();
        // are months between allowed?
        int startMonth = dateStart.getMonthOfYear();
        int endMonth = dateEnd.getMonthOfYear();
        int monthsInterval = Months.monthsBetween(dateStart, dateEnd).getMonths();
        List<Integer> monthsBetween = getValuesBetween(month.getAllowedValues(), startMonth, endMonth,
                yearsInterval, month.getTo());
        if (!getIsBetween(month.getAllowedValues(), startMonth, endMonth, startYear, endYear,
                new ArrayList<Integer>(), false, -1, false)) {
            LOG.debug("months are not between allowed");
            return false;
        }

		// are days between allowed?
        // the following comment doesn't make a sense in English (quite common elsewhere, too)
        // by days must be dayOfWeek or dayOfMonth null
        int dayOfWeekStart = dateStart.getDayOfWeek();
        int dayOfWeekEnd = dateEnd.getDayOfWeek();

        int dayOfMonthStart = dateStart.getDayOfMonth();
        int dayOfMonthEnd = dateEnd.getDayOfMonth();

        int dayStart;
        int dayEnd;
        int dayMax;
        List<Integer> allowedDayValues;

        if (dayOfWeek != null) {
            dayStart = dayOfWeekStart;
            dayEnd = dayOfWeekEnd;
            allowedDayValues = dayOfWeek.getAllowedValues();
            dayMax = dayOfWeek.getTo();
        } else {
            dayStart = dayOfMonthStart;
            dayEnd = dayOfMonthEnd;
            allowedDayValues = dayOfMonth.getAllowedValues();
            dayMax = dayOfMonth.getTo();
        }

        int daysInterval = Days.daysBetween(dateStart, dateEnd).getDays();

        List<Integer> daysBetween = getValuesBetween(allowedDayValues, dayStart, dayEnd,
                monthsBetween.size(), dayMax);

        // well, this distribution of whitespace is really something!
        if (!getIsBetween(allowedDayValues, dayStart, dayEnd, startMonth, endMonth, monthsBetween,
                monthsInterval >= month.getTo(), month.getTo(), yearsInterval >= 1)) {
            LOG.debug("days are not between allowed");
            return false;
        }

        // is that a claim? Or a description what the following code tries to determine? Say the whole sentences so that
        // you will be understood
        // are hours in allowed interval?
        int hourStart = dateStart.getHourOfDay();
        int hourEnd = dateEnd.getHourOfDay();

        int hoursInterval = Hours.hoursBetween(dateStart, dateEnd).getHours();

        List<Integer> hoursBetween = getValuesBetween(hour.getAllowedValues(), hourStart, hourEnd,
                daysBetween.size(), hour.getTo());

        if (!getIsBetween(hour.getAllowedValues(), hourStart, hourEnd, dayStart, dayEnd, daysBetween,
                daysInterval >= dayMax, dayMax, monthsInterval >= 1)) {
            LOG.debug("hours are not between allowed");
            return false;
        }

        // are minutes allowed?
        int minuteStart = dateStart.getMinuteOfHour();
        int minuteEnd = dateEnd.getMinuteOfHour();

        if (!getIsBetween(minute.getAllowedValues(), minuteStart, minuteEnd, hourStart, hourEnd, hoursBetween,
                hoursInterval >= hour.getTo(), hour.getTo(), daysInterval >= 1)) {
            LOG.debug("minutes are not between allowed");
            return false;
        }

        return true;
    }

    /**
     * @param allowedValues collection of allowed values
     * @param valFrom       number from to start to search for allowed value
     * @param valTo         number when to end when searching for allowed values
     * @param prevSize      size of previous interval
     * @param max           max allowed value (of what? Be more descriptive - this comment doesn't help)
     *
     * @return allowed values which are between <code>from</code> and <code>to</code>
     */
    private List<Integer> getValuesBetween(List<Integer> allowedValues, int valFrom, int valTo,
            int prevSize, int max) {
        List<Integer> valuesBetween = new ArrayList<Integer>();

        if (valFrom == valTo) {
            return valuesBetween;
        }

        int to = valTo;

        if (prevSize > 0) {
            to = max;
        }

        for (int i = valFrom; i <= to; i++) {
            if (allowedValues.contains(i)) {
                valuesBetween.add(i);
            }
        }

		// to is smaller then from -> adding the allowed values from 0 to the beginning of next interval (e.g. days from next month)
        // eg. from 20.3. - 2.4. -> adds allowed values from 0 to 2
        if (valTo < valFrom || prevSize > 0) {
            for (int i = 0; i <= valTo; i++) {
                if (allowedValues.contains(i)) {
                    valuesBetween.add(i);
                }
            }
        }

        return valuesBetween;
    }

    /**
     * <p>
     * Method for determining if the allowed values are between the interval. Method needs to know about borders of
     * previous fields.</p>
     *
     * <p>
     * In next lines "previous field" means the part of date which is the next bigger unit (eg. by minutes is it
     * hour).</p>
     *
     * @param allowedValues  list of allowed values
     * @param from           start of interval
     * @param to             end of interval
     * @param prevStart      start of interval of previous field
     * @param prevEnd        end of interval of previous field
     * @param valuesBetween  values of previous field which were between interval
     * @param biggerMax      boolean, true if the size of interval previous field is greater than previous field's max
     *                       value
     * @param max            maximal allowed value of previous field
     * @param precPrecBigger true, if the preceding preceding interval was greater then 0
     *
     * @return true, if allowed values are within interval between <code>from</code> and <code>to</code>
     */
    // this method is completely crazy. No idea what the individual parameters influence (the Javadoc above definitely
    // doesn't help), and any attempt to analyze the code below failed as well
    private boolean getIsBetween(List<Integer> allowedValues, int from, int to, int prevStart,
            int prevEnd, List<Integer> valuesBetween, boolean biggerMax, int max, boolean precPrecBigger) {

        // values between ares greater then 2 -> it must be within the interval
        if (valuesBetween.size() >= 2) {
            return true;
        }

        // size of previous interval is bigger than max value -> it must be within the interval
        if (biggerMax) {
            return true;
        }

        int f = from <= to ? from : to;
        int t = to >= from ? to : from;

		// only one value is between and the value equals start of the interval ->
        // test if there is some allowed value greater than the start of the interval
        if (valuesBetween.size() == 1 && valuesBetween.get(0).equals(prevStart)) {
            for (Integer v : allowedValues) {
                if (v >= from) {
                    return true;
                }
            }
            return false;
		// only one value is between and the value equals end of the interval ->
        // test if there is some allowed value smaller than the end of the interval
        } else if (valuesBetween.size() == 1 && valuesBetween.get(0).equals(prevEnd)) {
            for (Integer v : allowedValues) {
                if (v <= to) {
                    return true;
                }
            }
            return false;
		// other cases when there is only one value between:
        // previous end is after previous start -> value between must be between
        // previous end is before previous start -> value between must greater then 0 and smaller than max
        // preceding preceding interval was bigger than 0
        } else if (valuesBetween.size() == 1
                && ((prevEnd >= prevStart && valuesBetween.get(0) < prevEnd && valuesBetween.get(0) > prevStart)
                || (prevEnd < prevStart && valuesBetween.get(0) > 0 && valuesBetween.get(0) < max)
                || precPrecBigger)) {
            return true;

		// test if is there any allowed value that is greater than the start of the interval and smaller
        // than the end of the interval
        } else {
            for (Integer v : allowedValues) {
                if (v >= f && v <= t) {
                    return true;
                }
            }
        }

        return false;
    }
}
