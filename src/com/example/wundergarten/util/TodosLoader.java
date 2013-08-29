package com.example.wundergarten.util;

import java.util.ArrayList;

import org.scribe.exceptions.OAuthException;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.wundergarten.base.MijnTuinRemoteHandler;
import com.example.wundergarten.model.Todo;
import com.example.wundergarten.services.MijnTuinJSONParser;
import com.example.wundergarten.services.MijnTuinService;
import com.example.wundergarten.services.MijnTuinServiceEntry;


/**
 * TodosLoader
 */
public class TodosLoader extends AsyncTaskLoader<ArrayList<Todo>>
		implements MijnTuinRemoteHandler {

	/**
	 * Tag for this class.
	 */
	final String TAG = getClass().getName();
	
	/**
	 * Local array list of Plants
	 */
    protected ArrayList<Todo> mItems;
    
    
    protected MijnTuinServiceEntry mServiceEntry;

    /**
     * Constructor.
     * @param context
     */
    public TodosLoader(Context context) {
        super(context);
    }
    
    /**
     * Sets a helper class to handle the remote call.
     */
    public void setRemoteHandler(MijnTuinServiceEntry entry) {
    	this.mServiceEntry = entry;
    }

    /**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override 
    public ArrayList<Todo> loadInBackground() {
    	return loadItems();
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override 
    public void deliverResult(ArrayList<Todo> items) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (items != null) {
                onReleaseResources(items);
            }
        }
        ArrayList<Todo> oldItems = items;
        mItems = items;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(items);
        }

        // At this point we can release the resources associated with
        // 'oldItems' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldItems != null) {
            onReleaseResources(oldItems);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override 
    protected void onStartLoading() {
        if (mItems != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mItems);
        } else {
            // Start a load.
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
    public void onCanceled(ArrayList<Todo> items) {
        super.onCanceled(items);

        // At this point we can release the resources associated with 'items'
        // if needed.
        onReleaseResources(items);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'items'
        // if needed.
        if (mItems != null) {
            onReleaseResources(mItems);
            mItems = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(ArrayList<Todo> items) {
        // For a simple ArrayList<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
    
    /**
     * Calls the remote API and returns the result.
     * 
     * @return ArrayList<Todo>
     */
    protected ArrayList<Todo> loadItems() {

    	ArrayList<Todo> items = new ArrayList<Todo>();
    	
		try {
			MijnTuinService mijnService = MijnTuinService.getInstance();
			Log.i(TAG, "MijnTuinDashboardTask::doInBackground will get MijnTuin API response");
			String result = mijnService.call(mServiceEntry.getEndPoint());
			items = MijnTuinJSONParser.getTodos(result);
			
		} catch (OAuthException e) {
			Log.e(TAG, "Error during api call", e);
			e.printStackTrace();
			return null;
		}
    	
        return items;
    }
}
