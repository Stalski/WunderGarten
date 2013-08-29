package com.example.wundergarten;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.wundergarten.base.OnTodoSelected;
import com.example.wundergarten.base.WundergartenActivityBase;
import com.example.wundergarten.model.Todo;

public class TodoListActivity extends WundergartenActivityBase 
	implements OnTodoSelected  {

	/**
	 * Tag this class.
	 */
	final String TAG = getClass().getName();
	
	/**
	 * MyGarden items
	 */
	public ArrayList<Todo> items = new ArrayList<Todo>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "TodoListActivity.oncreate ");
		
		setContentView(R.layout.main);

		Log.i("Wundergarten", TAG + "Display List and detail separately");

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		TodoListFragment fragment = new TodoListFragment();
		ft.replace(R.id.mainFragment, fragment);
		ft.commit();

	}

	/**
	 * Uses AsyncTask subclass to fetch my garden.
	 * 
	 * @see WundergartenActivityBase.onStart()
	 */
	public void loadPage() {
		Log.i(TAG, "TodoListActivity::LoadPage ! Nothing to do anymore here :)");
	}

	/**
	 * Callback method from {@link PlantsListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(Todo todo) {
		Log.i(TAG, "TodoListActivity onItemSelected with plant id: " + todo.plant.getId());
		// In single-pane mode, simply start the detail activity
		// for the selected item ID.
		/*Intent detailIntent = new Intent(this,
				TodoDetailActivity.class);
		detailIntent.putExtra(TodoDetailFragment.ARG_ITEM_ID, todo.plant.getId());
		startActivity(detailIntent);*/
	}
}
