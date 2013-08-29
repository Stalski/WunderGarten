package com.example.wundergarten;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wundergarten.base.MijnTuinRemoteHandler;
import com.example.wundergarten.base.OnPlantSelected;
import com.example.wundergarten.model.Plant;
import com.example.wundergarten.services.MijnTuinServiceEndpoint;
import com.example.wundergarten.services.MijnTuinServiceEntry;
import com.example.wundergarten.util.ImageThreadLoader;
import com.example.wundergarten.util.ImageThreadLoader.ImageLoadedListener;
import com.example.wundergarten.util.MyGardenLoader;

public class MyGardenListFragment extends ListFragment
		implements LoaderManager.LoaderCallbacks<ArrayList<Plant>>,
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
	private OnPlantSelected mOnItemSelectedListener;
	
	/**
	 * List Adapter
	 */
	PlantsListAdapter mItemsAdapter;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public MyGardenListFragment() {
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
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        
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
	        mOnItemSelectedListener = (OnPlantSelected) activity;
	    } catch (ClassCastException e) {
	        throw new ClassCastException(activity.toString() + "Must Implement OnPlantSelected.");
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
	    super.onActivityCreated(savedInstanceState);
	    Log.i(TAG, "ListFragmentBase::onActivityCreated");
	    // Tracks activity state to make sure we know when it's available.
	    mReady = true;

	    // Ensure all the Runnable objects that were marked as pending when we
	    // weren’t ready are executed the moment we are
	    int pendingCallbacks = mPendingCallbacks.size();

	    while (pendingCallbacks-- > 0) {
	        getActivity().runOnUiThread(mPendingCallbacks.remove(0));
	    }
	    
		mItemsAdapter = new PlantsListAdapter(getActivity(),
				android.R.layout.simple_list_item_activated_1);
		setListAdapter(mItemsAdapter);

		//setHasOptionsMenu(true);

		// Prepare the loader. Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {

		Log.i(TAG, "ListFragmentBase OnListItemClick ; This will notify the parent activity. ");
		Plant item = mItemsAdapter.getItem(position);
        
        // Example of how to notify the parent activity from this fragment.
		super.onListItemClick(listView, view, position, id);

        mOnItemSelectedListener.onItemSelected(item.getId());

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
				"http://api.mijntuin.org/garden?user_id=me&image_size=m");
	}

	@Override
	public Loader<ArrayList<Plant>> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created. This
		// sample only has one Loader with no arguments, so it is simple.
		MyGardenLoader loader = new MyGardenLoader(getActivity());
		if (loader instanceof MijnTuinRemoteHandler) {
			loader.setRemoteHandler(getRemoteEntry());
		}
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<Plant>> loader,
			ArrayList<Plant> data) {
		// Set the new data in the adapter.
		mItemsAdapter.setData(data);
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<Plant>> loader) {
		// Clear the data in the adapter.
		mItemsAdapter.setData(null);
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
	    }
	    else {
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
	 * GardenListAdapter
	 * Adapts the List so it's ready for the view.
	 */
	public class PlantsListAdapter extends ArrayAdapter<Plant> {
		
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
		 * @param context
		 * @param layout
		 */
		public PlantsListAdapter(Context context, int layout) {
			super(context, layout);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
	
		public void setData(List<Plant> data) {
			clear();
			if (data != null) {
				for (Plant plant : data) {
					add(plant);
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

			final Plant item = getItem(position);

			// The image
			Bitmap cachedImage = null;
			try {
				cachedImage = imageLoader.loadImage(item.getImageRemotePath(),
						new ImageLoadedListener() {
							public void imageLoaded(Bitmap imageBitmap) {
			                	item.setImageBitmap(imageBitmap);
								image.setImageBitmap(imageBitmap);
								notifyDataSetChanged();
							}
						});
			} catch (MalformedURLException e) {
				Log.e("Menhireffect", "Bad remote image URL: " + item.getImageRemotePath(), e);
			}

			if (cachedImage != null) {
				image.setImageBitmap(cachedImage);
			}

			// The title
			textTitle.setText(item.getTitle());

			return view;
		}
	
	}
	
}
