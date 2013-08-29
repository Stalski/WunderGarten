package com.example.wundergarten.services;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.example.wundergarten.model.Plant;
import com.example.wundergarten.model.PlantAction;
import com.example.wundergarten.model.PlantProperties;
import com.example.wundergarten.model.Todo;

public class MijnTuinJSONParser {

	/**
	 * {"plants":[
	 * 	{
	 * 		"id":"343",
	 * 		"name":"Aardappel (algemeen)",
	 * 		"latinName":"Solanum tuberosum",
	 * 		"description":"\r\n",
	 * 		"minHeight":"0",
	 * 		"maxHeight":"50",
	 * 		"photo":"http:\/\/farm5.static.flickr.com\/4046\/4366716950_aab78d8303_s.jpg",
	 * @return
	 */
	public static ArrayList<Plant> getPlants(String jsonString) {

		ArrayList<Plant> items = new ArrayList<Plant>();
		try {
			
			JSONObject json = new JSONObject(jsonString);
			JSONArray jArray = json.getJSONArray("plants");

			for (int i = 0; i < jArray.length(); i++){
				
				JSONObject json_data = jArray.getJSONObject(i);

				Plant plant = new Plant(
						json_data.getString("id"),
						json_data.getString("name"));
				plant.setDescription(json_data.getString("description"));
				plant.setName(json_data.getString("latinName"));
				plant.setImageRemotePath(json_data.getString("photo"));
				
				items.add(plant);
					
			}
	      	
		} catch (JSONException e1){
			Log.e("MyGarden", "JSONException");
			Log.i("MijnTuinJSONParser", "jsonString: " + jsonString);
			e1.printStackTrace();
		} catch (ParseException e1){
			Log.i("MijnTuinJSONParser", "jsonString: " + jsonString);
			e1.printStackTrace();
		}
    	
        return items;
	}
	
	public static Plant parsePlant(JSONObject json) {
		
		Plant plant = new Plant();

		try {
			
			plant.setId(json.getString("id"));
			
			plant.setTitle(json.getString("name"));
			
			plant.setName(json.getString("latinName"));

			if (json.has("description")) {
				plant.setDescription(json.getString("description"));
			}
			
			if (json.has("photo")) {
				plant.setImageRemotePath(json.getString("photo"));
			}
	
			if (json.has("iFollow")) {
				plant.setIFollow(json.getBoolean("iFollow"));
			}
			
			if (json.has("numberOfFollowers")) {
				plant.setMaxHeight(json.getInt("numberOfFollowers"));
			}
			
			if (json.has("maxHeight")) {
				plant.setMaxHeight(json.getInt("maxHeight"));
			}
			
			if (json.has("minHeight")) {
				plant.setMinHeight(json.getInt("minHeight"));
			}
	
			if (json.has("properties")) {
				
				JSONObject propertiesData = json.getJSONObject("properties");
				
				PlantProperties plantProperties = new PlantProperties();
				
				if (propertiesData.has("Winterhard")) {
					JSONArray winterhardData = propertiesData.getJSONArray("Winterhard");
					String winterhardMessages = "";
					for (int i = 0; i < winterhardData.length(); i++){
						winterhardMessages += winterhardData.getString(i);
					}
					plantProperties.setWinterhard(winterhardMessages);
				}
				
				if (propertiesData.has("Vochtigheid")) {
					JSONArray vochtigheidData = propertiesData.getJSONArray("Vochtigheid");
					String vochtigheidMessages = "";
					for (int i = 0; i < vochtigheidData.length(); i++){
						vochtigheidMessages += vochtigheidData.getString(i);
					}
					plantProperties.setVochtigheid(vochtigheidMessages);
				}
				
				if (propertiesData.has("PH")) {
					JSONArray phData = propertiesData.getJSONArray("PH");
					String phMessages = "";
					for (int i = 0; i < phData.length(); i++){
						phMessages += phData.getString(i);
					}
					plantProperties.setPH(phMessages);
				}
				
				if (propertiesData.has("Evergreen")) {
					JSONArray evergreenData = propertiesData.getJSONArray("Evergreen");
					String evergreenMessages = "";
					for (int i = 0; i < evergreenData.length(); i++){
						evergreenMessages += evergreenData.getString(i);
					}
					plantProperties.setEvergreen(evergreenMessages);
				}
				
				if (propertiesData.has("Licht")) {
					JSONArray lichtData = propertiesData.getJSONArray("Licht");
					for (int i = 0; i < lichtData.length(); i++){
						plantProperties.addLicht(lichtData.getString(i));
					}
				}
				plant.setProperties(plantProperties);
			}
			
		} catch (JSONException e1){
			Log.e("MyGarden", "JSONException");
			e1.printStackTrace();
		} catch (ParseException e1){
			e1.printStackTrace();
		}
			
		return plant;
	}

	/**
	 * Parse one plant
	 * @param json
	 * @return
	 */
	public static PlantAction parsePlantAction(JSONObject json) {
		
		PlantAction plantAction = new PlantAction();

		try {

			plantAction.id = json.getInt("id");
			plantAction.name = json.getString("name");
			plantAction.startDay = json.getInt("startDay");
			plantAction.endDay = json.getInt("endDay");
			plantAction.repeat = json.getString("repeat");
			plantAction.period = json.getString("period");
			plantAction.todo = json.getInt("todo");
			
		} catch (JSONException e1){
			Log.e("MyGarden", "JSONException");
			e1.printStackTrace();
		} catch (ParseException e1){
			e1.printStackTrace();
		}
			
		return plantAction;
	}
	
	/**
	 * getPlant
	 * 
	 * @return
	 */
	public static Plant getPlant(String jsonString) {

		Plant plant = new Plant();
		try {
			Log.i("Plant:", jsonString);
			JSONObject json = new JSONObject(jsonString);
			plant = parsePlant(json);
	      	
		} catch (JSONException e1){
			Log.e("MyGarden", "JSONException");
			e1.printStackTrace();
		} catch (ParseException e1){
			e1.printStackTrace();
		}
    	
        return plant;
	}
	
	/**
	 * getTodos
	 * 
	 * @return
	 */
	public static ArrayList<Todo> getTodos(String jsonString) {

		ArrayList<Todo> items = new ArrayList<Todo>();
		try {
			
			JSONObject json = new JSONObject(jsonString);
			JSONObject jObject = json.getJSONObject("todos");
	        Iterator<?> keys = jObject.keys();

	        while( keys.hasNext() ){
	            String key = (String)keys.next();
	            if( jObject.get(key) instanceof JSONObject ){

					JSONObject json_data = jObject.getJSONObject(key);
					
					String sType = json_data.getString("type");
					JSONArray jsonActions = json_data.getJSONArray("actions");
					for (int k = 0; k < jsonActions.length(); k++){
						JSONObject jsonActionData = jsonActions.getJSONObject(k);
						JSONObject jsonP = jsonActionData.getJSONObject("plant");
						JSONObject jsonPA = jsonActionData.getJSONObject("plantAction");
						Todo todo = new Todo(sType, parsePlant(jsonP), parsePlantAction(jsonPA));
						items.add(todo);
					}
					
	            }
	        }
	      	
		} catch (JSONException e1){
			Log.e("MyGarden", "JSONException");
			e1.printStackTrace();
		} catch (ParseException e1){
			e1.printStackTrace();
		}
    	
        return items;
	}
	
}
