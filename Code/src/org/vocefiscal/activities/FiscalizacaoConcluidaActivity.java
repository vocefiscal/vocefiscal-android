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
import org.vocefiscal.twitter.TwitterLoginHandler;
import org.vocefiscal.twitter.TwitterSession;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;

public class FiscalizacaoConcluidaActivity extends AnalyticsActivity 
{
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
	private ImageButton twitterButton;
	
	private TwitterLoginHandler twitterLoginHandler;
	
	private Handler handler;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fiscalizacao_concluida);
		
		handler = new Handler();

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
		
		
		//--------------------------------------------------------------------------------------------------------
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


		//--------------------------------------------------------------------------------------------------------
		
		
		//Listener do botão Compartilhar no Facebook
		facebookLogin = (ImageButton) findViewById(R.id.btn_facebook);
		facebookLogin.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{	
				//Pega a sessão ativa
				Session session = Session.getActiveSession();
				if (!session.isOpened() && !session.isClosed()) 
				{
					session.openForRead(new Session.OpenRequest(FiscalizacaoConcluidaActivity.this).setCallback(statusCallback));
				} else
				{
					Session.openActiveSession(FiscalizacaoConcluidaActivity.this, true, statusCallback);					
				}
			}
		});
		
//------------------------------------------------------------------------------------------------------------------------
		
		twitterButton = (ImageButton) findViewById(R.id.twitterButton);
		twitterButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				final TwitterSession ts = TwitterSession.restore(getApplicationContext());
				if(ts!=null&ts.getToken()!=null&&ts.getTokensecret()!=null)
				{
					Thread t = new Thread() 
					{
						public void run() 
						{
							ConfigurationBuilder cb = new ConfigurationBuilder();
							cb.setOAuthConsumerKey(TwitterLoginHandler.consumerKey).setOAuthConsumerSecret(TwitterLoginHandler.consumerSecret).setOAuthAccessToken(ts.getToken()).setOAuthAccessTokenSecret(ts.getTokensecret());
							TwitterFactory tf = new TwitterFactory(cb.build());
							Twitter twitterPost = tf.getInstance();

							String tweet = "Eu fiscalizei a seção "+ secao +", na zona eleitoral " +  zonaEleitoral + ", no município de: " +  municipio + " http://www.vocefiscal.org/";
																			
							try 
							{
								twitterPost.updateStatus(tweet);
								
								handler.post(new Runnable() 
								{
									
									@Override
									public void run() 
									{
										Toast.makeText(FiscalizacaoConcluidaActivity.this,"Tweet feito com sucesso!",Toast.LENGTH_SHORT).show();
										
									}
								});
								
								
							} catch (TwitterException e) 
							{							
								handler.post(new Runnable() 
								{
									
									@Override
									public void run() 
									{
										Toast.makeText(FiscalizacaoConcluidaActivity.this,"Não foi possível twittar!",Toast.LENGTH_SHORT).show();
									}
								});
								
							}
						}
					};
					t.start();
					
					
				}else
				{
					final String callbackURL = "fiscalizacaoconcluidaactivitycallback:///"; 

					Thread t = new Thread() 
					{
						public void run() 
						{
							twitterLoginHandler = new TwitterLoginHandler(handler);
							twitterLoginHandler.startLogin(FiscalizacaoConcluidaActivity.this, callbackURL);
						}
					};
					t.start();	
				}
				

			}
		});

//-----------------------------------------------------------------------------------------------------------------------		
		
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
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) 
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
	 * Chama a sessão de Mapas da Activity
	 * @param view
	 */
	public void buttonMapaSecoes (View view)
	{
		Intent intent = new Intent(FiscalizacaoConcluidaActivity.this, MapsActivity.class);
		startActivity(intent);

	}

	//	/**
	//	 * Publica no mural do Facebook
	//	 */
	private void publishStory(Session session) 
	{
		Bundle postParams = new Bundle();
		postParams.putString("name", "Você Fiscal");

		// Receber os dados da eleição!!!
		postParams.putString("message", "Eu fiscalizei a seção: "+ this.secao +"\nNa zona eleitoral: " + this.zonaEleitoral + "\nNo município de: " + this.municipio);			
		postParams.putString("description", "Obrigado por contribuir com a democracia!");
		postParams.putString("link", "http://www.vocefiscal.org/");
		postParams.putString("picture", "http://imagizer.imageshack.us/v2/150x100q90/913/bAwPgx.png");

		Request.Callback callback= new Request.Callback() 
		{
			public void onCompleted(Response response) 
			{
				JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
				String postId = "Compartilhado com sucesso!";
				try 
				{
					postId = graphResponse.getString("Compartilhado com sucesso!");
				} catch (JSONException e) 
				{
				}
				FacebookRequestError error = response.getError();
				if (error != null) 
				{
					Toast.makeText(FiscalizacaoConcluidaActivity.this.getApplicationContext(),error.getErrorMessage(),Toast.LENGTH_SHORT).show();
				} else 
				{
					Toast.makeText(FiscalizacaoConcluidaActivity.this.getApplicationContext(),	postId,	Toast.LENGTH_LONG).show();
				}
			}
		};

		Request request = new Request(session, "me/feed", postParams, HttpMethod.POST, callback);

		RequestAsyncTask task = new RequestAsyncTask(request);
		task.execute();
	}


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
					session.requestNewPublishPermissions(newPermissionsRequest);
					return;
				}
				
				publishStory(session);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
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

	//------------------------------------------------------------------------------------------------

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
	//----------------------------------------------------------------------------------------------
	
	
	private void showToast(String msg) 
	{
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

	}

	// when user will click on twitte then first that will check that is
	// internet exist or not
	public boolean isNetworkAvailable() 
	{
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

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		Uri uri = getIntent().getData(); 
		if (uri != null)
		{ 
			String oauthVerifier = uri.getQueryParameter("oauth_verifier"); 
			if(oauthVerifier!=null)
				TwitterSession.restore(getApplicationContext()).saveRequest(getApplicationContext(), oauthVerifier);
		}
		
		if(twitterLoginHandler==null)
			twitterLoginHandler = new TwitterLoginHandler(handler);
		
		twitterLoginHandler.completeLogin(FiscalizacaoConcluidaActivity.this,secao,zonaEleitoral,municipio);
	}
	
	
}