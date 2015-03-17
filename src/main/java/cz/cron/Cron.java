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
		

		//Period period = Period.between(dateStart, dateEnd);
		int startYear = dateStart.getYear();
		int endYear = dateEnd.getYear();
		Long yearsInterval = ChronoUnit.YEARS.between(dateStart, dateEnd);
		
		
		int startMonth = dateStart.getMonthValue();
		int endMonth = dateEnd.getMonthValue();

		List<Integer> monthsBetween = getValuesBetween(month.getAllowedValues(), startMonth, endMonth, yearsInterval.intValue(), month.getTo());
		if(!getIsBetween(month.getAllowedValues(), startMonth, endMonth, startYear, endYear, yearsInterval)){
			LOG.debug("months are not betweed allowed");
			return false;
		}
		
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
		
		if(!getIsBetween(allowedDayValues, dayStart, dayEnd, startMonth, endMonth, monthsBetween)){
			LOG.debug("days are not betweed allowed");
			return false;
		}
		
		long daysInterval = ChronoUnit.DAYS.between(dateStart, dateEnd);
		
		int hourStart = dateStart.getHour();
		int hourEnd = dateEnd.getHour();
		long hoursInterval = ChronoUnit.HOURS.between(dateStart, dateEnd);
		List<Integer> hoursBetween = getValuesBetween(hour.getAllowedValues(), hourStart, hourEnd, daysBetween.size(), hour.getTo());
		if(!getIsBetween(hour.getAllowedValues(), hourStart, hourEnd, dayStart, dayEnd, daysBetween, daysInterval > dayMax, dayMax)){
			LOG.debug("hours are not betweed allowed");
			return false;
		}
		
		int minuteStart = dateStart.getMinute();
		int minuteEnd = dateEnd.getMinute();

		
		if(!getIsBetween(minute.getAllowedValues(), minuteStart, minuteEnd, hourStart, hourEnd, hoursBetween, hoursInterval > hour.getTo(),hour.getTo())){
			LOG.debug("minutes are not betweed allowed");
			return false;
		}
		
		return true;
	}
	
	private List<Integer> getValuesBetween(List<Integer> allowedValues, int from, int to, int prevSize, int prevMax){
		List<Integer> valuesBetween = new ArrayList<Integer>();
		
		if(from == to){
			return valuesBetween;
		}
		
//		int f = from <= to ? from : to;
//		int t = to >= from ? to : from; 
		int toI = to;
		if(prevSize > 0){
			toI = prevMax;
		}
		for(int i = from; i <= toI;i++){
			if (allowedValues.contains(i)){
				valuesBetween.add(i);
			}
		}
		
		if(to < from){

			for (int i = 0; i <= to; i++) {
				if (allowedValues.contains(i)) {
					valuesBetween.add(i);
				}
			}
		}
		return valuesBetween;
	}
	
	private boolean getIsBetween(List<Integer> allowedValues, int from, int to, int prevFrom, 
			int prevTo, long intervalSize){
		
		if(intervalSize >= 2){
			return true;
		}
		int f = from <= to ? from : to;
		int t = to >= from ? to : from; 
		for(Integer v : allowedValues){
			
			//at the beginning of interval 
			if( v >= f && (v <= t || intervalSize > 0)){
				return true;
			}
			
			//or at the end of interval
			if( v <= t && (v >= f || intervalSize > 0)){
				return true;
			}
			
		}
		
		return false;
		
	}

	private boolean getIsBetween(List<Integer> allowedValues, int from, int to, int prevFrom, 
			int prevTo, List<Integer> valuesBetween){
		return getIsBetween(allowedValues, from, to, prevFrom, prevTo, valuesBetween, false, -1);
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
		}else
		
		if(valuesBetween.size() == 1 && valuesBetween.get(0).equals(prevTo)){
			for(Integer v : allowedValues){
				if(v <= to){
					return true;
				}
			}
			return false;
		}else	if(valuesBetween.size() == 1 && ((prevTo >= prevFrom && valuesBetween.get(0) < prevTo && valuesBetween.get(0) > prevFrom) || (prevTo < prevFrom && valuesBetween.get(0) > 0 && valuesBetween.get(0) < max)) ){
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
	
	private int getFirst(int start, int end){
		return start < end 	? start : end	;
	}
	
	private boolean getIsBetween(List<Integer> allowedValues, int from, int to, int previousInterval){
		if(previousInterval > 0){
			return true;
		}
		
		for(Integer i : allowedValues){
			if( (i >= from && i <= to) ||  (i <= from && i >= to)){
				return true;
			}
			
		}
		
		return false;
	}
	
	private boolean getIsBetween(List<Integer> allowedValues, int from, int to, 
			int previousInterval, boolean firstOfPrev){

		if(Math.abs(previousInterval) > 0 && !firstOfPrev){
			return true;
		}
		
		if(firstOfPrev && previousInterval >= 1){
			
			return true;
			
		}
		
		for(Integer i : allowedValues){
			if( (i >= from && i <= to) ||  (i <= from && i >= to)){
				return true;
			}
			
		}
		
		return false;
	}

	
}
