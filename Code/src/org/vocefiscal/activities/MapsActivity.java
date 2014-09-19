package org.vocefiscal.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.vocefiscal.R;
import org.vocefiscal.asynctasks.AsyncTask;
import org.vocefiscal.asynctasks.GetStateStatsAsyncTask;
import org.vocefiscal.asynctasks.GetStateStatsAsyncTask.OnGetStateStatsPostExecuteListener;
import org.vocefiscal.communications.CommunicationConstants;
import org.vocefiscal.location.LocationController;
import org.vocefiscal.location.LocationUtils;
import org.vocefiscal.models.StateStats;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends AnalyticsActivity  implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, OnGetStateStatsPostExecuteListener<Object>
{	
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private GoogleMap mapa;

	private LocationController locationController;

	private int backoffStats = 0;

	ArrayList<String> listaDeEstados;

	private TextView textoMarker;

	private Location mPreviousLocation;

	private Location mLocation;

	private Handler handler;

	Marker lastOpenned = null;

	private Runnable atualizarMapa;
	
	private HashMap<String, GetStateStatsAsyncTask> mapStateStatsAsyncTasks;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		handler = new Handler();
		
		mapStateStatsAsyncTasks = new HashMap<String, GetStateStatsAsyncTask>();

		atualizarMapa = new Runnable()
		{	
			@Override
			public void run() 
			{
				mPreviousLocation = mLocation;
				mLocation = locationController.getCurrentLocation();
				if(mLocation != null)
				{

					mapa.clear();

					//Compara a latitude e longitude da minha localização, em módulo, 
					//com as capitais dos estados se for menor, cria e preenche o Marker
					for(String estado : listaDeEstados)
					{
						try
						{
							if ( (Math.abs( (Math.abs(mLocation.getLatitude()) - Math.abs(getStateLocation(estado).latitude))) < 25) &&
									(Math.abs( (Math.abs(mLocation.getLongitude()) - Math.abs(getStateLocation(estado).longitude))) < 8))
							{
								GetStateStatsAsyncTask getStateStatsAsyncTask = new GetStateStatsAsyncTask(getApplicationContext(), MapsActivity.this, estado, 0);
								getStateStatsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
								
								mapStateStatsAsyncTasks.put(estado, getStateStatsAsyncTask);
							}
						}catch(Exception e)
						{

						}
					}

					if(mLocation!=null && mPreviousLocation!= null && mLocation.getAccuracy() >= mPreviousLocation.getAccuracy())
					{
						locationController.stop();
					}else 
					{
						handler.postDelayed(atualizarMapa, LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
					}

				}else
				{
					handler.postDelayed(atualizarMapa, LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
				}
			}
		};

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
			if(mapa==null)
			{
				Toast.makeText(getApplicationContext(), "Problemas para apresentar o mapa. Atualize a versão do Google Maps no cel e tente novamente depois.", 
						Toast.LENGTH_LONG).show();
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
		uiSettings.setRotateGesturesEnabled(false);
		uiSettings.setAllGesturesEnabled(false);
		uiSettings.setIndoorLevelPickerEnabled(false);
		uiSettings.setScrollGesturesEnabled(false);
		uiSettings.setTiltGesturesEnabled(false);
		uiSettings.setZoomGesturesEnabled(false);


		/*
		 * *************************************************
		 * Location
		 * *************************************************
		 */
		LocationClient mLocationClient = new LocationClient(this, this, this);		
		locationController = new LocationController(mLocationClient, mapa);

		criaListaDeEstados();

	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() 
	{
		super.onResume();

		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
		{
			Toast.makeText(getApplicationContext(), "Habilite o seu GPS para melhores resultados de posicionamento.", Toast.LENGTH_LONG).show();
		}
		handler.post(atualizarMapa);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() 
	{
		super.onPause();

		handler.removeCallbacks(atualizarMapa);
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
	
	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() 
	{
		locationController.stop();

		super.onStop();
	}
	
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() 
	{
		if(mapStateStatsAsyncTasks!=null)
		{
			Iterator<Map.Entry<String,GetStateStatsAsyncTask>> it =  mapStateStatsAsyncTasks.entrySet().iterator();
		    while (it.hasNext()) 
		    {
		    	Map.Entry<String,GetStateStatsAsyncTask> pairs = (Map.Entry<String,GetStateStatsAsyncTask>)it.next();
		    	GetStateStatsAsyncTask getStateStatsAsyncTask = pairs.getValue();
		    	if(getStateStatsAsyncTask!=null&&!getStateStatsAsyncTask.isCancelled())
		    		getStateStatsAsyncTask.cancel(true);
		    }
		}
		
		super.onDestroy();
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

	//----------------------------------------------------------------------------------------------------------------		

	/*
	 * Called by Location Services if the connection to the
	 * location client drops because of an error.
	 */
	@Override
	public void onDisconnected() 
	{
		locationController.stop();

	}

	//----------------------------------------------------------------------------------------------------------------	

	/**
	 * Ao ser clicado, retorna da Acvitity
	 * @param view
	 */
	public void voltar(View view)
	{
		finish();		
	}

	//----------------------------------------------------------------------------------------------------------------	

	//----------------------------------------------------------------------------------------------------------------	

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

	//----------------------------------------------------------------------------------------------------------------	

	//Cria os marcadores no mapa e adiciona as informações dentro dos Markers
	private void fillStateStats(String stateCode, int pollTapesCount) 
	{
		//Tira a opção do usuário clicar no Market e mudar a posição do mapa
		mapa.setOnMarkerClickListener(new OnMarkerClickListener() 
		{
			public boolean onMarkerClick(Marker marker) 
			{
				// Check if there is an open info window
				if (lastOpenned != null) 
				{
					// Close the info window
					((Marker) lastOpenned).hideInfoWindow();

					// Is the marker the same marker that was already open
					if (lastOpenned.equals(marker)) 
					{
						// Nullify the lastOpenned object
						lastOpenned = null;
						// Return so that the info window isn't openned again
						return true;
					} 
				}

				// Open the info window for the marker
				marker.showInfoWindow();
				// Re-assign the last openned such that we can close it later
				lastOpenned = marker;

				// Event was handled by our code do not launch default behaviour.
				return true;
			}
		});


		LatLng latLng = getStateLocation(stateCode);

		// create marker
		MarkerOptions marker = new MarkerOptions().position(latLng);
		marker.draggable(false);
		marker.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.indicador,String.valueOf(pollTapesCount))));
		marker.visible(true);

		//Adiciona os contadores de cada estado dentro do Marker
		textoMarker = (TextView) findViewById(R.id.text_view_polls_count);
		textoMarker.setText(String.valueOf(pollTapesCount));

		// adding marker
		mapa.addMarker(marker);

	}

	//----------------------------------------------------------------------------------------------------------------	

	//Adiciona as siglas dos estados num vetor de Strings
	private void criaListaDeEstados() 
	{

		listaDeEstados = new ArrayList<String>();

		listaDeEstados.add("AC");
		listaDeEstados.add("AL");
		listaDeEstados.add("AP");
		listaDeEstados.add("AM");
		listaDeEstados.add("BA");
		listaDeEstados.add("CE");
		listaDeEstados.add("DF");
		listaDeEstados.add("ES");
		listaDeEstados.add("GO");
		listaDeEstados.add("MA");
		listaDeEstados.add("MT");
		listaDeEstados.add("MS");
		listaDeEstados.add("MG");
		listaDeEstados.add("PA");
		listaDeEstados.add("PB");
		listaDeEstados.add("PR");
		listaDeEstados.add("PE");
		listaDeEstados.add("PI");
		listaDeEstados.add("RJ");
		listaDeEstados.add("RN");
		listaDeEstados.add("RS");
		listaDeEstados.add("RO");
		listaDeEstados.add("RR");
		listaDeEstados.add("SC");
		listaDeEstados.add("SP");
		listaDeEstados.add("SE");
		listaDeEstados.add("TO");

	}



	//----------------------------------------------------------------------------------------------------------------	

	//Preenche e retorna latitude e longitude de cada capital do estado
	private LatLng getStateLocation(String stateCode) 
	{
		LatLng position = null;

		if(stateCode.equalsIgnoreCase("AC"))
			position = new LatLng(-9.978299, -67.810529);

		else if(stateCode.equalsIgnoreCase("AL"))
			position = new LatLng(-9.660822, -35.70163);

		else if(stateCode.equalsIgnoreCase("AP"))
			position = new LatLng(0.038951, -51.057405); 

		else if(stateCode.equalsIgnoreCase("AM"))
			position = new LatLng(-3.134691, -60.023335); 
		else if(stateCode.equalsIgnoreCase("BA"))
			position = new LatLng(-13.014772, -38.488061); 

		else if(stateCode.equalsIgnoreCase("CE"))
			position = new LatLng(-3.723805, -38.589928);

		else if(stateCode.equalsIgnoreCase("DF"))
			position = new LatLng(-15.794087, -47.887905); 

		else if(stateCode.equalsIgnoreCase("ES"))
			position = new LatLng(-20.338374, -40.293957);

		else if(stateCode.equalsIgnoreCase("GO"))
			position = new LatLng(-16.67331, -49.255814); 

		else if(stateCode.equalsIgnoreCase("MA"))
			position = new LatLng(-2.531886, -44.297919); 

		else if(stateCode.equalsIgnoreCase("MT"))
			position = new LatLng(-15.569989, -56.073252); 

		else if(stateCode.equalsIgnoreCase("MS"))
			position = new LatLng(-20.45803, -54.615744); 

		else if(stateCode.equalsIgnoreCase("MG"))
			position = new LatLng(-19.937524 , -43.926453); 

		else if(stateCode.equalsIgnoreCase("PA"))
			position = new LatLng(-1.459845, -48.487826); 

		else if(stateCode.equalsIgnoreCase("PB"))
			position = new LatLng(-7.149382, -34.873385); 

		else if(stateCode.equalsIgnoreCase("PR"))
			position = new LatLng(-25.432956, -49.271848); 

		else if(stateCode.equalsIgnoreCase("PE"))
			position = new LatLng(-8.062762, -34.888942); 

		else if(stateCode.equalsIgnoreCase("PI"))
			position = new LatLng(-5.086342, -42.80527); 

		else if(stateCode.equalsIgnoreCase("RJ"))
			position = new LatLng(-22.876652, -43.227875); 

		else if(stateCode.equalsIgnoreCase("RN"))
			position = new LatLng(-5.750899, -35.252255); 

		else if(stateCode.equalsIgnoreCase("RS"))
			position = new LatLng(-30.030037, -51.22866);

		else if(stateCode.equalsIgnoreCase("RO"))
			position = new LatLng(-8.768892, -63.831446);

		else if(stateCode.equalsIgnoreCase("RR"))
			position = new LatLng(-60.670533, 2.816682); 

		else if(stateCode.equalsIgnoreCase("SC"))
			position = new LatLng(-27.587796, -48.547637); 

		else if(stateCode.equalsIgnoreCase("SP"))
			position = new LatLng(-23.567386, -46.570383); 

		else if(stateCode.equalsIgnoreCase("SE"))
			position = new LatLng(-10.907216, -37.048213); 

		else if(stateCode.equalsIgnoreCase("TO"))
			position = new LatLng(-10.163253, -48.351044); 


		return position;

	}

	//----------------------------------------------------------------------------------------------------------------	

	@Override
	public void finishedGetStateStatsComResultado(StateStats stateStats) 
	{
		backoffStats = 0;
		fillStateStats(stateStats.getStateCode(), stateStats.getPollTapesCount());
	}

	//----------------------------------------------------------------------------------------------------------------	

	@Override
	public void finishedGetStateStatsComError(int errorCode, String error, String stateCode) 
	{
		backoffStats++;

		GetStateStatsAsyncTask getStateStatsAsyncTask = new GetStateStatsAsyncTask(getApplicationContext(),this, stateCode,CommunicationConstants.WAIT_RETRY*backoffStats);
		getStateStatsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
		mapStateStatsAsyncTasks.put(stateCode, getStateStatsAsyncTask);

	}

	//----------------------------------------------------------------------------------------------------------------	

	private Bitmap writeTextOnDrawable(int drawableId, String text) 
	{
		Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

		Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setTypeface(tf);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(convertToPixels(getBaseContext(), 14));

		Rect textRect = new Rect();
		paint.getTextBounds(text, 0, text.length(), textRect);

		Canvas canvas = new Canvas(bm);

		//If the text is bigger than the canvas , reduce the font size
		if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
			paint.setTextSize(convertToPixels(getBaseContext(), 7));        //Scaling needs to be used for different dpi's

		//Calculate the positions
		int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

		//"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
		int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2) - 10) ;  

		canvas.drawText(text, xPos, yPos, paint);

		return  bm;
	}

	//----------------------------------------------------------------------------------------------------------------	

	private static int convertToPixels(Context context, int nDP)
	{
		final float conversionScale = context.getResources().getDisplayMetrics().density;

		return (int) ((nDP * conversionScale) + 0.5f) ;

	}
}
