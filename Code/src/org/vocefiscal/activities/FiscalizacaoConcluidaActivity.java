package org.vocefiscal.activities;

import java.io.File;

import org.vocefiscal.R;
import org.vocefiscal.activities.MainFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.view.View;
import android.widget.TextView;

public class FiscalizacaoConcluidaActivity extends FragmentActivity 
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
	private MainFragment mainFragment;

    // Instance of Facebook Class
    String FILENAME = "AndroidSSO_data";


//	private Session.StatusCallback callback = new Session.StatusCallback() {
//		@Override
//		public void call(Session session, SessionState state, Exception exception) {
//			onSessionStateChange(session, state, exception);
//		}
//	};

//	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
//		@Override
//		public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
//			Log.d("Olá Facebook", String.format("Error: %s", error.toString()));
//		}
//
//		@Override
//		public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
//			Log.d("Olá Facebook", "Successo!");
//		}
//	};

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState == null) {
	       // Add the fragment on initial activity setup
	        mainFragment = new MainFragment();
	        getSupportFragmentManager().beginTransaction().add(android.R.id.content, mainFragment).commit();
	    } else {
	        // Or set the fragment from restored state info
	        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
	    }
		
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
				
				//Toast.makeText(getBaseContext(), ("Você fiscalizou a seção: "+ secao +"\nna zona eleitoral").toString() + zonaEleitoral, Toast.LENGTH_LONG).show();
			}
		}

		TextView secao = (TextView)findViewById(R.id.secaoEleitoral);
		secao.setText(this.secao);

		TextView zona = (TextView)findViewById(R.id.zonaEleitoral);
		zona.setText(this.zonaEleitoral);

		TextView municipio = (TextView)findViewById(R.id.municipio);
		municipio.setText(this.municipio);
	
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
	 * Método de clique do botão "compartilhar" que realiza a postagem de status no facebook
	 * @param view
	 */
	public void compartilharFacebook(View view)
	{
		//loginToFacebook();
		//publishStory();
		//postToWall();
		//onClickPostStatusUpdate();
	}
	
	public String getSecao(){
		return this.secao;
	}
	
	public String getZonaEleitoral(){
		return this.zonaEleitoral;
	}
	
	public String getMunicipio(){
		return this.municipio;
	}



}