package com.example.wundergarten.model;

import java.util.ArrayList;


public class PlantProperties {
	
	public String winterhard = "";
	public String vochtigheid = "";
	public String ph = "";
	public String evergreen = "";
	public ArrayList<String> licht = new ArrayList<String>();
	
	public PlantProperties() {
	}
	
	public String getWinterhard() {
		return this.winterhard;
	}
	
	public String getVochtigheid() {
		return this.vochtigheid;
	}
	
	public String getPH() {
		return this.ph;
	}
	
	public String getEvergreen() {
		return this.evergreen;
	}
	
	public ArrayList<String> getLicht() {
		return this.licht;
	}
	
	public void setWinterhard(String value) {
		this.winterhard = value;
	}
	
	public void setVochtigheid(String value) {
		this.vochtigheid = value;
	}
	
	public void setPH(String value) {
		this.ph = value;
	}
	
	public void setEvergreen(String value) {
		this.evergreen = value;
	}
	
	public void addLicht(String value) {
		this.licht.add(value);
	}
	
}
