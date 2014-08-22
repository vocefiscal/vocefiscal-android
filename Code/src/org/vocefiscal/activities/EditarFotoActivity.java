/**
 * 
 */
package org.vocefiscal.activities;

import org.vocefiscal.R;
import org.vocefiscal.bitmaps.ImageCache.ImageCacheParams;
import org.vocefiscal.bitmaps.ImageFetcher;
import org.vocefiscal.bitmaps.RecyclingImageView;
import org.vocefiscal.utils.ImageHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * @author andre
 *
 */
public class EditarFotoActivity extends Activity 
{

	private ImageFetcher imageFetcher;
	
	private int fotoWidth = -1;

	private int fotoHeight = -1;
	
	private static final float FOTO_SIZE_REF_WIDTH = 720;

	private static final float FOTO_SIZE_REF_HEIGHT = 1088;
	
	private String picturePath = null;	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editar_foto);

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
		
		Intent intent = this.getIntent();
		if(intent!=null)
		{
			Bundle bundle = intent.getExtras();
			if(bundle!=null)
			{
				picturePath = bundle.getString(CameraActivity.PICTURE_PREVIEW_PATH, null);
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
		 * Itens de UI e comportamentos
		 */

		ImageView up_logo = (ImageView) findViewById(R.id.up_logo);
		up_logo.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				setResult(RESULT_CANCELED);
				finish();

			}
		});
		
		ImageView excluir_foto = (ImageView) findViewById(R.id.excluir_foto);
		excluir_foto.setOnClickListener(new OnClickListener() 
		{		
			@Override
			public void onClick(View v) 
			{
				setResult(RESULT_OK);
				finish();
			}
		});
		
		ProgressBar progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
		
		RecyclingImageView preview_foto = (RecyclingImageView) findViewById(R.id.preview_foto);
		
		if(picturePath!=null)
			imageFetcher.loadImage(picturePath, preview_foto,progress_bar);
		else
			Toast.makeText(getApplicationContext(), "Nenhuma imagem para preview", Toast.LENGTH_SHORT).show();
				
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
