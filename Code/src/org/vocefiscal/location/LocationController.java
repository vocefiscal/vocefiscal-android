/**
 * 
 */
package org.vocefiscal.location;



import android.location.Location;
import android.widget.TextView;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;


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
	
	
	/**
	 * @param context
	 */
	public LocationController(LocationClient mLocationClient, GoogleMap map) 
	{
		super();

		this.mLocationClient = mLocationClient;
		this.map = map;

		initLocationController();
	}

	private void initLocationController()
	{

		// Create a new global location parameters object
		mLocationRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		mLocationRequest.setInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

		// Set the interval ceiling to one minute
	
		mLocationRequest.setFastestInterval(LocationUtils.FAST_CEILING_IN_SECONDS);

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
		startPeriodicUpdates();
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
		centerMapOnLocationWithInitialZoom();
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
	public Location getCurrentLocation()
	{
		return currentLocation;
	}
}
