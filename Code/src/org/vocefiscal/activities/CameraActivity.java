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
import org.vocefiscal.bitmaps.ImageCache.ImageCacheParams;
import org.vocefiscal.bitmaps.ImageFetcher;
import org.vocefiscal.bitmaps.ImageHandler;
import org.vocefiscal.bitmaps.RecyclingImageView;
import org.vocefiscal.views.CameraPreview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
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
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author andre
 *
 */
public class CameraActivity extends AnalyticsActivity
{
	private Camera mCamera;

	private CameraPreview mPreview;

	private PictureCallback mPicture;

	private AutoFocusCallback myAutoFocusCallback;

	private ArrayList<String> picturePathList = null;

	private ArrayList<String> picture30PCPathList = null;

	private int photoCount = 0;

	private TextView photo_counter;

	private Handler handler;

	private Runnable takePicture;

	private FrameLayout preview;

	private WakeLock wl;

	private static final float FOTO_SIZE_REF_30PC_HEIGHT = 100;

	public static final String PICTURE_PREVIEW_PATH = "picture_preview";

	protected static final int PICTURE_PREVIEW_REQUEST_CODE = 1000;

	public static final String PICTURE_PATH_LIST = "picture_path_list";

	public static final String PICTURE_30PC_PATH_LIST = "picture_30pc_path_list";

	private ImageFetcher imageFetcherFoto30PC;

	private RecyclingImageView trinta_por_cento;

	private int foto30PCwidth = -1;

	private int foto30PCheight = -1;

	private int desiredPictureHeightAdjusted = 900;

	private int desiredPictureWidthAdjusted = 1200;
	
	private int screenWidthForPicture = -1;

	private int screenHeightForPicture = -1;

	private SoundPool spool;

	private int soundID=-1;

	private boolean canPlaySound = false;	

	private ImageView photo_trigger;

	private TextView photo_concluido;

	private ImageView flash_status;

	private AnimationDrawable animationDrawable;

	private RecyclingImageView animateImageView;

	private RelativeLayout linha_vermelha_foto_anterior;	

	private ImageView linha_vermelha_foto_anterior_baixo;	

	private ImageView editar_foto_anterior;

	private TextView photo_counter_next;

	private ProgressBar progress_bar_foto_preview;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		/*
		 * Fonte UniSans Heavy
		 */		
		Typeface unisansheavy = Typeface.createFromAsset(this.getAssets(),"fonts/unisansheavy.otf");

		/*
		 * Customização de tamanhos para as diferentes telas dos dispositivos Android
		 */
		Display display = getWindowManager().getDefaultDisplay();	
		int width = display.getWidth();
		int height = display.getHeight();
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float density = metrics.density;

		foto30PCwidth = width;
		foto30PCheight = (int) (FOTO_SIZE_REF_30PC_HEIGHT*density);

		screenWidthForPicture = width;
		screenHeightForPicture = (int) (height - 48*density);
		//		
		//		Log.i("CameraActivity", "foto30PCwidth: "+String.valueOf(foto30PCwidth));
		//		Log.i("CameraActivity", "foto30PCheight: "+String.valueOf(foto30PCheight));
		//		Log.i("CameraActivity", "desiredPictureHeightAdjusted: "+String.valueOf(desiredPictureHeightAdjusted));
		//		Log.i("CameraActivity", "desiredPictureWidthAdjusted: "+String.valueOf(desiredPictureWidthAdjusted));
		
		desiredPictureHeightAdjusted = width;
		desiredPictureWidthAdjusted = height;

		/*
		 * Animação foto camera loader
		 */
		animateImageView = (RecyclingImageView) findViewById(R.id.animacao_camera);		
		animateImageView.setBackgroundResource(R.drawable.animacao_camera);
		animationDrawable = (AnimationDrawable) animateImageView.getBackground();

