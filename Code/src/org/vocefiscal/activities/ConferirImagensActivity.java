/**
 * 
 */
package org.vocefiscal.activities;

import java.util.ArrayList;
import java.util.List;

import org.vocefiscal.R;
import org.vocefiscal.adapters.FotoAdapter;
import org.vocefiscal.asynctasks.AsyncTask;
import org.vocefiscal.asynctasks.SendEmailAsyncTask;
import org.vocefiscal.asynctasks.SendEmailAsyncTask.OnSentMailListener;
import org.vocefiscal.bitmaps.ImageCache.ImageCacheParams;
import org.vocefiscal.bitmaps.ImageFetcher;
import org.vocefiscal.dialogs.CustomDialogClass;
import org.vocefiscal.dialogs.CustomDialogClass.BtnsControl;
import org.vocefiscal.utils.ImageHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * @author andre
 *
 */
public class ConferirImagensActivity extends Activity  implements OnSentMailListener
{

	private ArrayList<String> picturePathList;

	private ImageFetcher imageFetcher;

	private int fotoWidth = -1;

	private int fotoHeight = -1;

	private static final float FOTO_SIZE_REF_WIDTH = 720;

	private static final float FOTO_SIZE_REF_HEIGHT = 1280;

	private Handler handler;

	private LinearLayout progressBarLayout;

	private LinearLayout progressLayout;

	private CustomDialogClass envio;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conferir_imagens);

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
			}
		}

		/* 
		 * ImageFetcher e Cache 
		 */
		ImageCacheParams cacheParams = new ImageCacheParams(this, ImageHandler.IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

		//The ImageFetcher takes care of loading images into our ImageView children asynchronously
		imageFetcher = new ImageFetcher(ImageFetcher.CARREGAR_DO_DISCO, getApplicationContext(), fotoWidth, fotoHeight);
		imageFetcher.setLoadingImage(R.drawable.loading_image);
		imageFetcher.addImageCache(cacheParams);

		/*
		 * Elementos de UI e comportamentos
		 */

		handler = new Handler();

		FotoAdapter fotoAdapter = new FotoAdapter(getApplicationContext(), imageFetcher);

		ListView listview = (ListView) findViewById(R.id.listview);
		listview.setAdapter(fotoAdapter);
		listview.setDivider(this.getResources().getDrawable(R.color.transparent));
		listview.setOnScrollListener(new AbsListView.OnScrollListener() 
		{
			@Override
			public void onScrollStateChanged(AbsListView absListView, int scrollState) 
			{
				// Pause fetcher to ensure smoother scrolling when flinging
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING)
				{
					imageFetcher.setPauseWork(true);
				} else 
				{
					imageFetcher.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
			{
			}
		});

		fotoAdapter.setResultItemList(picturePathList);
		fotoAdapter.notifyDataSetChanged();

		ImageView up_logo = (ImageView) findViewById(R.id.up_logo);
		up_logo.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				voltarParaCamera();
			}
		});

		ImageView ruim = (ImageView) findViewById(R.id.ruim);
		ruim.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{
				voltarParaCamera();
			}
		});

		ImageView enviar = (ImageView) findViewById(R.id.enviar);
		enviar.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				enviarPorEmail();	
			}
		});

		/*
		 * Envio de fotos por email - não estará presente na versão final, apenas em versões intermediarias para testar experiencia e resultados
		 */

		progressBarLayout = (LinearLayout) findViewById(R.id.progressbarlayout);
		progressLayout = (LinearLayout) findViewById(R.id.progresslayout);
		envio = new CustomDialogClass(ConferirImagensActivity.this, "Título", "Msg");

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

	protected void voltarParaCamera() 
	{
		Intent intent = new Intent(getApplicationContext(), CameraActivity.class);

		startActivity(intent);

		handler.postDelayed(new Runnable() 
		{

			@Override
			public void run() 
			{
				ConferirImagensActivity.this.finish();
			}
		}, 1000);		
	}

	private void enviarPorEmail()
	{
		if(picturePathList!=null&&picturePathList.size()>0)
		{	          	        
			/*
			 * Conteúdo
			 */
			StringBuilder sb = new StringBuilder();
			sb.append("Enviadas com o Você Fiscal Android.");
			sb.append("\n\n");
			sb.append("Debug-infos:");
			sb.append("\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")");
			sb.append("\n OS API Level: " + android.os.Build.VERSION.SDK);
			sb.append("\n Device: " + android.os.Build.DEVICE);
			sb.append("\n Model (and Product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")");


			String from = "vocefiscal@gmail.com";
			String[] to = new String[]{"dedecun@gmail.com","helder@gmail.com","dfaranha@gmail.com"}; 
			String body = sb.toString();
			String subject = "[Você Fiscal] - Fotos de teste da versão B (controle manual entre fotos)";
			List<String> attachments = new ArrayList<String>();
			attachments.addAll(picturePathList);   

			SendEmailAsyncTask sendEmailAsyncTask = new SendEmailAsyncTask(this,this, to, from, subject, body, attachments, progressBarLayout, progressLayout);
			sendEmailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		}else
		{
			emailFalhou(-1);
		}	
	}

	@Override
	public void finishedSendingEmail(Boolean result, int errorCode) 
	{
		if(result)
		{
			emailEnviadoCorretamente();
		}else
		{
			emailFalhou(errorCode);
		}	

	}

	public void doNothing(View view)
	{
		//do nothing
	}

	private void emailEnviadoCorretamente() 
	{
		BtnsControl btnsEnvio = new BtnsControl() 
		{

			@Override
			public void positiveBtnClicked() 
			{
				finish();
			}

			@Override
			public void negativeBtnClicked() 
			{
				finish();
			}
		};

		envio.setBtnsControl(btnsEnvio, "OK", null);
		envio.setTitulo("Sucesso");
		envio.setPergunta("Fotos do BU enviadas com sucesso!");
		envio.show();
		envio.negativeButtonGone();			
	}

	private void emailFalhou(int errorCode)
	{			
		String msg = "Não foi possível enviar as fotos.";
		if(errorCode == SendEmailAsyncTask.SEM_CONEXAO_COM_A_INTERNET)
		{
			msg = "Sem conexão com a internet";
		}

		BtnsControl btnsErrosEnvio = new BtnsControl() 
		{

			@Override
			public void positiveBtnClicked() 
			{
				enviarPorEmail();
			}

			@Override
			public void negativeBtnClicked() 
			{
				finish();
			}
		};


		envio.setBtnsControl(btnsErrosEnvio, "Tentar", "Cancelar");
		envio.setTitulo("Falhou");
		envio.setPergunta(msg);
		envio.show();				
	}

}
