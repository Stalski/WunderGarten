package com.example.wundergarten.model;

import android.graphics.Bitmap;

public class Plant {

	private String id;
	private String title;
	private String description;
	private String name;
	private String uri;
	private String imageRemotePath;
	private Integer minHeight;
	private Integer maxHeight;
	private Integer numberOfFollowers;
	private Boolean iFollow;
	private PlantProperties plantProperties;
	private Bitmap imageBitmap;

	public Plant(String id, String title) {
		this.id = id;
		this.title = title;
		plantProperties = new PlantProperties();
	}

	public Plant() {
		plantProperties = new PlantProperties();
	}

	@Override
	public String toString() {
		return title;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUri() {
		return this.uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getImageRemotePath() {
		return this.imageRemotePath;
	}
	
	public void setImageRemotePath(String imageRemotePath) {
		this.imageRemotePath = imageRemotePath;
	}
	
	public Bitmap getImageBitmap() {
		return this.imageBitmap;
	}
	
	public void setImageBitmap(Bitmap imageBitmap) {
		this.imageBitmap = imageBitmap;
	}
	
	public Integer getNumberOfFollowers() {
		return this.numberOfFollowers;
	}
	
	public void setNumberOfFollowers(Integer number) {
		this.numberOfFollowers = number;
	}
	
	public Boolean getIFollow() {
		return this.iFollow;
	}
	
	public void setIFollow(Boolean iFollow) {
		this.iFollow = iFollow;
	}
	
	public Integer getMaxHeight() {
		return this.maxHeight;
	}
	
	public void setMaxHeight(Integer maxHeight) {
		this.maxHeight = maxHeight;
	}
	
	public Integer getMinHeight() {
		return this.minHeight;
	}
	
	public void setMinHeight(Integer minHeight) {
		this.minHeight = minHeight;
	}
	
	public PlantProperties getProperties() {
		return this.plantProperties;
	}
	
	public void setProperties(PlantProperties plantProperties) {
		this.plantProperties = plantProperties;
	}
	
}