		/* 
		 * ImageFetcher e Cache 
		 */
		ImageCacheParams cacheParams = new ImageCacheParams(this, ImageHandler.IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

		//The ImageFetcher takes care of loading images into our ImageView children asynchronously
		imageFetcherFoto30PC = new ImageFetcher(ImageFetcher.CARREGAR_DO_DISCO, getApplicationContext(), foto30PCwidth, foto30PCheight);	
		imageFetcherFoto30PC.addImageCache(cacheParams);

		/*
		 * Elementos dinâmicos, itens de UI e seus comportamentos
		 */

		handler = new Handler();

		picturePathList = new ArrayList<String>();

		picture30PCPathList = new ArrayList<String>();

		takePicture = new Runnable() 
		{

			@Override
			public void run() 
			{				
				// get an image from the camera
				try
				{	
//					PackageManager packageManager = CameraActivity.this.getPackageManager();
//					if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) 
//					{
//						mCamera.autoFocus(myAutoFocusCallback);		
//					}else
//					{
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

						//animacao da camera
						animationDrawable.setVisible(false, false);
						animateImageView.setVisibility(View.GONE);
//					}
				}catch(Exception e)
				{
					//Log.i(TAG, e.getMessage());
				}						
			}
		};

		ImageView up_logo = (ImageView) findViewById(R.id.up_logo);
		up_logo.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{
				CameraActivity.this.finish();				
			}
		});		

		trinta_por_cento = (RecyclingImageView) findViewById(R.id.trinta_por_cento);
		trinta_por_cento.setVisibility(View.GONE);
		setAlpha(trinta_por_cento, 0.5f);
		trinta_por_cento.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(getApplicationContext(), EditarFotoActivity.class);

				Bundle bundle = new Bundle();
				bundle.putString(PICTURE_PREVIEW_PATH, picturePathList.get(picturePathList.size()-1));

				intent.putExtras(bundle);

				CameraActivity.this.startActivityForResult(intent, PICTURE_PREVIEW_REQUEST_CODE);
			}
		});


		photo_concluido =  (TextView) findViewById(R.id.photo_concluido);
		photo_concluido.setTypeface(unisansheavy);
		photo_concluido.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{
				handler.removeCallbacks(takePicture);
				trinta_por_cento.setVisibility(View.GONE);

				Intent intent = new Intent(getApplicationContext(), ConferirImagensActivity.class);

				Bundle bundle = new Bundle();
				bundle.putStringArrayList(PICTURE_PATH_LIST, picturePathList);
				bundle.putStringArrayList(PICTURE_30PC_PATH_LIST, picture30PCPathList);

				intent.putExtras(bundle);

				CameraActivity.this.startActivity(intent);

				CameraActivity.this.finish();

			}
		});

		flash_status = (ImageView) findViewById(R.id.flash_status);
		PackageManager packageManager = CameraActivity.this.getPackageManager();
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) 
		{
			flash_status.setVisibility(View.VISIBLE);
			flash_status.setOnClickListener(new OnClickListener() 
			{

				@Override
				public void onClick(View v) 
				{
					Camera.Parameters params = mCamera.getParameters();	

					List<String> supportedFlashModes = params.getSupportedFlashModes();
					if(supportedFlashModes!=null&&supportedFlashModes.size()>0)
					{
						int currentFlashModeIndex = -1;
						String currentFlashMode = params.getFlashMode();
						for(int i=0;i<supportedFlashModes.size();i++)
						{
							String supportedFlashMode = supportedFlashModes.get(i);
							if(supportedFlashMode.equalsIgnoreCase(currentFlashMode))
							{
								currentFlashModeIndex = i;
								break;
							}
						}

						currentFlashModeIndex++;
						String nextSupportedFlashMode = supportedFlashModes.get(currentFlashModeIndex%supportedFlashModes.size());
						params.setFlashMode(nextSupportedFlashMode);						
						mCamera.setParameters(params);
						flash_status.setImageResource(getImageResource(nextSupportedFlashMode));
					}
				}
			});	
		}else
		{
			flash_status.setVisibility(View.INVISIBLE);
		}


		photo_counter = (TextView) findViewById(R.id.photo_counter);		

		photo_counter_next = (TextView) findViewById(R.id.photo_counter_next );		

		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{	
				preview.setEnabled(false);

				PackageManager packageManager = CameraActivity.this.getPackageManager();
				if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) 
				{
					animationDrawable.setVisible(true, true); 
					animateImageView.setVisibility(View.VISIBLE);
					
					mCamera.autoFocus(new AutoFocusCallback() 
					{					
						@Override
						public void onAutoFocus(boolean success, Camera camera) 
						{
							animationDrawable.setVisible(false, false);
							animateImageView.setVisibility(View.GONE);
							preview.setEnabled(true);				
						}
					});	
				}else
				{
					preview.setEnabled(true);		
				}
			}
		});

		photo_trigger = (ImageView) findViewById(R.id.photo_trigger);
		photo_trigger.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{				
				//animacao de camera 
				animationDrawable.setVisible(true, true); 
				animateImageView.setVisibility(View.VISIBLE);

				preview.setEnabled(false);
				photo_trigger.setVisibility(View.GONE);
				photo_concluido.setVisibility(View.GONE);
				editar_foto_anterior.setVisibility(View.GONE);
				handler.post(takePicture);	
			}
		});

		myAutoFocusCallback = new AutoFocusCallback()
		{

			@Override
			public void onAutoFocus(boolean arg0, Camera arg1) 
			{
//				mCamera.takePicture(null, null, mPicture);
//				handler.post(new Runnable() 
//				{
//
//					@Override
//					public void run() 
//					{
//						if(soundID!=-1)
//						{
//							if(canPlaySound)
//							{
//								spool.play(soundID, 100, 100, 1, 0, 1.0f);
//							}else
//							{
//								handler.postDelayed(this, 100);
//							}			
//						}			
//					}
//				});	

				//animacao da camera
				animationDrawable.setVisible(false, false);
				animateImageView.setVisibility(View.GONE);								
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
					Toast.makeText(CameraActivity.this, "Erro salvando a foto, verifique permissões de gravação de dados em mídia externa e tente novamente.",Toast.LENGTH_SHORT ).show();
					CameraActivity.this.finish();
				}								

				try 
				{										
					Bitmap bMap = ImageHandler.decodeByteArrayToBitmap(data,desiredPictureWidthAdjusted, desiredPictureHeightAdjusted);
					
					if(bMap==null)
					{
						Toast.makeText(CameraActivity.this, "Erro ao tirar a foto. Por favor, tente novamente.",Toast.LENGTH_SHORT ).show();
						CameraActivity.this.finish();
					}

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
						bMap = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(),	bMap.getHeight(), matrix, true);
					} else
						bMap = Bitmap.createScaledBitmap(bMap, bMap.getWidth(),	bMap.getHeight(), true);


					FileOutputStream out = new FileOutputStream(pictureFile);
					bMap.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.close();

					if (bMap != null) 
					{
						try
						{
							ImageHandler.insertImage(getContentResolver(), bMap, "Você Fiscal", "BU parte "+String.valueOf(photoCount));	
						}catch(Exception e)
						{
							
						}						
						
						bMap = ImageHandler.cropBitmapLastThird(bMap,screenHeightForPicture,foto30PCheight,screenWidthForPicture);						
						File lastThirdPicture = getOutputMediaFile(true);
						FileOutputStream outLastThirdPicture = new FileOutputStream(lastThirdPicture);
						bMap.compress(Bitmap.CompressFormat.JPEG, 100, outLastThirdPicture);
						outLastThirdPicture.close();

						bMap.recycle();
						bMap = null;

						picturePathList.add(pictureFile.getAbsolutePath());
						picture30PCPathList.add(lastThirdPicture.getAbsolutePath());

						preview.setEnabled(true);
						photo_trigger.setVisibility(View.VISIBLE);
						photo_concluido.setVisibility(View.VISIBLE);						
						trinta_por_cento.setVisibility(View.VISIBLE);
						linha_vermelha_foto_anterior.setVisibility(View.VISIBLE);						
						linha_vermelha_foto_anterior_baixo.setVisibility(View.VISIBLE);						
						editar_foto_anterior.setVisibility(View.VISIBLE);
						photoCount++;				
						photo_counter.setText(String.valueOf(photoCount));
						photo_counter_next.setText(String.valueOf(photoCount+1));

						imageFetcherFoto30PC.loadImage(lastThirdPicture.getAbsolutePath(), trinta_por_cento,progress_bar_foto_preview);	

						mPreview.surfaceChanged(null, 0, 0, 0);
					}

				} catch (FileNotFoundException e) 
				{
					Toast.makeText(CameraActivity.this, "Erro salvando a foto, verifique permissões de gravação de dados em mídia externa e tente novamente.",Toast.LENGTH_SHORT ).show();
					CameraActivity.this.finish();
				} catch (IOException e)
				{
					Toast.makeText(CameraActivity.this, "Erro salvando a foto, verifique permissões de gravação de dados em mídia externa e tente novamente.",Toast.LENGTH_SHORT ).show();
					CameraActivity.this.finish();
				}				
			}			
		};  

		linha_vermelha_foto_anterior = (RelativeLayout) findViewById(R.id.linha_vermelha_foto_anterior);

		linha_vermelha_foto_anterior_baixo = (ImageView) findViewById(R.id.linha_vermelha_foto_anterior_baixo);	

		editar_foto_anterior = (ImageView) findViewById(R.id.editar_foto_anterior);
		editar_foto_anterior.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(getApplicationContext(), EditarFotoActivity.class);

				Bundle bundle = new Bundle();
				bundle.putString(PICTURE_PREVIEW_PATH, picturePathList.get(picturePathList.size()-1));

				intent.putExtras(bundle);

				CameraActivity.this.startActivityForResult(intent, PICTURE_PREVIEW_REQUEST_CODE);
			}
		});

		progress_bar_foto_preview = (ProgressBar) findViewById(R.id.progress_bar_foto_preview);

		/*
		 * ************************************************
		 * Power management
		 * ************************************************
		 */

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WakeLock");

		/*
		 * Som do click de foto
		 */

		setupSound();
	}

	/** Create a File for saving an image or video */
	private File getOutputMediaFile(boolean isLastThird)
	{
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		String filePath = null;

		File mediaFile = null;

		File storeagePath = Environment.getExternalStorageDirectory();
		File picturePublicPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		storeagePath.mkdirs();
		picturePublicPath.mkdirs();

		if(isLastThird)
		{
			filePath = "IMG_LT"+ timeStamp + ".jpg";
			mediaFile = new File(storeagePath, filePath);	
		}else
		{
			filePath = "IMG_"+ timeStamp + ".jpg";
			mediaFile = new File(picturePublicPath, filePath);	
		}			

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

		preview.removeAllViews();

		mCamera = getCameraInstance();

		if (mCamera != null)
		{
			//setCameraDisplayOrientation(this, mCamera);
			
			mCamera.setDisplayOrientation(90);

			Camera.Parameters params = mCamera.getParameters();
			List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
			List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();

			Camera.Size bestPictureSize = getOptimalCameraSize(pictureSizes, desiredPictureWidthAdjusted, desiredPictureHeightAdjusted);
			
			Camera.Size bestPreviewSize = getOptimalCameraSize(previewSizes, desiredPictureWidthAdjusted, desiredPictureHeightAdjusted);
			
			if(bestPictureSize == null || bestPreviewSize == null)
			{
				Toast.makeText(CameraActivity.this, "Não foi possível encontrar uma resolução adequada na sua câmera.",Toast.LENGTH_LONG ).show();
				CameraActivity.this.finish();
			}

			PackageManager packageManager = this.getPackageManager();
//			if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) 
//			{
//				List<String> supportedFocusModes = params.getSupportedFocusModes();
//				boolean focusModeContinuosSupported = false;
//				if(supportedFocusModes!=null)
//				{
//					for(String supportedFocusMode : supportedFocusModes)
//					{
//						if(supportedFocusMode.equalsIgnoreCase(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
//						{
//							focusModeContinuosSupported = true;
//							break;
//						}
//					}
//				}
//				if(focusModeContinuosSupported)
//					params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);    
//			}
			if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) 
			{
				List<String> supportedFlashModes = params.getSupportedFlashModes();
				if(supportedFlashModes!=null&&supportedFlashModes.size()>0)
				{
					String supportedFlashMode = supportedFlashModes.get(0);
					flash_status.setImageResource(getImageResource(supportedFlashMode));
					params.setFlashMode(supportedFlashMode);
				}											
			}			
			params.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
			params.setPictureSize(bestPictureSize.width, bestPictureSize.height);
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
						
			handler.postDelayed(new Runnable() 
			{
				
				@Override
				public void run() 
				{
					PackageManager packageManager = CameraActivity.this.getPackageManager();
					if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) 
					{
						animationDrawable.setVisible(true, true); 
						animateImageView.setVisibility(View.VISIBLE);
						
						mCamera.autoFocus(myAutoFocusCallback);		
					}				
				}
			}, 1000);
			
		}
	}

