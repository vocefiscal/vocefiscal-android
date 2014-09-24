package org.vocefiscal.activities;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

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

	private ImageButton facebookLogin;	

	//Twitter Variables
	private ImageButton twitterButton;

	private Handler handler;	
	
	private CustomDialogClass envio;
	
	private UiLifecycleHelper uiHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fiscalizacao_concluida);
		
		uiHelper = new UiLifecycleHelper(this, null);
	    uiHelper.onCreate(savedInstanceState);

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
				if (FacebookDialog.canPresentShareDialog(getApplicationContext(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) 
				{
					FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(FiscalizacaoConcluidaActivity.this).setLink("http://www.vocefiscal.org")
							.setPicture("http://imagizer.imageshack.us/v2/150x100q90/913/bAwPgx.png").setName("Você Fiscal")
							.setDescription("Eu fiscalizei a seção: "+ FiscalizacaoConcluidaActivity.this.secao +"\nNa zona eleitoral: " + FiscalizacaoConcluidaActivity.this.zonaEleitoral + "\nNo município de: " + FiscalizacaoConcluidaActivity.this.municipio)
							.setCaption("Obrigado por contribuir com a democracia!").build();		
					
					uiHelper.trackPendingDialogCall(shareDialog.present());
				}else
				{
					Toast.makeText(FiscalizacaoConcluidaActivity.this,"Para compartilhar, é preciso ter o Facebook Android instalado.",Toast.LENGTH_SHORT).show();
				}				
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
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		uiHelper.onResume();

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



	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() 
	{		
		super.onPause();
		
		uiHelper.onPause();
	}
	
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() 
	{	
		super.onDestroy();
		
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		 uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() 
		 {
		        @Override
		        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) 
		        {
		        	Toast.makeText(FiscalizacaoConcluidaActivity.this.getApplicationContext(),"Erro ao compartilhar no Facebook!",Toast.LENGTH_SHORT).show();
		        }

		        @Override
		        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) 
		        {
		        	Toast.makeText(FiscalizacaoConcluidaActivity.this.getApplicationContext(),"Compartilhado com sucesso!",Toast.LENGTH_SHORT).show();
		        }
		    });
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
		if(Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB)
		{
			Intent intent = new Intent(FiscalizacaoConcluidaActivity.this, MapsActivity.class);
			startActivity(intent);
		}else
		{
			Toast.makeText(getApplicationContext(), "Desculpe, mas o mapa só é compatível com Android 3.0 ou superior.", Toast.LENGTH_SHORT).show();
		}
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
