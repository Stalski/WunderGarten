package com.example.wundergarten;

import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.wundergarten.services.MijnTuinService;

public class PrepareRequestTokenActivity extends Activity {

	// Tag this class.
	final String TAG = getClass().getName();

	// The service needed to authenticate and use this app.
	public MijnTuinService service;
	
	// Local storage of the requestToken.
	private Token requestToken;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Connect to the MijnTuin service.
		service = MijnTuinService.getInstance();
		Log.i(TAG, "PrepareRequestTokenActivity::onCreate");
		
		// Start activity is access token already known.
		if (service.hasSharedAccess(this)) {
			startActivity(new Intent(this, TodoListActivity.class));
		}
		else {

			OAuthRequestTokenTask mijntuinTask = new OAuthRequestTokenTask(this);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				mijntuinTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				mijntuinTask.execute();
			}
		}
		
	}
	
	/**
	 * Called when the OAuthRequestTokenTask finishes (user has authorized the request token).
	 * The callback URL will be intercepted here.
	 */
	@Override
	public void onNewIntent(Intent intent) {
		Log.i(TAG, "PrepareRequestTokenActivity.onNewIntent");
		super.onNewIntent(intent);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		final Uri uri = intent.getData();
		if (uri != null && uri.getScheme().equals(MijnTuinService.OAUTH_CALLBACK_SCHEME)) {
			new RetrieveAccessTokenTask(this, prefs).execute(uri);
			finish();
			return;
		}

		String token = prefs.getString(MijnTuinService.OAUTH_TOKEN, "");
		String secret = prefs.getString(MijnTuinService.OAUTH_TOKEN_SECRET, "");
		Log.i(TAG, token + " ---- " + secret);
		if (token.equals("")) {
			Log.i(TAG, "we can use the token here!");
		}
	}
	
	/**
	 * OAuthRequestTokenTask
	 * 
	 * Handles the Request token for the MijnTuin API.
	 */
	public class OAuthRequestTokenTask extends AsyncTask<Void, Void, String> {

		final String TAG = getClass().getName();
		private Context	context;

		/**
		 * Constructor OAuthRequestTokenTask
		 * 
		 * We pass the OAuth consumer and provider.
		 * 
		 * @param 	context
		 * 			Required to be able to start the intent to launch the browser.
		 */
		public OAuthRequestTokenTask(Context context) {
			this.context = context;
		}

		/**
		 * doInBackground
		 * 
		 * Retrieve the OAuth Request Token and present a browser to the user
		 * to authorize the token.
		 */
		@Override
		protected String doInBackground(Void... params) {

			// String url = params[0];
			try {
				requestToken = service.getRequestToken();
				Log.i(TAG, "Request token = " + requestToken.toString());

				return service.getAuthorizationUrl(requestToken);
				
			} catch (OAuthException e) {
				Log.e(TAG, "Error during OAUth retrieve request token", e);
				e.printStackTrace();
				return "";
			}
		}
		
		/**
		 * Called when the background task is completed.
		 */
		protected void onPostExecute(String authURL) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authURL))
				.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
			context.startActivity(intent);
	    }
	}
	
	
	/**
	 * RetrieveAccessTokenTask
	 * 
	 * Asynchronous task to get the access token.
	 */
	public class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, String> {

		final String TAG = getClass().getName();

		private Context	context;
		private SharedPreferences prefs;

		public RetrieveAccessTokenTask(Context context, SharedPreferences prefs) {
			this.context = context;
			this.prefs = prefs;
		}

		/**
		 * Retrieve the oauth_verifier, and store the oauth and oauth_token_secret 
		 * for future API calls.
		 */
		@Override
		protected String doInBackground(Uri...params) {
			final Uri uri = params[0];

			try {
				
				// Prepare the verifier and get the access token.
				final String oauth_verifier = uri.getQueryParameter("oauth_verifier");
				Verifier v = new Verifier(oauth_verifier);
				Token accessToken = service.getAccessToken(requestToken, v);
				Log.i(TAG, "Access token = " + accessToken.toString());
				
				// Store the access token.
				final Editor edit = prefs.edit();
				edit.putString(MijnTuinService.OAUTH_TOKEN, accessToken.getToken());
				edit.putString(MijnTuinService.OAUTH_TOKEN_SECRET, accessToken.getSecret());
				edit.commit();
				Log.i(TAG, "Adjusted preferences with the access token");
				
				String token = prefs.getString(MijnTuinService.OAUTH_TOKEN, "");
				String secret = prefs.getString(MijnTuinService.OAUTH_TOKEN_SECRET, "");
				
				service.setTokenWithSecret(token, secret);

			} catch (Exception e) {
				Log.e(TAG, "OAuth - Access Token Retrieval Error", e);
			}

			return "success";
		}
		
		protected void onPostExecute(String text) {
			Log.i(TAG, "RetrieveAccessTokenTask::onPostExecute Starts a new intent for TodoListActivity");
			// This should be needed but it isn't. I'd like to find out why.
			//context.startActivity(new Intent(context, MyGardenListActivity.class));
			context.startActivity(new Intent(context, TodoListActivity.class));
			
	    }
	}
	
	
}
