/**
 * 
 */
package org.vocefiscal.activities;

import java.util.ArrayList;

import org.vocefiscal.R;
import org.vocefiscal.adapters.FotoAdapter;
import org.vocefiscal.bitmaps.ImageCache.ImageCacheParams;
import org.vocefiscal.bitmaps.ImageFetcher;
import org.vocefiscal.bitmaps.ImageHandler;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author andre
 *
 */
public class ConferirImagensActivity extends AnalyticsActivity
{

	private ArrayList<String> picturePathList;
	
	private ArrayList<String> picture30PCPathList;
	
	private ImageFetcher imageFetcher;

	private int fotoWidth = -1;

	private int fotoHeight = -1;

	private static final float FOTO_SIZE_REF_WIDTH = 720;

	private static final float FOTO_SIZE_REF_HEIGHT = 1280;

	public static final String MODO_HISTORICO = "modo_historico";

	private Handler handler;
	
	private boolean isModoHistorico = false;

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
				picture30PCPathList = bundle.getStringArrayList(CameraActivity.PICTURE_30PC_PATH_LIST);
				isModoHistorico = bundle.getBoolean(MODO_HISTORICO,false);
			}
		}
		
		//pictureURLList = new ArrayList<String>();

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
		
		TextView titulo = (TextView)findViewById(R.id.titulo);
		if(isModoHistorico)
			titulo.setText("Confira as imagens desta fiscalização");
		
		RelativeLayout botoes = (RelativeLayout) findViewById(R.id.botoes);
		if(isModoHistorico)
			botoes.setVisibility(View.GONE);

		FotoAdapter fotoAdapter = new FotoAdapter(getApplicationContext(), imageFetcher);

		ListView listview = (ListView) findViewById(R.id.listview);
		listview.setAdapter(fotoAdapter);	
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
		if(isModoHistorico)
		{
			up_logo.setImageResource(R.drawable.selector_up_logo);
			up_logo.setOnClickListener(new OnClickListener() 
			{			
				@Override
				public void onClick(View v) 
				{
					if(isModoHistorico)
						finish();
				}
			});	
		}
			
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
				Intent intent = new Intent(getApplicationContext(), InformacoesFiscalizacaoActivity.class);

				Bundle bundle = new Bundle();
				bundle.putStringArrayList(CameraActivity.PICTURE_PATH_LIST, picturePathList);
				bundle.putStringArrayList(CameraActivity.PICTURE_30PC_PATH_LIST, picture30PCPathList);

				intent.putExtras(bundle);

				startActivity(intent);

				finish();					
			}
		});
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

		finish();	
	}
}