//	private static void setCameraDisplayOrientation(Activity activity,Camera camera) 
//	{
//		CameraInfo info = new CameraInfo();
//
//		int cameraId = -1;
//		int numberOfCameras = Camera.getNumberOfCameras();
//		for (int i = 0; i < numberOfCameras; i++) 
//		{
//			Camera.getCameraInfo(i, info);
//			if (info.facing == CameraInfo.CAMERA_FACING_BACK) 
//			{
//				cameraId = i;
//				break;
//			}
//		}	     
//		Camera.getCameraInfo(cameraId, info);
//		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//		int degrees = 0;
//		switch (rotation) 
//		{
//		case Surface.ROTATION_0: degrees = 0; break;
//		case Surface.ROTATION_90: degrees = 90; break;
//		case Surface.ROTATION_180: degrees = 180; break;
//		case Surface.ROTATION_270: degrees = 270; break;
//		}
//
//		int result;
//		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) 
//		{
//			result = (info.orientation + degrees) % 360;
//			result = (360 - result) % 360;  // compensate the mirror
//		} else 
//		{  // back-facing
//			result = (info.orientation - degrees + 360) % 360;
//		}
//		camera.setDisplayOrientation(result);
//	}

	private Camera.Size getOptimalCameraSize(List<Camera.Size> sizes, int w, int h) 
	{
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio=(double)w / h;

		if (sizes == null) return null;

		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetWidth = w;

		for (Camera.Size size : sizes) 
		{
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.width - targetWidth) < minDiff  && (size.width > size.height)) 
			{
				optimalSize = size;
				minDiff = Math.abs(size.width - targetWidth);
			}
		}

		if (optimalSize == null) 
		{
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes) 
			{
				if (Math.abs(size.width - targetWidth) < minDiff  && (size.width > size.height)) 
				{
					optimalSize = size;
					minDiff = Math.abs(size.width - targetWidth);
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
			Toast.makeText(getApplicationContext(), "Não foi possível ter acesso à câmera do celular.", Toast.LENGTH_SHORT).show();
			setResult(RESULT_CANCELED);
			
			finish();
		}
		return c; // returns null if camera is unavailable
	}

	private void releaseCamera()
	{

		if (mCamera != null)
		{
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			try 
			{
				mCamera.setPreviewDisplay(null);
			} catch (IOException e) 
			{
			
			}
			mCamera.lock();
			mCamera.release();        // release the camera for other applications
			mCamera = null;
			mPreview = null;
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

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{		
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode==PICTURE_PREVIEW_REQUEST_CODE)
		{
			if(resultCode==RESULT_OK)
			{
				picturePathList.remove(picturePathList.size()-1);
				picture30PCPathList.remove(picture30PCPathList.size()-1);								

				photoCount--;
				photo_counter.setText(String.valueOf(photoCount));
				photo_counter_next.setText(String.valueOf(photoCount+1));
				if(photoCount==0)
				{
					photo_concluido.setVisibility(View.GONE);						
					trinta_por_cento.setVisibility(View.GONE);
					linha_vermelha_foto_anterior.setVisibility(View.GONE);						
					linha_vermelha_foto_anterior_baixo.setVisibility(View.GONE);						
					editar_foto_anterior.setVisibility(View.GONE);
				}else
				{
					imageFetcherFoto30PC.loadImage(picture30PCPathList.get(picture30PCPathList.size()-1), trinta_por_cento,progress_bar_foto_preview);
				}
			}
		}
	}

	public static int getImageResource(String flashStatus) 
	{
		int imageResource = R.drawable.flash_auto;

		if(flashStatus.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_AUTO))
		{
			imageResource = R.drawable.flash_auto;
		}else if(flashStatus.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_OFF))
		{
			imageResource = R.drawable.flash_off;
		}else if(flashStatus.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_ON))
		{
			imageResource = R.drawable.flash_on;
		}else if(flashStatus.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_TORCH))
		{
			imageResource = R.drawable.flash_tocha;
		}else if(flashStatus.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_RED_EYE))
		{
			imageResource = R.drawable.flash_red_eye;
		}

		return imageResource;
	}
}
