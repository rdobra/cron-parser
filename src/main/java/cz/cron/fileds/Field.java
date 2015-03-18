package cz.cron.fileds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import cz.cron.CronParserException;

/**
 * Abstract class for one field from cron input string
 * 
 * **/
public abstract class Field {
	
	private final static Logger LOG = Logger.getLogger(Field.class.getName());
	
	private final int from;
	private final int to;
	protected List<Integer> allowedValues;
	private final String cronString;
	

	/**
	 * 
	 * @param from - min allowed value by this field
	 * @param to - max allowed value by this field
	 * @param cronString - cron string for this field
	 * @throws CronParserException - error when parsing <code>cronString</code>
	 * 
	 */
	public Field(int from, int to, String cronString) throws CronParserException {
		this.from = from;
		this.to = to;
		this.cronString = cronString == null ? "" : cronString.toUpperCase();
		
		init();
	}

	/**
	 * 
	 * @return allowed values
	 * 
	 */
	public List<Integer> getAllowedValues() {
		return allowedValues;
	}
	
	private void init() throws CronParserException{
		if(allowedValues == null){
			allowedValues = getAllowedValuesFromString(cronString);
		}
	}

	protected boolean getIsNumber(String cronString){
		return cronString.matches("\\d+");
	}
	
	protected boolean getIsNumberInterval(String cronString){
		return cronString.matches("\\d+-\\d+");
	}
	
	protected boolean getIsNumberEnumeration(String cronString){
		return cronString.contains(",") && !cronString.matches("\\w");
	}
	
	protected boolean getIsEvery(String cronString){
		return cronString.equals("*");
	}
	
	protected boolean getIsIncrement(String cronString){
		return cronString.matches("\\d+/\\d+");
	}
	
	protected boolean getIsIntervalIncrement(String cronString){
		return cronString.matches("\\d+-\\d+/\\d+");
	}
	


	/**
	 * 
	 * constructs from given <code>cronString</code> list with allowed values
	 * 
	 * @param cronString
	 * 
	 * @return list with allowed values
	 * 
	 * @throws CronParserException - error during parsing ocurred
	 */
	protected List<Integer> getAllowedValuesFromString(String cronString) throws CronParserException{

		if(cronString.startsWith("*/")){
			cronString = cronString.replace("*/", "0/");
		}

		if(getIsEvery(cronString)){
			return createIntegerInterval(from, to, 1);
		}else if(getIsNumberEnumeration(cronString)){
			return getAllowedValuesFromEnumeration(cronString);
		}else if(getIsNumberInterval(cronString)){
			return getAllowedValuesFromStringInterval(cronString);
		}else if(getIsNumber(cronString)){
			return getAllowedSingleNumber(cronString);
		}else if(getIsIncrement(cronString)) {
			return getAllowedValuesFromIncrement(cronString);
		}
		else if(getIsIntervalIncrement(cronString)) {
			return getAllowedValuesFromIntervalIncrement(cronString);
		}
			
		return null;
	}
	
	private List<Integer> getAllowedSingleNumber(String cronString) throws CronParserException{
		try{
			Integer intVal = Integer.parseInt(cronString);
			
			if(intVal > this.to){
				throw new CronParserException("the value " + intVal + " is grater than max allowed value for this field");
			}
			
			return Arrays.asList(intVal);
		}catch(NumberFormatException ex) {
			//it shoudn't come (because of meaning getIsNumber())
			LOG.error(ex.getMessage(), ex);
		}
		return null;
	}
	
	private List<Integer> getAllowedValuesFromStringInterval(String cronString) throws CronParserException{
		return getAllowedValuesFromStringInterval(cronString, 1);
	}
	
	private List<Integer> getAllowedValuesFromStringInterval(String cronString, int step) throws CronParserException{
		Integer from;
		Integer to;
		String[] bounds = cronString.split("-");
		
		if(bounds.length != 2){
			throw new CronParserException("bad count of parameters during parsing string " + cronString);
		}
		
		try{
			from = Integer.parseInt(bounds[0]);
			to = Integer.parseInt(bounds[1]);
		}catch(NumberFormatException ex){
			throw new CronParserException("");
		}
	
		return createIntegerInterval(from, to, step);
	}
	
	private List<Integer> getAllowedValuesFromEnumeration(String cronString) throws CronParserException{
		String[] vals = cronString.split(",");
		List<Integer> intVals = new ArrayList<Integer>();
		for(String s : vals){
			try{
				intVals.addAll(getAllowedValuesFromString(s));
			}catch(NumberFormatException ex){
				throw new CronParserException("error when parsing string " + cronString);
			}
		}
		return intVals;
	}
	
	private List<Integer> getAllowedValuesFromIncrement(String cronString) throws CronParserException{
		Integer from;
		Integer step;
		String[] vals = cronString.split("/");
		
		if(vals.length != 2){

			throw new CronParserException("bad count of parameters during parsing string " + cronString);
		}
		
		try{
			from = Integer.parseInt(vals[0]);
			step = Integer.parseInt(vals[1]);
		}catch(NumberFormatException ex){
			throw new CronParserException("");
		}
	
		return createIntegerInterval(from, to, step);
	}
	
	private List<Integer> getAllowedValuesFromIntervalIncrement(String cronString) throws CronParserException{
		String interval;
		Integer step;
		String[] vals = cronString.split("/");
		
		if(vals.length != 2){
			throw new CronParserException("bad count of parameters during parsing string " + cronString);
		}
		
		try{
			interval = vals[0];
			step = Integer.parseInt(vals[1]);
		}catch(NumberFormatException ex){
			throw new CronParserException("error when parsing string " + cronString);
		}
	
		return getAllowedValuesFromStringInterval(interval,step);
	}
	
	protected List<Integer> createIntegerInterval(int from, int to, int step) throws CronParserException{
		
		if(to > this.to){
			throw new CronParserException("the upper boundary " + to + " is grater than max allowed value for this field");
		}
		
		List<Integer> vals = new ArrayList<Integer>();
		for(int i  = from; i <= to; i = i+step){
			vals.add(i);
		}
		
		return vals;
	}

	public int getTo() {
		return to;
	}
}
