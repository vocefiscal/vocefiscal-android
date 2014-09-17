/**
 * 
 */
package org.vocefiscal.location;

import java.util.ArrayList;

import org.vocefiscal.R;
import org.vocefiscal.activities.MapsActivity;
import org.vocefiscal.asynctasks.AsyncTask;
import org.vocefiscal.asynctasks.GetStateStatsAsyncTask;
import org.vocefiscal.asynctasks.GetStateStatsAsyncTask.OnGetStateStatsPostExecuteListener;
import org.vocefiscal.communications.CommunicationConstants;
import org.vocefiscal.models.StateStats;
import org.vocefiscal.models.enums.BrazilStateCodesEnum;

import android.content.Context;
import android.location.Location;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.MarkerOptionsCreator;

/**
 * @author andre
 *
 */
public class LocationController implements LocationListener
{	
	private LocationClient mLocationClient;

	private LocationRequest mLocationRequest;

	private Location currentLocation;

	private GoogleMap map;
	
	private Context contexto;
	
	
	/**
	 * @param context
	 */
	public LocationController(LocationClient mLocationClient, GoogleMap map, Context contexto, TextView textViewController) 
	{
		super();
		this.contexto = contexto;
		this.mLocationClient = mLocationClient;
		this.map = map;
		//textoMarker = textViewController;

		initLocationController();
	}

	private void initLocationController()
	{

		// Create a new global location parameters object
		mLocationRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		mLocationRequest.setInterval(60000);

		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

		// Set the interval ceiling to one minute
		mLocationRequest.setFastestInterval(60000);
	}

	public void start()
	{
		// Connect the location client.
		mLocationClient.connect();
	}

	public void stop()
	{
		// If the client is connected
		if (mLocationClient.isConnected()) 
		{
			stopPeriodicUpdates();
		}

		// Disconnecting the location client invalidates it.
		mLocationClient.disconnect();
	}

	public void onConnected()
	{
		// Get the current location
		currentLocation = mLocationClient.getLastLocation(); 
		centerMapOnLocationWithInitialZoom();
		//startPeriodicUpdates();
	}

	/**
	 * Report location updates to the UI.
	 *
	 * @param location The updated location.
	 */
	@Override
	public void onLocationChanged(Location location) 
	{
		currentLocation = location;
		centerMapOnLocation();
	}


	private void centerMapOnLocation() 
	{
		if(currentLocation!=null)
		{
			
			LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

			// create marker
			MarkerOptions marker = new MarkerOptions().position(latLng).title("Você"); 
			// RED color icon
			marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.indicador));
			// adding marker
			map.addMarker(marker);
		}   		
	}


	private void centerMapOnLocationWithInitialZoom() 
	{
		if(currentLocation!=null)
		{
			LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));	
		}    	
	}

	/**
	 * In response to a request to start updates, send a request
	 * to Location Services
	 */
	private void startPeriodicUpdates() 
	{

		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	/**
	 * In response to a request to stop updates, send a request to
	 * Location Services
	 */
	private void stopPeriodicUpdates() 
	{
		mLocationClient.removeLocationUpdates(this);
	}

	/**
	 * @return the currentLocation
	 */
	public Location getCurrentLocation() {
		return currentLocation;
	}



}
