package com.example.wundergarten;

import java.net.MalformedURLException;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wundergarten.model.Plant;
import com.example.wundergarten.model.PlantProperties;
import com.example.wundergarten.util.ImageThreadLoader;
import com.example.wundergarten.util.ImageThreadLoader.ImageLoadedListener;
import com.example.wundergarten.util.PlantLoader;

/**
 * A fragment representing a single Botanical item detail screen. This fragment
 * is either contained in a {@link PlantsListActivity} in two-pane mode
 * (on tablets) or a {@link PlantDetailActivity} on handsets.
 */
public class PlantDetailFragment extends Fragment
	implements LoaderManager.LoaderCallbacks<Plant> {
	
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * Simple Adapter
	 */
	//public PlantAdapter mPlantAdapter;
	public Plant plant;
	
	private View rootView;
	
	public ImageView image;

    /**
     * Create a new instance of CountingFragment, providing "number"
     * as an argument.
     */
    public static PlantDetailFragment newInstance(int itemId) {
    	
    	PlantDetailFragment f = new PlantDetailFragment();
    	Log.i("Wundergarten", "PlantDetailFragment initiated");

        // Supply number input as an argument.
        Bundle args = new Bundle();
        args.putInt(ARG_ITEM_ID, itemId);
        f.setArguments(args);

        return f;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Retain the instance so the triggering activity is not lost.
		setRetainInstance(true);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			String id = getArguments().getString(ARG_ITEM_ID);

	        Bundle args = new Bundle();
	        args.putString(ARG_ITEM_ID, id);
	        
			getLoaderManager().initLoader(Integer.parseInt(id), args, this);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    rootView = inflater.inflate(
				R.layout.fragment_plant_detail, container, false);

		Log.i("PlantDetailFragment", "PlantDetailFragment::onCreateView");
		
		// Show the dummy content as text in a TextView.
		if (plant != null) {
			((TextView) rootView.findViewById(R.id.plant_detail))
					.setText(plant.getTitle());
		}

		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    // Todo check if it's here or in "onCreate"
		//getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Plant> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created. This
		// sample only has one Loader with no arguments, so it is simple.
		PlantLoader plantLoader = new PlantLoader(getActivity());
		plantLoader.setId(id);
		plantLoader.setArgs(args);
		return plantLoader;
	}

	@Override
	public void onLoadFinished(Loader<Plant> loader, Plant data) {
		plant = data;
		
		TextView textView1 = (TextView) rootView.findViewById(R.id.plant_detail);
		textView1.setText(plant.getTitle());
	    
		// Prepare the image to be populated later on.
	    image = (ImageView) rootView.findViewById(R.id.plantImage);

		PlantProperties properties = plant.getProperties();
		
		if (!properties.ph.isEmpty()) {
			TextView textPH = (TextView) rootView.findViewById(R.id.plantPH);
			textPH.setText(getResources().getString(R.string.ph) + ": " + properties.ph);
		}
		
		if (!properties.vochtigheid.isEmpty()) {
			TextView textHumidity = (TextView) rootView.findViewById(R.id.plantHumidity);
			textHumidity.setText(getResources().getString(R.string.humidity) + ": " + properties.vochtigheid);
		}
		
		if (!properties.evergreen.isEmpty()) {
			TextView textEvergreen = (TextView) rootView.findViewById(R.id.plantEvergreen);
			textEvergreen.setText(getResources().getString(R.string.evergreen) + ": " + properties.evergreen);
		}
		
		if (!properties.winterhard.isEmpty()) {
			TextView textWinterhard = (TextView) rootView.findViewById(R.id.plantWinterhard);
			textWinterhard.setText(getResources().getString(R.string.winterhard) + ": " + properties.winterhard);
		}
		
		if (!properties.licht.isEmpty()) {
			TextView textLicht = (TextView) rootView.findViewById(R.id.plantLight);
			String output = "";
			output += getResources().getString(R.string.light) + ": ";
			for (String l : properties.licht) {
				output += l + " ";
			}
			textLicht.setText(output);
		}
		
		TextView textView2 = (TextView) rootView.findViewById(R.id.plant_detail_desc);
		textView2.setText(Html.fromHtml(plant.getDescription()));

		// The image
		Bitmap cachedImage = null;
		try {

			ImageThreadLoader imageLoader = new ImageThreadLoader();
			cachedImage = imageLoader.loadImage(plant.getImageRemotePath(),
					new PlantImageLoadedListener());
		} catch (MalformedURLException e) {
			Log.e("Menhireffect", "Bad remote image URL: " + plant.getImageRemotePath(), e);
		}

		if (cachedImage != null) {
			image.setImageBitmap(cachedImage);
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Plant> loader) {
		plant = null;
	}
	
	/**
	 * PlantImageLoadedListener
	 */
	public class PlantImageLoadedListener implements ImageLoadedListener {
		public void imageLoaded(Bitmap imageBitmap) {
			Log.i("PlantImageLoadedListener", "image loaded ...");
			//plant.setImageBitmap(imageBitmap);
			image.setImageBitmap(imageBitmap);
		}
		
	}
	
}
