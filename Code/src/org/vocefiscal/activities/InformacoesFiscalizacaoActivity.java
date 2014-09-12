/**
 * 
 */
package org.vocefiscal.activities;

import java.util.ArrayList;

import org.vocefiscal.R;
import org.vocefiscal.bitmaps.ImageCache.ImageCacheParams;
import org.vocefiscal.bitmaps.ImageFetcher;
import org.vocefiscal.bitmaps.ImageHandler;
import org.vocefiscal.bitmaps.RecyclingImageView;
import org.vocefiscal.database.VoceFiscalDatabase;
import org.vocefiscal.dialogs.CustomDialogClass;
import org.vocefiscal.dialogs.CustomDialogClass.BtnsControl;
import org.vocefiscal.models.Fiscalizacao;
import org.vocefiscal.models.enums.StatusEnvioEnum;
import org.vocefiscal.services.UploadManagerService;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author andre
 *
 */
public class InformacoesFiscalizacaoActivity extends Activity 
{
	private ArrayList<String> picturePathList;

	private ArrayList<String> picture30PCPathList;
	
	private ImageFetcher imageFetcher;

	private int fotoWidth = -1;

	private int fotoHeight = -1;

	private static final float FOTO_SIZE_REF_WIDTH = 720;

	private static final float FOTO_SIZE_REF_HEIGHT = 218;

	public static final String SECAO = "secao";

	public static final String ZONA = "zona";

	public static final String MUNICIPIO = "municipio";

	private Handler handler;
	
	private VoceFiscalDatabase voceFiscalDatabase;
	
	private CustomDialogClass envio;
	
	private Spinner municipio_spinner;
	
	private Spinner estado_spinner;
	
	private EditText zona_eleitoral_et;
	
	private EditText local_votacao_et;
	
	private EditText secao_eleitoral_et;
	
