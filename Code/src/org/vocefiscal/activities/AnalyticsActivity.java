/**
 * 
 */
package org.vocefiscal.activities;

import android.app.Activity;

import com.flurry.android.FlurryAgent;

/**
 * @author andre
 *
 */
public class AnalyticsActivity extends Activity 
{

	public static final String FLURRY_API_KEY = "62JC8GQQ38H3S66Q5W68";

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() 
	{
		super.onStart();
		FlurryAgent.onStartSession(this, FLURRY_API_KEY);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() 
	{
		super.onStop();
		FlurryAgent.onEndSession(this);
	}	
}
