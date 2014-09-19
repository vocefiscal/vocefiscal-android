package org.vocefiscal.activities;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.vocefiscal.R;
import org.vocefiscal.communications.CommunicationConstants;
import org.vocefiscal.dialogs.CustomDialogClass;
import org.vocefiscal.dialogs.CustomDialogClass.BtnsControl;
import org.vocefiscal.twitter.TwitterSession;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
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
	//Variável de permissão do Facebook
	public static final String TAB_TO_SELECT = "tab_to_select";
	public static final int FISCALIZAR = 0;
	public static final int CONFERIR = 1;
	public static final int CAMERA = 2;

	private String secao;
	private String zonaEleitoral;
	private String municipio;

	//Facebook Login
	private Session.StatusCallback statusCallback;
	private ImageButton facebookLogin;	

	//Twitter Variables
	private ImageButton twitterButton;

	private Handler handler;	
	
	private CustomDialogClass envio;

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

		SharedPreferences prefs = null;
		SharedPreferences.Editor editor = null;

		prefs = getSharedPreferences("vocefiscal", 0);
		if(prefs!=null)
			editor = prefs.edit();

		if(prefs!=null&&editor!=null)
		{	
			if(secao!=null)
				editor.putString(InformacoesFiscalizacaoActivity.SECAO, secao);
			else
				secao = prefs.getString(InformacoesFiscalizacaoActivity.SECAO, null);
			if(zonaEleitoral!=null)
				editor.putString(InformacoesFiscalizacaoActivity.ZONA, zonaEleitoral);
			else
				zonaEleitoral = prefs.getString(InformacoesFiscalizacaoActivity.ZONA, null);
			if(municipio!=null)
				editor.putString(InformacoesFiscalizacaoActivity.MUNICIPIO, municipio);
			else
				municipio = prefs.getString(InformacoesFiscalizacaoActivity.MUNICIPIO, null);

			editor.commit();
		}			

		handler = new Handler();				

		//Listener do botão Compartilhar no Facebook
		facebookLogin = (ImageButton) findViewById(R.id.btn_facebook);
		facebookLogin.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{							        
				//Pega a sessão ativa
				Session session = Session.getActiveSession();

				if (session!=null&&!session.isOpened() && !session.isClosed()) 
				{
					session.openForRead(new Session.OpenRequest(FiscalizacaoConcluidaActivity.this).setCallback(statusCallback));
				} else
				{
					Session.openActiveSession(FiscalizacaoConcluidaActivity.this, true, statusCallback);					
				}
			}
		});

		facebookLogin.setOnLongClickListener(new OnLongClickListener()
		{			
			@Override
			public boolean onLongClick(View v) 
			{
				Session session = Session.getActiveSession();

				if (session!=null&&!session.isClosed()) 
				{
					session.closeAndClearTokenInformation();
				}

				finish();
				return true;
			}
		});

		twitterButton = (ImageButton) findViewById(R.id.twitterButton);
		twitterButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				BtnsControl btnsControlTimeout = new BtnsControl() 
				{

					@Override
					public void positiveBtnClicked() 
					{
						final TwitterSession ts = TwitterSession.restore(getApplicationContext());
						if(ts!=null&ts.getToken()!=null&&ts.getTokensecret()!=null)
						{
							Thread t = new Thread() 
							{
								public void run() 
								{
									ConfigurationBuilder cb = new ConfigurationBuilder();
									cb.setOAuthConsumerKey(CommunicationConstants.TWITTER_API_KEY).setOAuthConsumerSecret(CommunicationConstants.TWITTER_API_SECRET).setOAuthAccessToken(ts.getToken()).setOAuthAccessTokenSecret(ts.getTokensecret());
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
										if(e!=null&&e.getStatusCode()!=403)
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
									startTwitterLogin(callbackURL);
								}
							};
							t.start();	
						}
					}

					@Override
					public void negativeBtnClicked() 
					{
						//do nothing
					}
				};
				
				String tweet = "Eu fiscalizei a seção "+ secao +", na zona eleitoral " +  zonaEleitoral + ", no município de: " +  municipio + " http://www.vocefiscal.org/";
				
				envio = new CustomDialogClass(FiscalizacaoConcluidaActivity.this, "Compartilhar no Twitter", "Será twittado no seu perfil - "+tweet);
				envio.setBtnsControl(btnsControlTimeout, "OK", "Cancelar");	
				if(envio!=null&&!envio.isShowing())
					envio.show();
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
		}
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
			{
				TwitterSession.restore(getApplicationContext()).saveRequest(getApplicationContext(), oauthVerifier);

				completeTwitterLogin();
			}				
		}				
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
		Intent intent = new Intent(FiscalizacaoConcluidaActivity.this,HomeActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(TAB_TO_SELECT, CAMERA);
		intent.putExtras(bundle);
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

	public void voltar(View view)
	{
		finish();
	}	

	public boolean startTwitterLogin(final String callbackURL)
	{		
		Twitter twitter = new TwitterFactory().getInstance(); 
		twitter.setOAuthConsumer (CommunicationConstants.TWITTER_API_KEY, CommunicationConstants.TWITTER_API_SECRET); 

		boolean ok = true;

		try 
		{
			RequestToken rToken = twitter.getOAuthRequestToken(callbackURL);


			TwitterSession twitterSession = new TwitterSession();

			twitterSession.saveRequest(FiscalizacaoConcluidaActivity.this, rToken,twitter);

			Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(rToken.getAuthenticationURL()+"&force_login=true"));
			startActivity(twitterIntent); 

			handler.postDelayed(new Runnable() 
			{				
				@Override
				public void run() 
				{
					FiscalizacaoConcluidaActivity.this.finish();					
				}
			}, 2000);
		} catch (TwitterException e) 
		{
			ok = false;
			if(e!=null&&e.getStatusCode()!=403)
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
		return ok;
	}

	public void completeTwitterLogin()
	{
		Thread t = new Thread()
		{
			public void run()
			{
				try 
				{
					final TwitterSession twitterSession = TwitterSession.restore(FiscalizacaoConcluidaActivity.this);

					if (twitterSession!=null && twitterSession.getOauthverifier() != null) 
					{ 
						//COMMON PART

						final Twitter twitter = new TwitterFactory().getInstance();				

						twitter.setOAuthConsumer (CommunicationConstants.TWITTER_API_KEY, CommunicationConstants.TWITTER_API_SECRET); 

						//retrieve request token
						RequestToken requestToken = new RequestToken(twitterSession.getRtoken(), twitterSession.getRtokensecret());

						final AccessToken at = twitter.getOAuthAccessToken(requestToken, twitterSession.getOauthverifier());

						twitterSession.setTwitter(twitter);

						twitterSession.save(FiscalizacaoConcluidaActivity.this, at);


						ConfigurationBuilder cb = new ConfigurationBuilder();
						cb.setOAuthConsumerKey(CommunicationConstants.TWITTER_API_KEY).setOAuthConsumerSecret(CommunicationConstants.TWITTER_API_SECRET).setOAuthAccessToken(at.getToken()).setOAuthAccessTokenSecret(at.getTokenSecret());
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
							if(e!=null&&e.getStatusCode()!=403)
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
					}
				} catch (TwitterException e) 
				{
					e.printStackTrace();
				}
			}
		};
		t.start();			
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() 
	{	
		super.onBackPressed();

		Intent intent = new Intent(FiscalizacaoConcluidaActivity.this,HomeActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(TAB_TO_SELECT, FISCALIZAR);
		intent.putExtras(bundle);
		startActivity(intent);

		finish();
	}
}
