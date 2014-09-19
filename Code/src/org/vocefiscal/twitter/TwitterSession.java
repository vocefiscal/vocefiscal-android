
package org.vocefiscal.twitter;

import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * A utility class for storing and retrieving Twitter session data.
 * 
 * @author Vitor
 */
public class TwitterSession 
{

    private static final String TOKEN = "twitter_access_token";
    private static final String TOKENSECRET = "twitter_access_tokenSecret";
    private static final String R_TOKEN = "r_twitter_access_token";
    private static final String R_TOKENSECRET = "r_twitter_access_tokenSecret";
    private static final String OAUTH_VERIFIER = "oauthverifier";
    
    private static final String KEY = "twitter-session";

    private static TwitterSession singleton;
    private static Twitter twitterLoggingIn;

    // The Twitter object
    private Twitter twitter;
   
    //oauth values
    private String token;
   
    private String tokensecret;
    
    private String rtoken;
    
    private String rtokensecret;
     
    private String oauthverifier;
    

    public TwitterSession() 
    {

    }

	/**
     * Stores the session data on disk.
     * 
     * @param context
     * @return
     */
    public boolean saveRequest(Context context, RequestToken requestToken, Twitter t) 
    {

    	twitter = t;
        Editor editor =  context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();

        rtoken = requestToken.getToken();
        rtokensecret =requestToken.getTokenSecret();
        editor.putString(R_TOKEN,rtoken);
        editor.putString(R_TOKENSECRET, rtokensecret);

        if (editor.commit()) 
        {
            singleton = this;
            return true;
        }
        return false;
    }

	/**
     * Stores the session data on disk.
     * 
     * @param context
     * @return
     */
    public boolean saveRequest(Context context, String oauthverifier)
    {

        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        this.oauthverifier = oauthverifier;
        editor.putString(OAUTH_VERIFIER, oauthverifier);
     
        if (editor.commit()) 
        {
            singleton = this;
            return true;
        }
        return false;
    }
    /**
     * Stores the session data on disk.
     * 
     * @param context
     * @return
     */
    public boolean save(Context context, AccessToken accessToken) 
    {

        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();

        this.token = accessToken.getToken();
        this.tokensecret = accessToken.getTokenSecret();
        editor.putString(TOKEN, token);
        editor.putString(TOKENSECRET, tokensecret);
        

        if (editor.commit()) 
        {
            singleton = this;
            return true;
        }
        return false;
    }
    /**
     * Loads the session data from disk.
     * 
     * @param context
     * @return
     */
    public static TwitterSession restore(Context context) 
    {
    	if (singleton != null) 
    	{
    		return singleton;
    	}

        SharedPreferences prefs = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);

        TwitterSession session = new TwitterSession();
        
        session.setToken(prefs.getString(TOKEN, null));
        session.setTokensecret(prefs.getString(TOKENSECRET, null));
        session.setRtoken(prefs.getString(R_TOKEN, null));
        session.setRtokensecret(prefs.getString(R_TOKENSECRET, null));
        session.setOauthverifier(prefs.getString(OAUTH_VERIFIER, null));
        
        
        singleton = session;
        return session;
    }

    /**
     * Clears the saved session data.
     * 
     * @param context
     */
    public static void clearSavedSession(Context context) 
    {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
        singleton = null;
    }

    /**
     * Freezes a Facebook object while it's waiting for an auth callback.
     */
    public static void waitForAuthCallback(Twitter t)
    {
        twitterLoggingIn = t;
    }

    /**
     * Returns a Facebook object that's been waiting for an auth callback.
     */
    public static Twitter wakeupForAuthCallback() 
    {
        Twitter twitter = twitterLoggingIn;
        twitterLoggingIn = null;
        return twitter;
    }

	public Twitter getTwitter() 
	{
		return twitter;
	}



	public void setTwitter(Twitter twitter)
	{
		this.twitter = twitter;
	}

	public String getToken() {
		return token;
	}



	public void setToken(String token) {
		this.token = token;
	}



	public String getTokensecret() {
		return tokensecret;
	}



	public void setTokensecret(String tokensecret) {
		this.tokensecret = tokensecret;
	}



	public String getRtoken() {
		return rtoken;
	}



	public void setRtoken(String rtoken) {
		this.rtoken = rtoken;
	}



	public String getRtokensecret() {
		return rtokensecret;
	}



	public void setRtokensecret(String rtokensecret) {
		this.rtokensecret = rtokensecret;
	}



	public String getOauthverifier() {
		return oauthverifier;
	}



	public void setOauthverifier(String oauthverifier) {
		this.oauthverifier = oauthverifier;
	}

}
