package org.vocefiscal.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.vocefiscal.R;




import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FiscalizacaoConcluidaActivity extends AnalyticsActivity 
{
	// Chaves do Twitter
	public final String consumer_key = "oK5OfonM29VXA5dOfiVam0mLl";
	public final String secret_key = "TKLEgt3Zp5BHTq4NyMZKgH6GjHzfTBpNecVxy58cjdr07q0A8b";
	
	//public final String consumer_key = "trWwomp0b09ER2A8H1cQg";
	//public final String secret_key = "PAC3E3CtcPcTuPl9VpCuzY6eDD8hPZPwp6gRDCviLs";
	
	File casted_image;

	String string_img_url = null , string_msg = null;
	//TextView btn;
	public static final String SECAO = "secao";
	public static final String ZONA = "zona";
	public static final String MUNICIPIO = "municipio";

	//Variável de permissão do Facebook
	public static final String TAB_TO_SELECT = "tab_to_select";
	public static final int FISCALIZAR = 0;
	public static final int CONFERIR = 1;

	private String secao;
	private String zonaEleitoral;
	private String municipio;
	public Boolean isStatusChanged = false;

//	public static final String USER = "Fredsvv";

	//Facebook Login
	private Session.StatusCallback statusCallback;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private ImageButton facebookLogin;	
	private static final String TAG = "FiscalizacaoConcluidaActivity";

	//Twitter Variables
	ImageButton twitterButton;


	// Instance of Facebook Class
	//String FILENAME = "AndroidSSO_data";

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fiscalizacao_concluida);
	
		/*
		 * Captando a missão
		 */
		Intent intent = this.getIntent();
		if(intent!=null)
		{
			Bundle bundle = intent.getExtras();
			if(bundle!=null)
			{
				secao = bundle.getString(InformacoesFiscalizacaoActivity.SECAO);
				zonaEleitoral = bundle.getString(InformacoesFiscalizacaoActivity.ZONA);
				municipio = bundle.getString(InformacoesFiscalizacaoActivity.MUNICIPIO);

			}
		}

		//Twitter Button Click
