package com.example.wundergarten;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.wundergarten.base.OnPlantSelected;
import com.example.wundergarten.base.WundergartenActivityBase;
import com.example.wundergarten.model.Plant;

/**
 * An activity representing a list of Botanical items.
 */
public class MyGardenListActivity extends WundergartenActivityBase 
		implements OnPlantSelected  {
	
	/**
	 * Tag this class.
	 */
	final String TAG = getClass().getName();
	
	/**
	 * MyGarden items
	 */
	public ArrayList<Plant> items = new ArrayList<Plant>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "MyGardenListActivity.oncreate ");
		
		setContentView(R.layout.main);

		// Two panes and fragments.
		if (findViewById(R.id.plant_detail_container) != null) {

			Log.i("Wundergarten", TAG + "Display Details and list in two panes");
			
			// Activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((MyGardenListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.mygarden_list))
					.setActivateOnItemClick(true);
			/*
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			PlantDetailFragment fragment = PlantDetailFragment.newInstance(2);
			//f.setId(this.mDetailId);
			ft.replace(R.id.mainFragment, fragment);
			ft.commit();
			*/
		}
		// Simple list fragment.
		else {

			Log.i("Wundergarten", TAG + "Display List and detail separately");

			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			MyGardenListFragment fragment = new MyGardenListFragment();
			ft.replace(R.id.mainFragment, fragment);
			ft.commit();
			
		}

	}

	/**
	 * Uses AsyncTask subclass to fetch my garden.
	 * 
	 * @see WundergartenActivityBase.onStart()
	 */
	public void loadPage() {
		Log.i(TAG, "MyGardenListActivity::LoadPage ! Nothing to do anymore here :)");
	}

	/**
	 * Callback method from {@link PlantsListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		
		if (mTwoPane) {
			Log.i(TAG, "Activity::onItemSelected in two pane mode with id: " + id);
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(PlantDetailFragment.ARG_ITEM_ID, id);
			PlantDetailFragment fragment = new PlantDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.plant_detail_container, fragment)
					.commit();

		} else {
			Log.i(TAG, "Activity::onItemSelected in single pane mode with id: " + id);
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					PlantDetailActivity.class);
			detailIntent.putExtra(PlantDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}

}
