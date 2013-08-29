package com.example.wundergarten;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wundergarten.base.MijnTuinRemoteHandler;
import com.example.wundergarten.base.OnTodoSelected;
import com.example.wundergarten.model.Todo;
import com.example.wundergarten.services.MijnTuinServiceEndpoint;
import com.example.wundergarten.services.MijnTuinServiceEntry;
import com.example.wundergarten.util.ExpandableListFragment;
import com.example.wundergarten.util.ImageThreadLoader;
import com.example.wundergarten.util.ImageThreadLoader.ImageLoadedListener;
import com.example.wundergarten.util.TodosLoader;

public class TodoListFragment extends ExpandableListFragment implements
		LoaderManager.LoaderCallbacks<ArrayList<Todo>>,
		MijnTuinServiceEndpoint {

	/**
	 * Tag this class.
	 */
	final String TAG = getClass().getName();

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * Boolean to reflect whether the parent Activity is ready for events.
	 */
	protected Boolean mReady = true;

	/**
	 * Boolean to reflect access.
	 */
	protected Boolean mAccess = false;

	/**
	 * List to enqueue callbacks triggered when it’s not.
	 */
	protected List<Runnable> mPendingCallbacks = new LinkedList<Runnable>();

	/**
	 * Listens to the list clicks.
	 */
	private OnTodoSelected mOnTodoSelectedListener;

	/**
	 * List Adapter
	 */
	//TodoListAdapter mItemsAdapter;
	
	/**
	 * TodoExpandableListAdapter
	 */
	TodoExpandableListAdapter mAdapter;


	//ExpandableListView listView;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TodoListFragment() {
		Log.i(TAG, TAG + " fragment instantiated");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Retain the instance so the triggering activity is not lost.
		setRetainInstance(true);

		Log.i(TAG, TAG + " fragment onCreate");
	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		//View view = inflater.inflate(R.layout.fragment_list, container, false);
		View view = inflater.inflate(R.layout.fragment_expandable_list, container, false);

		Log.i(TAG, TAG + " fragment onCreateView");
		return view;

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.i(TAG, TAG + " fragment onViewCreate");

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mOnTodoSelectedListener = (OnTodoSelected) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "Must Implement OnTodoSelected.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Mark activity as not ready for events.
		mReady = false;
	}

	/**
	 * Tracks activity
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		Log.i(TAG, "ListFragmentBase::onActivityCreated");

		ArrayList<TodoGroup> groupData = new ArrayList<TodoGroup>();
		ArrayList<ArrayList<TodoChild>> childData = new ArrayList<ArrayList<TodoChild>>();

		mAdapter = new TodoExpandableListAdapter(getActivity(), groupData, childData);
		//listView.setIndicatorBounds(0, 20);
		//listView.setAdapter(mAdapter);
		//setListAdapter(mAdapter);
		
		super.onActivityCreated(savedInstanceState);
		
		// Tracks activity state to make sure we know when it's available.
		mReady = true;

		// Ensure all the Runnable objects that were marked as pending when we
		// weren’t ready are executed the moment we are
		int pendingCallbacks = mPendingCallbacks.size();

		while (pendingCallbacks-- > 0) {
			getActivity().runOnUiThread(mPendingCallbacks.remove(0));
		}

		//mItemsAdapter = new TodoListAdapter(getActivity(),
		//		android.R.layout.simple_list_item_activated_1);
		//setListAdapter(mItemsAdapter);

		// setHasOptionsMenu(true);

		// Prepare the loader. Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {

		//Todo item = mItemsAdapter.getItem(position);
		Todo item = (Todo) getListView().getItemAtPosition(position);
		
		Log.i(TAG, item.getTitle() + " has been clicked");

		// Example of how to notify the parent activity from this fragment.
		super.onListItemClick(listView, view, position, id);
		 
		// Let's do it here, seems a lot easier without a todo ID.
	    // Switch the main view to the todo in detail.
		TodoDetailFragment detailFragment = TodoDetailFragment.newInstance(position);
	    detailFragment.setTodo(item);
	    // Create new transaction
	    FragmentTransaction transaction = getFragmentManager().beginTransaction();
	    // Replace whatever is in the fragment_container view with this fragment,
	    // and add the transaction to the back stack
	    transaction.replace(R.id.mainFragment, detailFragment);
	    transaction.addToBackStack(null);
	    // Commit the transaction
        transaction.commit();

		mOnTodoSelectedListener.onItemSelected(item);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	@Override
	public MijnTuinServiceEntry getRemoteEntry() {
		return new MijnTuinServiceEntry(
				"http://api.mijntuin.org/todos?image_size=m");
	}

	@Override
	public Loader<ArrayList<Todo>> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created. This
		// sample only has one Loader with no arguments, so it is simple.
		TodosLoader loader = new TodosLoader(getActivity());
		if (loader instanceof MijnTuinRemoteHandler) {
			loader.setRemoteHandler(getRemoteEntry());
		}
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<Todo>> loader,
			ArrayList<Todo> data) {
		// Set the new data in the adapter.
		//mItemsAdapter.setData(data);
		mAdapter.setData(data);
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<Todo>> loader) {
		// Clear the data in the adapter.
		//mItemsAdapter.setData(null);
		mAdapter.setData(null);
	}

	/**
	 * Allows enqueuing a Runnable to be executed whenever the Fragment deems
	 * appropiate.
	 * 
	 * @param runnable
	 */
	public void runWhenReady(Runnable runnable) {
		if (mReady) {
			getActivity().runOnUiThread(runnable);
		} else {
			mPendingCallbacks.add(runnable);
		}
	}

	/**
	 * Sets the activated position of the items.
	 * 
	 * @param position
	 */
	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	public void setAccess(Boolean access) {
		mAccess = access;
	}

	public Boolean hasAccess() {
		return mAccess;
	}
	
	/**
	 * TodoGroup
	 */
	public class TodoGroup {
		private final String text;
		 
		public TodoGroup(String text){
			this.text = text;
		}
		 
		public String getText() {
			return text;
		}
	}
	
	/**
	 * TodoChild
	 */
	public class TodoChild {
		
		private Todo todo;
		 
		public TodoChild(Todo todo){
			this.todo = todo;
		}
		 
		public Todo getTodo() {
			return todo;
		}
		 
		public void setTodo(Todo todo) {
			this.todo = todo;
		}
	}
	
	/**
	 * public class TodoExpandableListAdapter
	 */
	public class TodoExpandableListAdapter extends
			BaseExpandableListAdapter {
		
		//private List<TodoGroup> groupData;
		//private List<List<TodoChild>> childData; 
		private ArrayList<TodoListFragment.TodoGroup> groupData;
		private ArrayList<ArrayList<TodoListFragment.TodoChild>> childData;
		
		private LayoutInflater inflater;
		
		/**
		 * ImageThreadLoader.
		 */
		private ImageThreadLoader imageLoader = new ImageThreadLoader();

		public TodoExpandableListAdapter(Context context,
				ArrayList<TodoListFragment.TodoGroup> groupData, 
				ArrayList<ArrayList<TodoListFragment.TodoChild>> childData) {
			this.groupData = groupData;
			this.childData = childData;
			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//LayoutInflater.from(context);
			
		}

		public Object getChild(int groupPosition, int childPosition) {
			return childData.get(groupPosition).get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View v;
			if (convertView == null) {
				v = newChildView(isLastChild, parent);
			} else {
				v = convertView;
			}
			bindChildView(childPosition, groupPosition, isLastChild, v, parent);
			return v;
		}

		/**
		 * Instantiates a new View for a child.
		 * 
		 * @param parent
		 *            The eventual parent of this new View.
		 * @return A new child View
		 */
		public View newChildView(boolean isLastChild, ViewGroup parent) {
			return getInflater().inflate(R.layout.child_layout, parent, false);
			//received errors when using the other overloaded inflate methods
			//and when trying to attach the inflated layout to the parent
		}

		/**
		 * @param childPosition
		 *            Position of the child in the childData list
		 * @param groupPosition
		 *            Position of the child's group in the groupData list
		 * @param v
		 *            The view to bind data to
		 * @param parent
		 *            The eventual parent of v.
		 */
		public void bindChildView(int childPosition,
				int groupPosition, boolean isLastChild, View v, ViewGroup parent) {
			
			TodoChild child = (TodoChild) getChild(groupPosition, childPosition);
			Todo item = child.getTodo();
			
			TextView textTitle = (TextView) v.findViewById(R.id.text);
			textTitle.setText(child.getTodo().getTitle());
					
			TextView textDesc = (TextView) v.findViewById(R.id.desc);
			textDesc.setText(child.getTodo().getFrequency());

			final ImageView image = (ImageView) v.findViewById(R.id.icon);

			// The image
			Bitmap cachedImage = null;
			try {
				cachedImage = imageLoader.loadImage(child.getTodo().plant.getImageRemotePath(),
						new ImageLoadedListener() {
							public void imageLoaded(Bitmap imageBitmap) {
								//item.plant.setImageBitmap(imageBitmap);
								image.setImageBitmap(imageBitmap);
								notifyDataSetChanged();
							}
						});
			} catch (MalformedURLException e) {
				Log.e("Menhireffect", "Bad remote image URL: " + item.plant.getImageRemotePath(), e);
			}

			if (cachedImage != null) {
				image.setImageBitmap(cachedImage);
			}
			
		}

		public int getChildrenCount(int groupPosition) {
			return childData.get(groupPosition).size();
		}

		public Object getGroup(int groupPosition) {
			return groupData.get(groupPosition);
		}

		public int getGroupCount() {
			return groupData.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			View v;
			if (convertView == null) {
				v = newGroupView(isExpanded, parent);
			} else {
				v = convertView;
			}
			bindGroupView(groupPosition, isExpanded, v, parent);
			return v;
		}

		/**
		 * Instantiates a new View for a group.
		 * 
		 * @param isExpanded
		 *            Whether the group is currently expanded.
		 * @param parent
		 *            The eventual parent of this new View.
		 * @return A new group View
		 */
		public View newGroupView(boolean isExpanded, ViewGroup parent) {
			return getInflater().inflate(R.layout.group_layout, parent, false);
			//received errors when using the other overloaded inflate methods
			//and when trying to attach the inflated layout to the parent
		}

		/**
		 * @param groupPosition
		 *            Position of the group in the groupData list
		 * @param isExpanded
		 *            Whether the group is currently expanded.
		 * @param v
		 *            The view to bind data to
		 * @param parent
		 *            The eventual parent of v.
		 */
		public void bindGroupView(int groupPosition, boolean isExpanded, View v,
				ViewGroup parent) {
			TodoGroup group = (TodoGroup) getGroup(groupPosition);
			 
			TextView textView = (TextView) v.findViewById(R.id.textView);
			textView.setText(group.getText());
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}

		public LayoutInflater getInflater() {
			return inflater;
		}

		public void setData(List<Todo> data) {
			//clear();
			ArrayList<String> categories = new ArrayList<String>();
			if (data != null) {

				for (Todo todo : data) {
					
					String action = todo.getAction();
					
					// If the category was already used as group, skip it.
					if (!categories.contains(action)) {
						
						categories.add(action);

						TodoGroup todoGroup = new TodoGroup(action);
						this.groupData.add(todoGroup);
						
					}
					else {
						
					}

					TodoChild todoChild = new TodoChild(todo);
					ArrayList<TodoChild> aList = new ArrayList<TodoChild>();
					aList.add(todoChild);
					this.childData.add(aList);
					
				}
				
			}
		}

	}

	/**
	 * GardenListAdapter Adapts the List so it's ready for the view.
	 */
	public class TodoListAdapter extends ArrayAdapter<Todo> {

		/**
		 * Layout inflater.
		 */
		private final LayoutInflater mInflater;
		
		/**
		 * ImageThreadLoader.
		 */
		private ImageThreadLoader imageLoader = new ImageThreadLoader();

		/**
		 * Constructor.
		 * 
		 * @param context
		 * @param layout
		 */
		public TodoListAdapter(Context context, int layout) {
			super(context, layout);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void setData(List<Todo> data) {
			clear();
			if (data != null) {
				for (Todo todo : data) {
					add(todo);
				}
			}
		}

		/**
		 * Populate new items in the list.
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view;
			TextView textTitle;
			final ImageView image;

			if (convertView == null) {
				view = mInflater.inflate(R.layout.list_item_icon_text, parent,
						false);
			} else {
				view = convertView;
			}

			try {
				textTitle = (TextView) view.findViewById(R.id.text);
				image = (ImageView) view.findViewById(R.id.icon);
			} catch (ClassCastException e) {
				Log.e("Menhireffect",
						"Your layout must provide an image view and text view with ID's icon and text.",
						e);
				throw e;
			}

			final Todo item = getItem(position);

			// The image
			Bitmap cachedImage = null;
			try {
				cachedImage = imageLoader.loadImage(item.plant.getImageRemotePath(),
						new ImageLoadedListener() {
							public void imageLoaded(Bitmap imageBitmap) {
								item.plant.setImageBitmap(imageBitmap);
								image.setImageBitmap(imageBitmap);
								notifyDataSetChanged();
							}
						});
			} catch (MalformedURLException e) {
				Log.e("Menhireffect", "Bad remote image URL: " + item.plant.getImageRemotePath(), e);
			}

			if (cachedImage != null) {
				image.setImageBitmap(cachedImage);
			}

			textTitle.setText(item.getTitle() + "\n" + item.getAction() 
					+ "\n" + item.getFrequency());

			return view;
		}

	}
}