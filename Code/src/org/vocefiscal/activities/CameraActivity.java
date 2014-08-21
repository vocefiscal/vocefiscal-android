/**
 * 
 */
package org.vocefiscal.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.vocefiscal.R;
import org.vocefiscal.asynctasks.AsyncTask;
import org.vocefiscal.asynctasks.SendEmailAsyncTask;
import org.vocefiscal.asynctasks.SendEmailAsyncTask.OnSentMailListener;
import org.vocefiscal.bitmaps.ImageCache.ImageCacheParams;
import org.vocefiscal.bitmaps.ImageFetcher;
import org.vocefiscal.bitmaps.RecyclingImageView;
import org.vocefiscal.dialogs.CustomDialogClass;
import org.vocefiscal.dialogs.CustomDialogClass.BtnsControl;
import org.vocefiscal.models.enums.FlashModeEnum;
import org.vocefiscal.utils.ImageHandler;
import org.vocefiscal.views.CameraPreview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author andre
 *
 */
public class CameraActivity extends Activity implements OnSentMailListener
{
	private int pictureHeight = -1;

	private int pictureWidth = -1;

	private static final String TAG = "CameraActivity";

	private Camera mCamera;

	private CameraPreview mPreview;

	private PictureCallback mPicture;

	private AutoFocusCallback myAutoFocusCallback;

	private ArrayList<String> picturePathList = null;

	private int photoCount = 0;

	private TextView photo_counter;

	private Handler handler;

	private Runnable takePicture;

	private FrameLayout preview;

	private WakeLock wl;

	private static final int desiredPictureHeight = 720;

	private static final int desiredPictureWidth = 1088;

	private static final float FOTO_SIZE_REF_30PC_WIDTH = 720;

	private static final float FOTO_SIZE_REF_30PC_HEIGHT = 100;

	private ImageFetcher imageFetcherFoto30PC;

	private RecyclingImageView trinta_por_cento;

	private int foto30PCwidth = -1;

	private int foto30PCheight = -1;
	
	private int desiredPictureHeightAdjusted = -1;

	private int desiredPictureWidthAdjusted = -1;

	private SoundPool spool;

	private int soundID=-1;

	private boolean canPlaySound = false;

	private LinearLayout progressBarLayout;

	private LinearLayout progressLayout;

	private CustomDialogClass envio;
	
	private ImageView photo_trigger;
	
	private TextView photo_concluido;
	
	private ImageView flash_status;
	
	private int flashStatus = FlashModeEnum.AUTO.ordinal();

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		//Typeface unisansthin = Typeface.createFromAsset(this.getAssets(),"fonts/unisansthin.otf");
		Typeface unisansheavy = Typeface.createFromAsset(this.getAssets(),"fonts/unisansheavy.otf");

		/*
		 * Customização de tamanhos para as diferentes telas dos dispositivos Android
		 */
		Display display = getWindowManager().getDefaultDisplay();			
		int width = display.getWidth();
		int height = display.getHeight();
		float dw = width/720.0f;
		float dh = height/1184.0f;
		float deltaDisplay = Math.max(dw, dh);

		foto30PCwidth = (int) (FOTO_SIZE_REF_30PC_WIDTH*deltaDisplay);	
		foto30PCheight = (int) (FOTO_SIZE_REF_30PC_HEIGHT*deltaDisplay);
		
		desiredPictureHeightAdjusted = (int) (desiredPictureHeight*deltaDisplay);	
		desiredPictureWidthAdjusted = (int) (desiredPictureWidth*deltaDisplay);	
//		
//		Log.i("CameraActivity", "foto30PCwidth: "+String.valueOf(foto30PCwidth));
//		Log.i("CameraActivity", "foto30PCheight: "+String.valueOf(foto30PCheight));
//		Log.i("CameraActivity", "desiredPictureHeightAdjusted: "+String.valueOf(desiredPictureHeightAdjusted));
//		Log.i("CameraActivity", "desiredPictureWidthAdjusted: "+String.valueOf(desiredPictureWidthAdjusted));

