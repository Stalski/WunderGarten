package com.example.wundergarten.util;

import android.os.AsyncTask;

public class PlantActionTask extends AsyncTask<String, Void, String> {

	/**
	 * Keep the context to update the corresponding parent activity.
	 */
	private PlantActionNotifier mContext;
	
    /**
     * Constructor.
     * @param context
     */
    public PlantActionTask(PlantActionNotifier context) {
        this.mContext = context;
    }
	
	@Override
    protected String doInBackground(String... params) {
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
    	this.mContext.notifyPlantActionDone(result);
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
