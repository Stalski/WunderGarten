package com.example.wundergarten.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Todo {

	public PlantAction plantAction;
	
	public Plant plant;
	
	public String type;
	
	private static final DateFormat DATE_FORMAT =
		    new SimpleDateFormat("dd/MMM", Locale.ENGLISH);
	
	/**
	 * Constructor
	 * 
	 * @param String type
	 * @param plant
	 * @param plantAction
	 */
	public Todo(String type, Plant plant, PlantAction plantAction) {
		this.type = type;
		this.plant = plant;
		this.plantAction = plantAction;
	}
	
	/**
	 * Get a title for the todo.
	 * @return
	 */
	public String getTitle() {
		return this.plant.getTitle() + " (" + this.plant.getName() + ")";
	}
	
	/**
	 * Get a plant action name for the todo.
	 * @return
	 */
	public String getAction() {
		return this.plantAction.getName();
	}
	
	/**
	 * Get harvest dates for the todo.
	 * @return
	 */
	public String getHarvestDates() {
		return this.getStartDate() + " - " + this.getEndDate();
	}
	
	/**
	 * Get a (sowing,planting) period for the todo.
	 * @return
	 */
	public String getPeriod() {
		return this.plantAction.period;
	}
	
	/**
	 * Get a frequency for the todo.
	 * @return
	 */
	public String getFrequency() {
		return this.plantAction.repeat;
	}
	
	/**
	 * Calculate the start day.
	 * @return
	 */
	public String getStartDate() {
		GregorianCalendar d = new GregorianCalendar();
		d.add(Calendar.DAY_OF_YEAR, this.plantAction.startDay);
		return DATE_FORMAT.format(d.getTime());
	}
	
	/**
	 * Calculate the end day.
	 * @return
	 */
	public String getEndDate() {
		GregorianCalendar d = new GregorianCalendar();
		d.add(Calendar.DAY_OF_YEAR, this.plantAction.endDay);
		return DATE_FORMAT.format(d.getTime());
	}
}
