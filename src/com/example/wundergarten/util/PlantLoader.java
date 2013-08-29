package com.example.wundergarten.util;

import org.scribe.exceptions.OAuthException;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.wundergarten.model.Plant;
import com.example.wundergarten.services.MijnTuinJSONParser;
import com.example.wundergarten.services.MijnTuinService;

public class PlantLoader extends AsyncTaskLoader<Plant> {

	/**
	 * Tag for this class.
	 */
	final String TAG = getClass().getName();
	
	/**
	 * Local Plant
	 */
    protected Plant mItem;

    /**
     * Id
     */
    public Integer id;
    
    /**
     * Arguments
     */
    public Bundle args;

    /**
     * Constructor.
     * @param context
     */
    public PlantLoader(Context context) {
        super(context);
    }

    /**
     * sets the id
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    
    /**
     * sets the arguments.
     */
    public void setArgs(Bundle args) {
    	this.args = args;
    }
    
    /**
     * Gets the Endpoint for this loader task.
     * 
     * @return String endpoint
     */
    protected String getEndpoint() {
    	return String.format("http://api.mijntuin.org/plants/show?plant_id=%s&image_size=f", 
    			id);
    }

    /**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override 
    public Plant loadInBackground() {
    	return loadItem();
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override 
    public void deliverResult(Plant item) {
        if (isReset() && item != null) {
            onReleaseResources(item);
        }
        Plant oldItem = item;
        mItem = item;

        if (isStarted()) {
            super.deliverResult(item);
        }

        if (oldItem != null) {
            onReleaseResources(oldItem);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override 
    protected void onStartLoading() {
        if (mItem != null) {
            deliverResult(mItem);
        } else {
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override 
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override 
    public void onCanceled(Plant item) {
        super.onCanceled(item);
        onReleaseResources(item);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mItem != null) {
            onReleaseResources(mItem);
            mItem = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(Plant item) {
        // For a simple ArrayList<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
    
    /**
     * Calls the remote API and returns the result.
     * 
     * @return ArrayList<Plant>
     */
    protected Plant loadItem() {

    	Plant item = new Plant();
    	
		try {
			MijnTuinService mijnService = MijnTuinService.getInstance();
			Log.i(TAG, "MijnTuinDashboardTask::doInBackground will get MijnTuin API response");
			String result = mijnService.call(getEndpoint());
			item = MijnTuinJSONParser.getPlant(result);
			
		} catch (OAuthException e) {
			Log.e(TAG, "Error during api call", e);
			e.printStackTrace();
			return null;
		}
    	
        return item;
    }
}
