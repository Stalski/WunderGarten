package com.example.wundergarten.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;

import com.example.wundergarten.MyGardenListActivity;
import com.example.wundergarten.PlantsListActivity;
import com.example.wundergarten.PrepareRequestTokenActivity;
import com.example.wundergarten.R;
import com.example.wundergarten.SettingsActivity;
import com.example.wundergarten.TodoListActivity;
import com.example.wundergarten.exceptions.IllegalPageException;
import com.example.wundergarten.services.MijnTuinService;

public class WundergartenActivityBase extends FragmentActivity {
	
	/**
	 * Tag this class.
	 */
	final String TAG = getClass().getName();

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	protected boolean mTwoPane;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, TAG + " Ondestroy");
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i(TAG, TAG + " onStart ");
		loadOnStart();
	}
	
	/**
	 * Loads whatever should load OnStart.
	 */
	public void loadOnStart() {

		// AsyncTask if connected.
		if (isConnected()) {
			
			MijnTuinService mijnService = MijnTuinService.getInstance();
			
			Log.i(TAG, "WunderkrautActivityBase::loadOnStart Checking access");
			final Boolean access = mijnService.hasAccess(this);
			
			// Perform the actual work in the loadPage.
			if (access) {
				Log.i(TAG, "WunderkrautActivityBase::loadOnStart --> Load page ");
				loadPage();
			}
			// Otherwise, get access first by starting the OAuth steps.
			else {
				Log.i(TAG, "WunderkrautActivityBase::loadOnStart Getting access and start OAuth steps");
				if (!mijnService.isInProgress()) {
					Intent intent = new Intent(this,
		                    PrepareRequestTokenActivity.class);
					//intent.setClass(this, PrepareRequestTokenActivity.class);
					startActivity(intent);
				}
			}
			
		} else {
			showErrorPage();
		}
	}

	/**
	 * Uses AsyncTask subclass to fetch my garden.
	 */
	public void loadPage() {
		throw new IllegalPageException("Your class must implement loadPage");
	}
	
	/**
	 *  Displays an error if the app is unable to load content.
	 */
	protected void showErrorPage() {
		setContentView(R.layout.error);

		// The specified network connection is not available. Displays error
		// message.
		WebView myWebView = (WebView) findViewById(R.id.webview);
		myWebView.loadData(getResources().getString(R.string.connection_error),
				"text/html", null);
	}
	
	/**
	 * True if there is wifi or any other mobile connection.
	 * 
	 * @return Boolean
	 */
	public Boolean isConnected() {
		// Gets the user's network preference settings
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		// Retrieves a string value for the preferences. The second parameter
		// is the default value to use if a preference value is not found.
		final String sPref = sharedPrefs.getString("listPref", "Any");

		return (sPref.equals("Any") || sPref.equals("Wi-Fi"));
	}

	// Populates the activity's options menu.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	/**
	 * Handles the user's menu selection.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.mygarden_list:
				Log.i(TAG, "MyGardenListActivity::onOptionsItemSelected GardenList");
	        	Intent myGardenIntent = new Intent(this, MyGardenListActivity.class);
	        	myGardenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(myGardenIntent);
	            return true;
			case R.id.todos_list:
				Log.i(TAG, "MyGardenListActivity::onOptionsItemSelected GardenList");
	        	Intent todosIntent = new Intent(this, TodoListActivity.class);
	        	todosIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(todosIntent);
	            return true;
	        case R.id.plants_list:
				Log.i(TAG, "MyGardenListActivity::onOptionsItemSelected PlantsList");
	        	Intent listIntent = new Intent(this, PlantsListActivity.class);
	        	//listIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(listIntent);
	            return true;
	        case R.id.settings:
				Log.i(TAG, "MyGardenListActivity::onOptionsItemSelected Settings");
	        	Intent settingIntent = new Intent(this, SettingsActivity.class);
	        	settingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(settingIntent);
	            return true;
			case R.id.refresh:
				//loadPage();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
}