	private Fiscalizacao fiscalizacao;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_informacoes_fiscalizacao);
		
		voceFiscalDatabase = new VoceFiscalDatabase(this);
		
		fiscalizacao = new Fiscalizacao();
		
		/*
		 * Customização de tamanhos para as diferentes telas dos dispositivos Android
		 */
		Display display = getWindowManager().getDefaultDisplay();			
		int width = display.getWidth();
		int height = display.getHeight();
		float dw = width/720.0f;
		float dh = height/1184.0f;
		float deltaDisplay = Math.max(dw, dh);

		fotoWidth = (int) (FOTO_SIZE_REF_WIDTH*deltaDisplay);	
		fotoHeight = (int) (FOTO_SIZE_REF_HEIGHT*deltaDisplay);
		
		/*
		 * Captando a missão
		 */
		Intent intent = this.getIntent();
		if(intent!=null)
		{
			Bundle bundle = intent.getExtras();
			if(bundle!=null)
			{
				picturePathList = bundle.getStringArrayList(CameraActivity.PICTURE_PATH_LIST);
				picture30PCPathList = bundle.getStringArrayList(CameraActivity.PICTURE_30PC_PATH_LIST);
			}
		}
		
		/* 
		 * ImageFetcher e Cache 
		 */
		ImageCacheParams cacheParams = new ImageCacheParams(this, ImageHandler.IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

		//The ImageFetcher takes care of loading images into our ImageView children asynchronously
		imageFetcher = new ImageFetcher(ImageFetcher.CARREGAR_DO_DISCO, getApplicationContext(), fotoWidth, fotoHeight);
		imageFetcher.setLoadingImage(R.drawable.capa_conferir);
		imageFetcher.addImageCache(cacheParams);

		/*
		 * Elementos de UI e comportamentos
		 */

		handler = new Handler();
		
		ImageView up_logo = (ImageView) findViewById(R.id.up_logo);
		up_logo.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				voltarParaConferir();
			}
		});
		
		Typeface unisansheavy = Typeface.createFromAsset(this.getAssets(),"fonts/unisansheavy.otf");
		
		TextView dados_secao = (TextView) findViewById(R.id.dados_secao);
		dados_secao.setTypeface(unisansheavy);
		
		ProgressBar progress_bar_capa = (ProgressBar) findViewById(R.id.progress_bar_capa);
		
		RecyclingImageView capa = (RecyclingImageView) findViewById(R.id.capa);
		if(picturePathList!=null&&picturePathList.size()>0)
		{
			imageFetcher.loadImage(picturePathList.get(0), capa, progress_bar_capa);
		}
		
		BtnsControl btnsControlTimeout = new BtnsControl() 
		{

			@Override
			public void positiveBtnClicked() 
			{
				//Dados
				fiscalizacao.setPodeEnviarRedeDados(1);
				gravarNaBaseLocalESeguir();
			}

			@Override
			public void negativeBtnClicked() 
			{
				//Wi-Fi
				fiscalizacao.setPodeEnviarRedeDados(0);
				gravarNaBaseLocalESeguir();
			}
		};
		
		envio = new CustomDialogClass(InformacoesFiscalizacaoActivity.this, "Enviar a fiscalização", "Deseja enviar a fiscalização também usando rede de dados (3G) ou somente quando estiver em uma rede Wi-Fi?");
		envio.setBtnsControl(btnsControlTimeout, "Dados (3G)", "Wi-Fi");
		
		municipio_spinner = (Spinner) findViewById(R.id.municipio_et);
		ArrayAdapter<String> municipioAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		municipioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		municipio_spinner.setAdapter(municipioAdapter);
		
		estado_spinner = (Spinner) findViewById(R.id.estado_et);
		
		zona_eleitoral_et = (EditText) findViewById(R.id.zona_eleitoral_et);
		
		local_votacao_et = (EditText) findViewById(R.id.local_votacao_et);
		
		secao_eleitoral_et = (EditText) findViewById(R.id.secao_eleitoral_et);
		
		ImageView btn_enviar = (ImageView) findViewById(R.id.btn_enviar);
		btn_enviar.setOnClickListener(new OnClickListener() 
		{		
			@Override
			public void onClick(View v) 
			{
				String municipio = null;
				String estado = null;
				String zona_eleitoral = null;
				String local_votacao = null;
				String secao_eleitoral = null;
				
				if(estado_spinner!=null)
				{
					//estado = estado_et.getText().toString();	
					
					if(municipio_spinner!=null)
					{
						//municipio = municipio_et.getText().toString();
												
						if(zona_eleitoral_et!=null&&zona_eleitoral_et.getText()!=null&&zona_eleitoral_et.getText().length()>0)
						{
							zona_eleitoral = zona_eleitoral_et.getText().toString();	
							
							if(local_votacao_et!=null&&local_votacao_et.getText()!=null&&local_votacao_et.getText().length()>0)
							{
								local_votacao = local_votacao_et.getText().toString();	
								
								if(secao_eleitoral_et!=null&&secao_eleitoral_et.getText()!=null&&secao_eleitoral_et.getText().length()>0)
								{
									secao_eleitoral= secao_eleitoral_et.getText().toString();
									
									fiscalizacao.setMunicipio(municipio);
									fiscalizacao.setEstado(estado);
									fiscalizacao.setZonaEleitoral(zona_eleitoral);
									fiscalizacao.setLocalDaVotacao(local_votacao);
									fiscalizacao.setSecaoEleitoral(secao_eleitoral);
									fiscalizacao.setPicturePathList(picturePathList);
									fiscalizacao.setPicture30PCPathList(picture30PCPathList);
									fiscalizacao.setStatusDoEnvio(StatusEnvioEnum.ENVIANDO.ordinal());
									fiscalizacao.setData(System.currentTimeMillis());
									
									if(envio!=null&&!envio.isShowing())
										envio.show();
									
								}else
								{
									Toast.makeText(getApplicationContext(), "Seção eleitoral é obrigatória.", Toast.LENGTH_SHORT).show();
								}	
								
							}else
							{
								Toast.makeText(getApplicationContext(), "Local de votação é obrigatório.", Toast.LENGTH_SHORT).show();
							}	
							
						}else
						{
							Toast.makeText(getApplicationContext(), "Zona eleitoral é obrigatória.", Toast.LENGTH_SHORT).show();
						}	
						
					}else
					{
						Toast.makeText(getApplicationContext(), "Município é obrigatório.", Toast.LENGTH_SHORT).show();
					}	
					
				}else
				{
					Toast.makeText(getApplicationContext(), "Estado é obrigatório.", Toast.LENGTH_SHORT).show();
				}				
			}
		});
	}

	protected void gravarNaBaseLocalESeguir() 
	{
		if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
			voceFiscalDatabase.addFiscalizacao(fiscalizacao);
		
		Intent intentService = new Intent(getApplicationContext(), UploadManagerService.class);
		startService(intentService);
		
		Intent intent = new Intent(getApplicationContext(), FiscalizacaoConcluidaActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(SECAO, fiscalizacao.getSecaoEleitoral());
		bundle.putString(ZONA, fiscalizacao.getZonaEleitoral());
		bundle.putString(MUNICIPIO, fiscalizacao.getMunicipio());

		intent.putExtras(bundle);

		startActivity(intent);

		handler.postDelayed(new Runnable() 
		{
			@Override
			public void run() 
			{
				InformacoesFiscalizacaoActivity.this.finish();					
			}
		}, 1000);
		
	}

	protected void voltarParaConferir() 
	{
		Intent intent = new Intent(getApplicationContext(), ConferirImagensActivity.class);

		Bundle bundle = new Bundle();
		bundle.putStringArrayList(CameraActivity.PICTURE_PATH_LIST, picturePathList);
		bundle.putStringArrayList(CameraActivity.PICTURE_30PC_PATH_LIST, picture30PCPathList);

		intent.putExtras(bundle);

		startActivity(intent);

		handler.postDelayed(new Runnable() 
		{
			@Override
			public void run() 
			{
				InformacoesFiscalizacaoActivity.this.finish();					
			}
		}, 1000);
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() 
	{		
		super.onResume();
		
		if(imageFetcher!=null)
			imageFetcher.setExitTasksEarly(false);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() 
	{		
		super.onPause();
		
		if(imageFetcher!=null)
		{
			imageFetcher.setPauseWork(false);
			imageFetcher.setExitTasksEarly(true);
			imageFetcher.flushCache();
		}      
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() 
	{		
		super.onDestroy();
		
		if(imageFetcher!=null)
			imageFetcher.closeCache();
	}

}
