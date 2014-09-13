package org.vocefiscal.activities;

import org.vocefiscal.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;


public class SplashScreenActivity extends AnalyticsActivity 
{
	/** Duration of wait **/
	private final int SPLASH_DISPLAY_LENGTH = 2500;

	private Handler handler;

	private Runnable goToHome;

	@Override
	public void onCreate(Bundle bundle) 
	{
		super.onCreate(bundle);
		setContentView(R.layout.activity_splash_screen);

		/* New Handler to start the Menu-Activity 
		 * and close this Splash-Screen after some seconds.*/
		handler = new Handler();

		goToHome = new Runnable() 
		{

			@Override
			public void run() 
			{
				SharedPreferences prefs = getSharedPreferences("vocefiscal", 0);
				if (prefs.getBoolean("dontshowagain", false)) 
				{ 
					Intent mainIntent = new Intent(SplashScreenActivity.this,HomeActivity.class);
					SplashScreenActivity.this.startActivity(mainIntent);
					SplashScreenActivity.this.finish();			
				}else
				{
					SharedPreferences.Editor editor = prefs.edit();
					
					editor.putBoolean("dontshowagain", true);
					editor.commit();

					Intent mainIntent = new Intent(SplashScreenActivity.this,TourActivity.class);
					SplashScreenActivity.this.startActivity(mainIntent);
					SplashScreenActivity.this.finish();	
				}
			}
		};

		handler.postDelayed(goToHome, SPLASH_DISPLAY_LENGTH);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() 
	{		
		super.onBackPressed();

		handler.removeCallbacks(goToHome);

		finish();
	}
}
