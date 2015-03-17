package cz.cron;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cz.cron.fileds.DayOfMonth;
import cz.cron.fileds.DayOfWeek;
import cz.cron.fileds.Hour;
import cz.cron.fileds.Minute;
import cz.cron.fileds.Month;

/**
 * 
 * One cron line
 * @author rdobra
 *
 */
public class Cron {

	private final static Logger LOG = Logger.getLogger(Cron.class.getName());
	
	private Minute minute;
	private Hour hour;
	private DayOfWeek dayOfWeek;
	private Month month;
	private DayOfMonth dayOfMonth;
	
	
	public Cron(Minute minute, Hour hour, DayOfWeek dayOfWeek, Month month,
			DayOfMonth dayOfMonth) {
		this.minute = minute;
		this.hour = hour;
		this.dayOfWeek = dayOfWeek;
		this.month = month;
		this.dayOfMonth = dayOfMonth;
	}


	public Minute getMinute() {
		return minute;
	}


	public Hour getHour() {
		return hour;
	}


	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}


	public Month getMonth() {
		return month;
	}


	public DayOfMonth getDayOfMonth() {
		return dayOfMonth;
	}
	
	/**
	 * determines if the cron job will take place during given interval or not
	 * @param dateStart - start of interval
	 * @param dateEnd - end of interval
	 * @return true - the cron job will take place during given interval, false otherwise
	 * @throws CronParserException
	 */
	public boolean getWillRun(LocalDateTime dateStart, LocalDateTime dateEnd) throws CronParserException{
		
		if(LOG.isDebugEnabled()){
			LOG.debug("getWillRun(" +dateStart + ", " + dateEnd + ")");
		}

		//years - will be used below
		int startYear = dateStart.getYear();
		int endYear = dateEnd.getYear();
		Long yearsInterval = ChronoUnit.YEARS.between(dateStart, dateEnd);
		
		//are months between allowed?
		int startMonth = dateStart.getMonthValue();
		int endMonth = dateEnd.getMonthValue();
		long monthsInterval = ChronoUnit.HOURS.between(dateStart, dateEnd);
		List<Integer> monthsBetween = getValuesBetween(month.getAllowedValues(), startMonth, endMonth, 
				yearsInterval.intValue(), month.getTo());
		if(!getIsBetween(month.getAllowedValues(), startMonth, endMonth, startYear, endYear, 
				new ArrayList<Integer>(),false, -1)){
			LOG.debug("months are not betweed allowed");
			return false;
		}
		
		//are days between allowed 
		//by days must be dayOfWeek or dayOfMonth null
		int dayOfWeekStart = dateStart.getDayOfWeek().getValue();
		int dayOfWeekEnd = dateEnd.getDayOfWeek().getValue();
		
		int dayOfMonthStart = dateStart.getDayOfMonth();
		int dayOfMonthEnd = dateEnd.getDayOfMonth();
		
		
		int dayStart;
		int dayEnd;
		int dayMax;
		List<Integer> allowedDayValues;
		
		if(dayOfWeek != null){
			dayStart = dayOfWeekStart;
			dayEnd = dayOfWeekEnd;
			allowedDayValues = dayOfWeek.getAllowedValues();
			dayMax = dayOfWeek.getTo();
		}else{
			
			dayStart = dayOfMonthStart;
			dayEnd = dayOfMonthEnd;
			allowedDayValues = dayOfMonth.getAllowedValues();
			dayMax = dayOfMonth.getTo();
		}
		
		List<Integer> daysBetween = getValuesBetween(allowedDayValues, dayStart, dayEnd, monthsBetween.size(), dayMax);
		
		if(!getIsBetween(allowedDayValues, dayStart, dayEnd, startMonth, endMonth, monthsBetween,
				monthsInterval > month.getTo(), month.getTo())){
			LOG.debug("days are not betweed allowed");
			return false;
		}
		
		long daysInterval = ChronoUnit.DAYS.between(dateStart, dateEnd);
		
		//are hours in allowed interval
		int hourStart = dateStart.getHour();
		int hourEnd = dateEnd.getHour();
		
		long hoursInterval = ChronoUnit.HOURS.between(dateStart, dateEnd);
		
		List<Integer> hoursBetween = getValuesBetween(hour.getAllowedValues(), hourStart, hourEnd, daysBetween.size(), hour.getTo());
		
		if(!getIsBetween(hour.getAllowedValues(), hourStart, hourEnd, dayStart, dayEnd, daysBetween, 
				daysInterval > dayMax, dayMax)){
			LOG.debug("hours are not betweed allowed");
			return false;
		}
		
		
		//are minutes allowed?
		int minuteStart = dateStart.getMinute();
		int minuteEnd = dateEnd.getMinute();

		
		if(!getIsBetween(minute.getAllowedValues(), minuteStart, minuteEnd, hourStart, hourEnd, hoursBetween, hoursInterval > hour.getTo(),hour.getTo())){
			LOG.debug("minutes are not betweed allowed");
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param allowedValues collection of allowed values
	 * @param valFrom number from to start to search for allowed value
	 * @param valTo number when to end when searching for allowed values
	 * @param prevSize size of previous interval 
	 * @param max - max allowed value 
	 * @return allowed values which are between <code>from</code> and <code>to</code>
	 */
	private List<Integer> getValuesBetween(List<Integer> allowedValues, int valFrom, int valTo, int prevSize, int max){
		List<Integer> valuesBetween = new ArrayList<Integer>();
		
		if(valFrom == valTo){
			return valuesBetween;
		}
		
		int to = valTo;
		if(prevSize > 0){
			to = max;
		}
		for(int i = valFrom; i <= to;i++){
			if (allowedValues.contains(i)){
				valuesBetween.add(i);
			}
		}
		
		
		//to is smaller then from -> adding the allowed values from 0 to the beginning of next interval (e.g. days from next month)
		//eg. from 20.3. - 2.4. -> adds allowed values from 0 to 2
		if(valTo < valFrom){
			for (int i = 0; i <= valTo; i++) {
				if (allowedValues.contains(i)) {
					valuesBetween.add(i);
				}
			}
		}
		
		return valuesBetween;
	}

	private boolean getIsBetween(List<Integer> allowedValues, int from, int to, int prevFrom, 
			int prevTo, List<Integer> valuesBetween, boolean biggerMax, int max){
		
		if(valuesBetween.size() >= 2){
			return true;
		}
		
		if(biggerMax){
			return true;
		}
		
		int f = from <= to ? from : to;
		int t = to >= from ? to : from;
		
		int fP = prevFrom <= prevTo ? prevFrom : prevTo;
		int tP = prevTo >= prevFrom ? prevTo : prevFrom;

		if(valuesBetween.size() == 1 && valuesBetween.get(0).equals(prevFrom)){
			for(Integer v : allowedValues){
				if(v >= from){
					return true;
				}
			}
			return false;
		}else if(valuesBetween.size() == 1 && valuesBetween.get(0).equals(prevTo)){
			for(Integer v : allowedValues){
				if(v <= to){
					return true;
				}
			}
			return false;
		}else if(valuesBetween.size() == 1 && ((prevTo >= prevFrom && valuesBetween.get(0) < prevTo && valuesBetween.get(0) > prevFrom) || (prevTo < prevFrom && valuesBetween.get(0) > 0 && valuesBetween.get(0) < max)) ){
			return true;
		}else{
			for(Integer v : allowedValues){
				if( v >= f && v <= t){
					return true;
				}
			}
		}

		return false;
	}

}
