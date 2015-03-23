package cz.cron;

import java.util.Calendar;
import org.testng.annotations.Test;

public class CronParserTest {
    @Test(expectedExceptions = CronParserException.class)
    public void testInvalidCronString() throws CronParserException {
        new CronParser().parse("x x * x x");
    }

    @Test(expectedExceptions = CronParserException.class)
    public void testInvalidCronString2() throws CronParserException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 0, 1, 0, 0);
        String dateInMillisAsString = Long.toString(calendar.getTimeInMillis());
        CronChecker.getWillRun("x x * x x", dateInMillisAsString, dateInMillisAsString);
    }
}
