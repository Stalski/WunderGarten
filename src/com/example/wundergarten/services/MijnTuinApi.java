package com.example.wundergarten.services;

import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verb;

public class MijnTuinApi extends TwitterApi {
		
  private static final String AUTHORIZE_URL = "http://www.mijntuin.org/oauth/authorize?oauth_token=%s";
  private static final String REQUEST_TOKEN_RESOURCE = "api.mijntuin.org/oauth/request_token";
  private static final String ACCESS_TOKEN_RESOURCE = "api.mijntuin.org/oauth/access_token";

  @Override
  public String getRequestTokenEndpoint() {
    return "http://" + REQUEST_TOKEN_RESOURCE;
  }
  
  @Override
  public String getAccessTokenEndpoint() {
    return "http://" + ACCESS_TOKEN_RESOURCE;
  }

  @Override
  public String getAuthorizationUrl(Token requestToken) {
	return String.format(AUTHORIZE_URL, requestToken.getToken());
  }

  /**
   * Returns the verb for the access token endpoint (defaults to POST)
   * 
   * @return access token endpoint verb
   */
  public Verb getAccessTokenVerb() {
    return Verb.GET;
  }
  
  /**
   * Returns the verb for the request token endpoint (defaults to POST)
   * 
   * @return request token endpoint verb
   */
  public Verb getRequestTokenVerb() {
    return Verb.GET;
  }
}
