package org.vocefiscal.activities;

import org.vocefiscal.R;
import org.vocefiscal.location.LocationController;
import org.vocefiscal.location.LocationUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;


public class MapsActivity extends FragmentActivity  implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener
{	
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
    private GoogleMap mapa;
	
	private LocationController locationController;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		
		
		
		/*
		 * *************************************************************************************
		 *  Check device for Play Services APK. If check succeeds, proceed with GCM registration.
		 *  *************************************************************************************
		 */
		
		if (checkPlayServices()) 
		{
			/*
			 * *************************************************
			 * Map
			 * *************************************************
			 */

			// Get a handle to the Map Fragment
			mapa = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapa)).getMap();
			if(mapa!=null)
			{
				mapa.setMyLocationEnabled(true);
			}else
			{
				Toast.makeText(getApplicationContext(), "Problemas para apresentar o mapa. Atualize a versão do Google Maps no cel e tente novamente depois.", Toast.LENGTH_LONG).show();
				finish();		
			}
		} 
		
		//Configura o mapa para não dar zoom, sem gestos
		// e sem o botão de localização e compass
		UiSettings uiSettings = mapa.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setScrollGesturesEnabled(false);
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
	
		/*
		 * *************************************************
		 * Location
		 * *************************************************
		 */
		LocationClient mLocationClient = new LocationClient(this, this, this);		
		locationController = new LocationController(mLocationClient, mapa);
	}
	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() 
	{
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) 
		{
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) 
			{
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else 
			{
				finish();
			}
			return false;
		}
		return true;
	}
	
//----------------------------------------------------------------------------------------------------------------		


	/*
	 * Handle results returned to the FragmentActivity
	 * by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// Choose what to do based on the request code
				switch (requestCode) 
				{
				// If the request code matches the code sent in onConnectionFailed
				case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

					switch (resultCode) 
					{
					// If Google Play services resolved the problem
					case Activity.RESULT_OK:

						// Log the result
						Log.d(LocationUtils.APPTAG, "Resolved");
						break;

						// If any other result was returned by Google Play services
					default:
						// Log the result
						Log.d(LocationUtils.APPTAG, "No resolution");
						break;
					}

					// If any other request code was received
				default:
					// Report that this Activity received an unknown requestCode
					Log.d(LocationUtils.APPTAG, "Unkown requestcode: "+requestCode);
					break;
				}
	}
	
//----------------------------------------------------------------------------------------------------------------		

	/*
	 * Called by Location Services when the request to connect the
	 * client finishes successfully. At this point, you can
	 * request the current location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle)
	{
		locationController.onConnected();

	}
	
	/*
	 * Called by Location Services if the connection to the
	 * location client drops because of an error.
	 */
	@Override
	public void onDisconnected() 
	{
		locationController.stop();

	}
	
	/**
	 * Ao ser clicado, retorna da Acvitity
	 * @param view
	 */
	public void voltar(View view)
	{
		finish();		
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() 
	{
		locationController.stop();

		super.onStop();
	}

	/*
	 * Called by Location Services if the attempt to
	 * Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) 
	{
		/*
		 * Google Play services can resolve some errors it detects.
		 * If the error has a resolution, try sending an Intent to
		 * start a Google Play services activity that can resolve
		 * error.
		 */
		if (connectionResult.hasResolution()) 
		{
			try 
			{

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */

			} catch (IntentSender.SendIntentException e) 
			{

				// Log the error
				e.printStackTrace();
			}
		} else 
		{
			// If no resolution is available, display a dialog to the user with the error.
			Log.e(LocationUtils.APPTAG, String.valueOf(connectionResult.getErrorCode()));
		}

	}
	
	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() 
	{
		super.onStart();

		locationController.start();
	}
}
