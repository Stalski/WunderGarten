package com.example.wundergarten.services;

public class MijnTuinServiceEntry {

	private String endPoint = "";
	
	public MijnTuinServiceEntry(String endPoint) {
		setEndPoint(endPoint);
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}
	
	public String getEndPoint() {
		return this.endPoint;
	}
}