		/* 
		 * ImageFetcher e Cache 
		 */
		ImageCacheParams cacheParams = new ImageCacheParams(this, ImageHandler.IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

		//The ImageFetcher takes care of loading images into our ImageView children asynchronously
		imageFetcherFoto30PC = new ImageFetcher(ImageFetcher.CARREGAR_DO_DISCO, getApplicationContext(), foto30PCwidth, foto30PCheight);
		//mImageFetcher.setLoadingImage(R.drawable.foto_grupo);		
		imageFetcherFoto30PC.addImageCache(cacheParams);

		handler = new Handler();

		picturePathList = new ArrayList<String>();

		takePicture = new Runnable() 
		{

			@Override
			public void run() 
			{				
				// get an image from the camera
				try
				{
					mCamera.autoFocus(myAutoFocusCallback);
					photo_trigger.setVisibility(View.GONE);
					photo_concluido.setVisibility(View.GONE);	
				}catch(Exception e)
				{
					Log.i(TAG, e.getMessage());
				}						
			}
		};

		ImageView up_logo = (ImageView) findViewById(R.id.up_logo);
		up_logo.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{
				finish();				
			}
		});

		trinta_por_cento = (RecyclingImageView) findViewById(R.id.trinta_por_cento);
		trinta_por_cento.setVisibility(View.GONE);
		setAlpha(trinta_por_cento, 0.5f);
		
		photo_concluido =  (TextView) findViewById(R.id.photo_concluido);
		photo_concluido.setTypeface(unisansheavy);
		photo_concluido.setVisibility(View.GONE);
		photo_concluido.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				handler.removeCallbacks(takePicture);
				trinta_por_cento.setVisibility(View.GONE);
				enviarPorEmail();				
			}
		});
		
		flash_status = (ImageView) findViewById(R.id.flash_status);
		flash_status.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				flashStatus = FlashModeEnum.nextFlashMode(flashStatus);												
				
				Camera.Parameters params = mCamera.getParameters();				
				params.setFlashMode(FlashModeEnum.getFlashMode(flashStatus));				
				mCamera.setParameters(params);
				
				flash_status.setImageResource(FlashModeEnum.getImageResource(flashStatus));
			}
		});

		//TextView up_text = (TextView) findViewById(R.id.up_text);			

		photo_counter = (TextView) findViewById(R.id.photo_counter);
		photo_counter.setText(String.valueOf(photoCount));
		photo_concluido.setTypeface(unisansheavy);

		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{				
				mCamera.autoFocus(new AutoFocusCallback() 
				{					
					@Override
					public void onAutoFocus(boolean success, Camera camera) 
					{
						// do nothing here					
					}
				});				
			}
		});

		progressBarLayout = (LinearLayout) findViewById(R.id.progressbarlayout);
		progressLayout = (LinearLayout) findViewById(R.id.progresslayout);

		photo_trigger = (ImageView) findViewById(R.id.photo_trigger);
		photo_trigger.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{												
				handler.post(takePicture);	
			}
		});

		myAutoFocusCallback = new AutoFocusCallback()
		{

			@Override
			public void onAutoFocus(boolean arg0, Camera arg1) 
			{
				mCamera.takePicture(null, null, mPicture);
				handler.post(new Runnable() 
				{

					@Override
					public void run() 
					{
						if(soundID!=-1)
						{
							if(canPlaySound)
							{
								spool.play(soundID, 100, 100, 1, 0, 1.0f);
							}else
							{
								handler.postDelayed(this, 100);
							}			
						}			
					}
				});	
				photoCount++;				
				photo_counter.setText(String.valueOf(photoCount));				
			}
		};

		mPicture = new PictureCallback() 
		{

			@Override
			public void onPictureTaken(byte[] data, Camera camera) 
			{										
				File pictureFile = getOutputMediaFile(false);

				if (pictureFile == null)
				{
					Log.d(TAG, "Error creating media file, check storage permissions");
					return;
				}								

				try 
				{
					Bitmap bMap = ImageHandler.decodeByteArrayToBitmap(data,desiredPictureWidthAdjusted, desiredPictureHeightAdjusted);

					int orientation = 0;
					if(bMap.getHeight() < bMap.getWidth())
					{
						orientation = 90;
					} else 
					{
						orientation = 0;
					}

					if (orientation != 0) 
					{
						Matrix matrix = new Matrix();
						matrix.postRotate(orientation);
						bMap = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(),
								bMap.getHeight(), matrix, true);
					} else
						bMap = Bitmap.createScaledBitmap(bMap, bMap.getWidth(),
								bMap.getHeight(), true);


					FileOutputStream out = new FileOutputStream(pictureFile);
					bMap.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.close();

					if (bMap != null) 
					{
						bMap = ImageHandler.cropBitmapLastThird(bMap,desiredPictureWidthAdjusted,foto30PCheight);						
						File lastThirdPicture = getOutputMediaFile(true);
						FileOutputStream outLastThirdPicture = new FileOutputStream(lastThirdPicture);
						bMap.compress(Bitmap.CompressFormat.JPEG, 100, outLastThirdPicture);
						outLastThirdPicture.close();

						bMap.recycle();
						bMap = null;
						picturePathList.add(pictureFile.getAbsolutePath());
						
						photo_trigger.setVisibility(View.VISIBLE);
						photo_concluido.setVisibility(View.VISIBLE);						
						trinta_por_cento.setVisibility(View.VISIBLE);
						imageFetcherFoto30PC.loadImage(lastThirdPicture.getAbsolutePath(), trinta_por_cento);	
						mPreview.surfaceChanged(null, 0, 0, 0);
						
					}

				} catch (FileNotFoundException e) 
				{
					Log.d(TAG, "File not found: " + e.getMessage());
				} catch (IOException e)
				{
					Log.d(TAG, "Error accessing file: " + e.getMessage());
				}				
			}			
		};  

		/*
		 * ************************************************
		 * Power management
		 * ************************************************
		 */

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WakeLock");

		setupSound();

		envio = new CustomDialogClass(CameraActivity.this, "Título", "Msg");
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

	/** Create a File for saving an image or video */
	private File getOutputMediaFile(boolean isLastThird)
	{
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		String filePath = null;

		if(isLastThird)
		{
			filePath = "IMG_LT"+ timeStamp + ".jpg";
		}else
		{
			filePath = "IMG_"+ timeStamp + ".jpg";
		}

		File mediaFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filePath);		

		return mediaFile;
	}

	@Override
	protected void onResume() 
	{		
		super.onResume();

		if(imageFetcherFoto30PC!=null)
			imageFetcherFoto30PC.setExitTasksEarly(false);

		if(wl!=null && !wl.isHeld())
			wl.acquire();	

		mCamera = getCameraInstance();

		if (mCamera != null)
		{
			mCamera.setDisplayOrientation(90);

			Camera.Parameters params = mCamera.getParameters();
			List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
			List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
			
			Camera.Size bestPictureSize = getOptimalCameraSize(pictureSizes, desiredPictureWidthAdjusted, desiredPictureHeightAdjusted);
			pictureWidth = bestPictureSize.width;
			pictureHeight = bestPictureSize.height;
			
			Camera.Size bestPreviewSize = getOptimalCameraSize(previewSizes, desiredPictureWidthAdjusted, desiredPictureHeightAdjusted);
							
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			params.setFlashMode(FlashModeEnum.getFlashMode(flashStatus));
			params.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
			params.setPictureSize(pictureWidth, pictureHeight);
			params.setJpegQuality(100);	
			mCamera.setParameters(params);
			
//			Log.e("CameraActivity", "pictureWidth: "+String.valueOf(pictureWidth));
//			Log.e("CameraActivity", "pictureHeight: "+String.valueOf(pictureHeight));
//			
//			Log.e("CameraActivity", "previewWidth: "+String.valueOf(bestPreviewSize.width));
//			Log.e("CameraActivity", "previewHeight: "+String.valueOf(bestPreviewSize.height));

			// Create our Preview view and set it as the content of our activity.
			mPreview = new CameraPreview(this, mCamera);			
			
			preview.addView(mPreview);
		}
	}
	
	private Camera.Size getOptimalCameraSize(List<Camera.Size> sizes, int w, int h) 
	{
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio=(double)h / w;

		if (sizes == null) return null;

		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		for (Camera.Size size : sizes) 
		{
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.height - targetHeight) < minDiff) 
			{
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		if (optimalSize == null) 
		{
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes) 
			{
				if (Math.abs(size.height - targetHeight) < minDiff) 
				{
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() 
	{		
		super.onPause();

		if(imageFetcherFoto30PC!=null)
		{
			imageFetcherFoto30PC.setPauseWork(false);
			imageFetcherFoto30PC.setExitTasksEarly(true);
			imageFetcherFoto30PC.flushCache();
		}      

		if(wl!=null && wl.isHeld())
			wl.release();

		releaseCamera();
	}

	@Override
	protected void onDestroy() 
	{		
		super.onDestroy();		

		if(imageFetcherFoto30PC!=null)
			imageFetcherFoto30PC.closeCache();
	}

	/** A safe way to get an instance of the Camera object. */
	public Camera getCameraInstance()
	{
		Camera c = null;
		try 
		{
			c = Camera.open(); // attempt to get a Camera instance
		}
		catch (Exception e)
		{
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	private void releaseCamera()
	{
		if (mCamera != null)
		{
			mCamera.release();        // release the camera for other applications
			mCamera = null;
		}
	}

	private void setupSound() 
	{		
		spool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		spool.setOnLoadCompleteListener(new OnLoadCompleteListener() 
		{

			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) 
			{
				if(status==0)
				{
					if(sampleId==soundID)
						canPlaySound = true;
				}				
			}
		});


		soundID = spool.load(getApplicationContext(), R.raw.one_click, 1);
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

	@SuppressLint("NewApi")
	public static void setAlpha(View view, float alpha)
	{
		if (Build.VERSION.SDK_INT < 11)
		{
			final AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
			animation.setDuration(0);
			animation.setFillAfter(true);
			view.startAnimation(animation);
		}
		else view.setAlpha(alpha);
	}
}
