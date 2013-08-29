package com.example.wundergarten;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wundergarten.model.Todo;
import com.example.wundergarten.util.PlantActionNotifier;

/**
 * A fragment representing a single Todo detail screen. 
 */
public class TodoDetailFragment extends Fragment implements PlantActionNotifier {

	public Todo todo;

    /**
     * Create a new instance of CountingFragment, providing "number"
     * as an argument.
     */
    public static TodoDetailFragment newInstance(int itemId) {
    	TodoDetailFragment f = new TodoDetailFragment();
        return f;
    }
    
    /**
     * Set a todo from elsewhere.
     * @param todo
     */
    public void setTodo(Todo todo) {
	    Log.i("TodoDetailFragment", "setting todo: " + todo.getTitle());
    	this.todo = todo;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Retain the instance so the triggering activity is not lost.
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View rootView = inflater.inflate(
				R.layout.fragment_todo_detail, container, false);

		// Show the content as TextViews.
	    Log.i("TodoDetailFragment", "root view used " + todo.getTitle());
		if (todo != null) {
			
		    Log.i("TodoDetailFragment", "root view used " + todo.getTitle());
			TextView titleView = (TextView) rootView.findViewById(R.id.todoTitle);
			titleView.setText(todo.getTitle());

			TextView textAction = (TextView) rootView.findViewById(R.id.action);
			textAction.setText(todo.getAction());

			TextView textPeriod = (TextView) rootView.findViewById(R.id.period);
			textPeriod.setText(getResources().getString(R.string.plant_period) + ": " + todo.getPeriod());

			TextView textHarvest = (TextView) rootView.findViewById(R.id.dates);
			textHarvest.setText(getResources().getString(R.string.harvest_on) + ": " + todo.getHarvestDates());

			TextView textFrequency = (TextView) rootView.findViewById(R.id.frequency);
			textFrequency.setText(getResources().getString(R.string.frequency) + ": " + todo.getFrequency());

			Button buttonMarkAsDone = (Button) rootView.findViewById(R.id.markAsDone);
			buttonMarkAsDone.setOnClickListener(new MarkTodoAsDone());
			
			Button buttonRemove = (Button) rootView.findViewById(R.id.remove);
			buttonRemove.setOnClickListener(new TodoRemoval());

		}

		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    // Todo check if it's here or in "onCreate"
		//getLoaderManager().initLoader(0, null, this);
	}
	
	/**
	 * notifyPlantActionDone
	 * 
	 * @param message
	 */
	public void notifyPlantActionDone(String message) {
		Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * MarkTodoAsDone
	 */
	class MarkTodoAsDone implements Button.OnClickListener {
		@Override
	    public void onClick(View v) {
			// We could check the private buttonMarkAsDon ... but it's hard for now.
			Toast.makeText(v.getContext(), "Hello!! button Clicked", Toast.LENGTH_SHORT).show();
		}
	}

	
	/**
	 * TodoRemoval
	 */
	class TodoRemoval implements Button.OnClickListener {
		@Override
	    public void onClick(View v) {
			// We could check the private buttonMarkAsDon ... but it's hard for now.
			Toast.makeText(v.getContext(), "Hello!! button Clicked", Toast.LENGTH_SHORT).show();
		}
	}
	
}
