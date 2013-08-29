package com.example.wundergarten.services;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class MijnTuinService {
	
	private static MijnTuinService instance = new MijnTuinService();
	
	// Local service.
	private OAuthService service;
	
	// Local storage for the accessToken.
	private Token accessToken;

	// The callback for the service authentication.
	public static final String	OAUTH_CALLBACK_SCHEME	= "oauth";
	public static final String	OAUTH_CALLBACK_HOST		= "mijntuin";
	public static final String	CALLBACK		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
	
    // Consumer key / secret.
	public static final String CONSUMER_KEY 	= "b0e9d1c44d139f6ad0a3ad797e926a15";
	public static final String CONSUMER_SECRET 	= "fdad7eccbe5fca94be83eacbc1704a20";
	
	public static final String OAUTH_TOKEN = "mijntuin_token";
	public static final String OAUTH_TOKEN_SECRET = "mijntuin_secret";
	
	// Boolean to indicate the status of the access token.
	private Boolean hasAccess = false;
	
	/**
	 * Indicates whether a request / access token authorization is in progress.
	 */
	private Boolean inProgress = false;
	
	
	/**
	 * MijnTuinService
	 * 
	 * @param service
	 */
	private MijnTuinService() {
		service = getService();
	}
 
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clone is not allowed.");
    }
	
	/**
	 * Instantiator.
	 * 
	 * @return MijnTuinService
	 */
    public static synchronized MijnTuinService getInstance() {
        return instance;
	}
    
    /**
     * hasAccess
     */
    public Boolean hasAccess(Activity activity) {
    	if (this.hasAccess) {
    		return true;
    	}
    	else {
    		if (hasSharedAccess(activity)) {
    			this.hasAccess = true;
        	}
    		return this.hasAccess;
    	}
    }
    
    /**
     * Has Access in the shared preferences.
     * @param activity
     * @return sharedAccess
     */
    public Boolean hasSharedAccess(Activity activity) {

    	Boolean sharedAccess = false;
    	
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		String token = prefs.getString(MijnTuinService.OAUTH_TOKEN, "");
		String secret = prefs.getString(MijnTuinService.OAUTH_TOKEN_SECRET, "");
		Log.i("MijnTuinService", "Checking shared access , token: " + token);
		if (!token.isEmpty() && !secret.isEmpty()) {
			setTokenWithSecret(token, secret);
			sharedAccess = true;
    	}
		
		return sharedAccess;
    }
    
    /**
     * Get the request token.
     * @return
     */
    public Token getRequestToken() {
    	inProgress = true;
    	return service.getRequestToken();
    }
    
    /**
     * Get the authorization url.
     * @return Url
     */
    public String getAuthorizationUrl(Token requestToken) {
    	return service.getAuthorizationUrl(requestToken);
    }
    
    /**
     * Get the access token.
     * @return Token
     */
    public Token getAccessToken(Token requestToken, Verifier v) {
    	accessToken = service.getAccessToken(requestToken, v);
    	return accessToken;
    }
	
	/**
	 * Sets the token and the secret.
	 * 
	 * @param p_token
	 * @param p_secret
	 */
	public void setTokenWithSecret(String p_token, String p_secret) {
		inProgress = false;
		accessToken = new Token(p_token, p_secret);
		hasAccess = true;
	}
	
	/**
	 * getService
	 * 
	 * Creates the MijnTuin Service with OAuth authentication.
	 * 
	 * @return 
	 *   OAuthService
	 */
	public static OAuthService getService() {

		OAuthService service = new ServiceBuilder().provider(MijnTuinApi.class)
				.apiKey(CONSUMER_KEY)
				.apiSecret(CONSUMER_SECRET)
				.callback(CALLBACK)
				// getSignatureType should be QueryString (not the default
				// Header)
				.signatureType(SignatureType.QueryString).build();
		
		return service;
	}
	
	/**
	 * Returns whether the service is already connecting or not.
	 * @return
	 */
	public Boolean isInProgress() {
		return inProgress;
	}
	
	/**
	 * Calls the api for the given endpoint
	 * 
	 * @param endPoint
	 * @return
	 */
	public String call(String endPoint) {
		Log.i("Service", "Service get " + endPoint + " with access token: " + accessToken.getToken());
		
		if (hasAccess) {
			OAuthRequest request = new OAuthRequest(Verb.GET, endPoint);
			service.signRequest(accessToken, request);
			Response response = request.send();
			return response.getBody();
		}
		else {
			return "Problem while retrieving my garden";
		}
	}
}
