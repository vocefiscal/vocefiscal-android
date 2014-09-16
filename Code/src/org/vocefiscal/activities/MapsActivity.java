package org.vocefiscal.activities;

import org.vocefiscal.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;


public class MapsActivity extends FragmentActivity  implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener
{

	// Global constants
	/*
	 * Define a request code to send to Google Play services
	 * This code is returned in Activity.onActivityResult
	 */
	private final static int
	CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;

	private Context mContext;
	
	private GoogleMap mapa;

	Location mCurrentLocation;
	LocationClient mLocationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		mLocationClient = new LocationClient(this, this, this);
		
		

		//mCurrentLocation = mLocationClient.getLastLocation();
	}


	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;
		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}
		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
		public void show(FragmentManager supportFragmentManager, String string) {
			// TODO Auto-generated method stub

		}
	}

	/*
	 * Handle results returned to the FragmentActivity
	 * by Google Play services
	 */
	@Override
	protected void onActivityResult(
			int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {

		case CONNECTION_FAILURE_RESOLUTION_REQUEST :
			/*
			 * If the result code is Activity.RESULT_OK, try
			 * to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK :
				/*
				 * Try the request again
				 */

				break;
			}

		}
	}

	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mapa == null) {
            // Try to obtain the map from the SupportMapFragment.
        	mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mapa != null) {
            	mapa.setMyLocationEnabled(true);
                mapa.setOnMyLocationButtonClickListener((OnMyLocationButtonClickListener) this);
            }
        }
    }
	
	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode =
				GooglePlayServicesUtil.
				isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates",
					"Google Play services is available.");
			// Continue
			return true;

			// Google Play services was not available for some reason.
			// resultCode holds the error code.
		} else {
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					resultCode,
					this,
					CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment =
						new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getSupportFragmentManager(),
						"Location Updates");
			}
			return false;
		}
	}

	/*
	 * Called by Location Services when the request to connect the
	 * client finishes successfully. At this point, you can
	 * request the current location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

	}

	/*
	 * Called by Location Services if the connection to the
	 * location client drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.maps, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void ErrorAlert(final Context context) {
		mContext = context;
	}

	//	public void showErrorDialog(final String title, final String message) {
	//	    AlertDialog aDialog = new AlertDialog.Builder(mContext).setMessage(message).setTitle(title).setNeutralButton("Close", new OnClickListener() {
	//	          public void onClick(final DialogInterface dialog,
	//	              final int which) {
	//	            //Prevent to finish activity, if user clicks about.
	//	            if (!title.equalsIgnoreCase("About") && !title.equalsIgnoreCase("Directory Error") && !title.equalsIgnoreCase("View")) {
	//	              ((Activity) mContext).finish();
	//	            }
	//	            
	//	          }
	//	        }).create();
	//	    aDialog.setOnKeyListener((OnKeyListener) this);
	//	    aDialog.show();
	//	  }

	void showErrorDialog(int code) {
		GooglePlayServicesUtil.getErrorDialog(code, this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
	}

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
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
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}

	/*
	 * Called by Location Services if the attempt to
	 * Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects.
		 * If the error has a resolution, try sending an Intent to
		 * start a Google Play services activity that can resolve
		 * error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the
			 * user with the error.
			 */
			showErrorDialog(connectionResult.getErrorCode());
		}

	}

	public Location getLocation(LocationClient locationClient){

		if(locationClient.getLastLocation() == null){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return getLocation(locationClient);
		}else{
			return locationClient.getLastLocation();
		}
	}

}
