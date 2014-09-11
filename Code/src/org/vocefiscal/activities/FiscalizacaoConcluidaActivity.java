package org.vocefiscal.activities;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.vocefiscal.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class FiscalizacaoConcluidaActivity extends Activity 
{
	// Replace your KEY here and Run ,
	public final String consumer_key = "trWwomp0b09ER2A8H1cQg";
	public final String secret_key = "PAC3E3CtcPcTuPl9VpCuzY6eDD8hPZPwp6gRDCviLs";
	File casted_image;

	String string_img_url = null , string_msg = null;
	TextView btn;
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

	public static final String USER = "user";

	//Facebook Login
	private Session.StatusCallback statusCallback;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private ImageButton facebookLogin;	
	private static final String TAG = "FiscalizacaoConcluidaActivity";


	// Instance of Facebook Class
	String FILENAME = "AndroidSSO_data";

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

		TextView secao = (TextView)findViewById(R.id.secaoEleitoral);
		secao.setText(this.secao);

		TextView zona = (TextView)findViewById(R.id.zonaEleitoral);
		zona.setText(this.zonaEleitoral);

		TextView municipio = (TextView)findViewById(R.id.municipio);
		municipio.setText(this.municipio);

		//Listener do botão Compartilhar no Facebook
		facebookLogin = (ImageButton) findViewById(R.id.btn_facebook);
		facebookLogin.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) {
				
			
				//Pega a sessão ativa
				Session session = Session.getActiveSession();
				if(session.isOpened())
				{
					publishStory();	
				}

			}
		});

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
	 * Chamada quando o botão mapa é clicado
	 * @param view
	 */
	public void mapaSecoes(View view)
	{

	}

	/**
	 * Publica no mural do Facebook
	 */
	private void publishStory() {
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
					String postId = null;
					try {
						postId = graphResponse.getString("id");
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Session.getActiveSession()
	        .onActivityResult(this, requestCode, resultCode, data);
	}
	public void voltar(View view)
	{
		finish();
	}

}