//		try {
//
//			twitterButton = (ImageButton) findViewById(R.id.twitterButton);
//			twitterButton.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					try {
//						onClickTwitt();
//					} catch (TwitterException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			});
//		} catch (Exception e) {
//			// TODO: handle exception
//			runOnUiThread(new Runnable() {
//				public void run() {
//					showToast("View problem");
//				}
//			});
//
//		}

		//Listener do botão Compartilhar no Facebook
		facebookLogin = (ImageButton) findViewById(R.id.btn_facebook);
		facebookLogin.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) {
				//Pega a sessão ativa
				Session session = Session.getActiveSession();
				if (!session.isOpened() && !session.isClosed()) 
				{
					session.openForRead(new Session.OpenRequest(FiscalizacaoConcluidaActivity.this).setCallback(statusCallback));

				} else
				{
					//Session.openActiveSession(FiscalizacaoConcluidaActivity.this, true, statusCallback);
					publishStory();	

				}

			}
		});

		TextView secao = (TextView)findViewById(R.id.secaoEleitoral);
		secao.setText(this.secao);

		TextView zona = (TextView)findViewById(R.id.zonaEleitoral);
		zona.setText(this.zonaEleitoral);

		TextView municipio = (TextView)findViewById(R.id.municipio);
		municipio.setText(this.municipio);

		statusCallback = new SessionStatusCallback();

		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
		if (session == null) 
		{
			if (savedInstanceState != null) 
			{
				session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
			}
			else if (session == null) 
			{
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED) || session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) 
			{
				session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
			}
		}

	}

	/**
	 * Chamada quando o botão Fiscalizar é clicado
	 * @param view
	 */
	public void fiscalizar(View view) 
	{

		Intent intent = new Intent(FiscalizacaoConcluidaActivity.this,HomeActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(TAB_TO_SELECT, FISCALIZAR);
		intent.putExtras(bundle);
		startActivity(intent);

		finish();
	}

	/**
	 * Chamada quando o botão Conferir é clicado
	 * @param view
	 */
	public void conferir(View view) 
	{

		Intent intent = new Intent(FiscalizacaoConcluidaActivity.this,HomeActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(TAB_TO_SELECT, CONFERIR);
		intent.putExtras(bundle);
		startActivity(intent);

		finish();
	}

	/**
	 * Chamada quando o botão proximaSecao é clicado
	 * @param view
	 */
	public void proximaSecao(View view)
	{
		Intent intent = new Intent(FiscalizacaoConcluidaActivity.this,CameraActivity.class);
		startActivity(intent);

		finish();

	}

	/**
	 * Chamada quando o botão mapa é clicado
	 * @param view
	 */
	public void mapaSecoes(View view)
	{

	}

//	/**
//	 * Publica no mural do Facebook
//	 */
	private void publishStory() 
	{
		Session session = Session.getActiveSession();

		if (session != null){

			// Check for publish permissions    
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				Session.NewPermissionsRequest newPermissionsRequest = new Session
						.NewPermissionsRequest(this, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}

			Bundle postParams = new Bundle();
			postParams.putString("name", "Você Fiscal");

			// Receber os dados da eleição!!!
			postParams.putString("message", "Você fiscalizou a seção: "+ this.secao +"\nNa zona eleitoral: " + this.zonaEleitoral + "\nNo município de: " + this.municipio);
			//postParams.putString("caption", "Você fiscalizou a seção: "+ this.secao +"\nna zona eleitoral" + this.zonaEleitoral + "\nno município de" + this.municipio);
			postParams.putString("description", "Obrigado por contribruir com a democracia!");
			postParams.putString("link", "http://www.vocefiscal.org/");
			postParams.putString("picture", "http://imagizer.imageshack.us/v2/150x100q90/913/bAwPgx.png");

			Request.Callback callback= new Request.Callback() {
				public void onCompleted(Response response) {
					JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
					String postId = "Compartilhado com sucesso!";
					try {
						postId = graphResponse.getString("Compartilhado com sucesso!");
					} catch (JSONException e) {
						Log.i(TAG,
								"JSON error "+ e.getMessage());
					}
					FacebookRequestError error = response.getError();
					if (error != null) {
						Toast.makeText(FiscalizacaoConcluidaActivity.this.getApplicationContext(),error.getErrorMessage(),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(FiscalizacaoConcluidaActivity.this.getApplicationContext(), 
								postId,
								Toast.LENGTH_LONG).show();
					}
				}
			};

			Request request = new Request(session, "me/feed", postParams, 
					HttpMethod.POST, callback);

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}

	}
//
	private class SessionStatusCallback implements Session.StatusCallback
	{ 

		@Override
		public void call(Session session, SessionState state, Exception exception) 
		{
			if (session.isOpened()) 
			{							
				List<String> permissions = session.getPermissions();

				List<String> PERMISSIONS = Arrays.asList("publish_actions");

				if (!isSubsetOf(PERMISSIONS, permissions)) 
				{
					Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(FiscalizacaoConcluidaActivity.this, PERMISSIONS);
					session.requestNewReadPermissions(newPermissionsRequest);
					return;
				}
				
//				if(isStatusChanged == false)
//				{	
//					publishStory();
//					isStatusChanged = true;
//				}

			}


		}

	}

	private boolean isSubsetOf(Collection<String> subset,Collection<String> superset) 
	{
		for (String string : subset) 
		{
			if (!superset.contains(string)) 
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void onStart() 
	{
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}
//
	@Override
	public void onStop() 
	{   
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);        
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	public void voltar(View view)
	{
		finish();
	}


	/*
	 * MÉTODOS DO TWITTER 
	 */


//	// Here you can pass the string message & image path which you want to share
//	// in Twitter.
//	public void onClickTwitt() throws TwitterException {
//		if (isNetworkAvailable()) {
//			Twitt_Sharing twitt = new Twitt_Sharing(FiscalizacaoConcluidaActivity.this, consumer_key, secret_key);
//			//string_img_url = "http://imagizer.imageshack.us/v2/150x100q90/913/bAwPgx.png";
//			string_msg = "Você fiscalizou a seção: "+ this.secao +"\nNa zona eleitoral: " + this.zonaEleitoral + "\nNo município de: " + this.municipio;
//			
//			string_img_url = "http://www.bharatbpo.in/bbpo/images/AndroidLogo.jpg";
//			//string_msg = "TESTE";
//			
//			// here we have web url image so we have to make it as file to
//			// upload
//			String_to_File(string_img_url);
//			
//			// Now share both message & image to sharing activity
//			twitt.shareToTwitter(string_msg, casted_image);
//			//twitt.shareToTwitter(string_img_url, casted_image);
//
//		} else {
//			showToast("Sem conexão disponível!");
//		}
//	}

	private void showToast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

	}
//
//	// when user will click on twitte then first that will check that is
//	// internet exist or not
	public boolean isNetworkAvailable() {
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo [ ] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Chama a sessão de Mapas da Activity
	 * @param view
	 */
	public void buttonMapaSecoes (View view)
	{
		Intent intent = new Intent(FiscalizacaoConcluidaActivity.this, MapsActivity.class);
		startActivity(intent);
			
	}

	// this function will make your image to file
	public File String_to_File(String img_url) {

		try {
			File rootSdDirectory = Environment.getExternalStorageDirectory();

			casted_image = new File(rootSdDirectory, "attachment.jpg");
			if (casted_image.exists()) {
				casted_image.delete();
			}
			casted_image.createNewFile();

			FileOutputStream fos = new FileOutputStream(casted_image);

			URL url = new URL(img_url);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			InputStream in = connection.getInputStream();

			byte [ ] buffer = new byte [1024];
			int size = 0;
			while ((size = in.read(buffer)) > 0) {
				fos.write(buffer, 0, size);
			}
			fos.close();
			return casted_image;

		} catch (Exception e) {

			System.out.print(e);
			// e.printStackTrace();

		}
		return casted_image;
	}

}