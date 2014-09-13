package org.vocefiscal.bitmaps;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;

public class ImageHandler 
{
	public static final String IMAGE_CACHE_DIR ="vocefiscal_cache";
	
	public static String nomeDaMidia(File file)
	{
		String nomeDaMidia = null;

		if(file!=null)
		{
			/*
			 * Ler o arquivo em bytes para poder
			 */
			int size = (int) file.length();
			byte[] bytes = new byte[size];
			try 
			{
				BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
				buf.read(bytes, 0, bytes.length);
				buf.close();
				
				nomeDaMidia = MD5_Hash(bytes);
				
			} catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}

		return(nomeDaMidia);
	}
	
	public static String MD5_Hash(byte[] bytes) 
	{
		MessageDigest m = null;

		try 
		{
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
		}

		m.update(bytes,0,bytes.length);
		String hash = new BigInteger(1, m.digest()).toString(16);
		return hash;
	}
	
	public static Bitmap cropBitmapLastThird(Bitmap bmp,int height,int cutHeight, int screenWidth)
	{	    
	    int pictureWidth = bmp.getWidth();
	    int pictureHeight = bmp.getHeight();
	    
//	    Log.e("ImageHandler", "width: "+String.valueOf(width));
//	    Log.e("ImageHandler", "height: "+String.valueOf(height));
//	    Log.e("ImageHandler", "pictureHeight: "+String.valueOf(pictureHeight));
//	    Log.e("ImageHandler", "cutHeight: "+String.valueOf(cutHeight));
	    
	    int cutWidth = (int) ((pictureWidth-screenWidth)/2.0f);
	    if(cutWidth<0)
	    	cutWidth=0;
	    
	    float dh = pictureHeight/(float) height;
		
	    int beginCutHeight = (int) (pictureHeight-1.33333*cutHeight*dh);
	    
	    int beginCutWidth = 0+cutWidth;
	    
//	    Log.e("ImageHandler", "beginCut: "+String.valueOf(beginCut));
	    
	    int heightSpan = cutHeight;
	    int widthSpan = screenWidth;
	    
	    /*
	     * Respeitando os limites da foto tirada na hora do corte (resolução da foto != da resolução da tela e do preview!)
	     */
	    if((beginCutHeight+ heightSpan) > pictureHeight)
	    	heightSpan = pictureHeight - beginCutHeight;
	    
	    if((beginCutWidth+widthSpan)>pictureWidth)
	    	widthSpan = pictureWidth - beginCutWidth;
	    
	    Bitmap bmOverlay = Bitmap.createBitmap(bmp, beginCutWidth, beginCutHeight, widthSpan, heightSpan);

	    return bmOverlay;
	}
	
	public static byte[] bitmapToByteArray(Bitmap bmp)
	{
		if(bmp!=null)
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress( Bitmap.CompressFormat.JPEG, 80, stream);
			byte[] byteArray = stream.toByteArray();
			return byteArray;
		}

		return null;
	}

	public static byte[] bitmapToByteArray(Bitmap bmp, int quality)
	{
		if(bmp!=null)
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress( Bitmap.CompressFormat.JPEG, quality, stream);
			byte[] byteArray = stream.toByteArray();
			return byteArray;
		}

		return null;
	}


	public static Bitmap decodeFileToBitmap(String filePath) 
	{
		// Decode image size
		Bitmap bitmap = null;
		try{

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);

			options.inSampleSize = calculateInSampleSize(options, 150, 180);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(filePath, options);

		}
		catch (Exception e) {

		}
		return bitmap;

	}
	
	public static Bitmap decodeFileToBitmap(String filePath, int w, int h) 
	{
		// Decode image size
		Bitmap bitmap = null;
		try{

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);

			options.inSampleSize = calculateInSampleSize(options, w, h);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(filePath, options);

		}
		catch (Exception e) {

		}
		return bitmap;

	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) 
	{
		if(bitmap!=null)
		{
			int w = bitmap.getWidth();                                          
			int h = bitmap.getHeight();                                         

			int radius = Math.min(h / 2, w / 2);                                
			Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Config.ARGB_8888);

			Paint p = new Paint();                                              
			p.setAntiAlias(true);                                               

			Canvas c = new Canvas(output);                                      
			c.drawARGB(0, 0, 0, 0);                                             
			p.setStyle(Style.FILL); 
			p.setColor(Color.WHITE);

			c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);                  

			p.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));                 

			c.drawBitmap(bitmap, 4, 4, p);                                                     

			return output; 
		}else
		{
			return null;
		}
		
	}

	public static Bitmap decodeFileDescriptorToBitmap(Context context, String filePath) 
	{
		// Decode image size
		Bitmap bitmap = null;
		try
		{	
			File file = new File(context.getFilesDir(), filePath);
			String fileAbsolutePath = file.getAbsolutePath();
			bitmap = BitmapFactory.decodeFile(fileAbsolutePath);

		}
		catch (Exception e) 
		{

		}
		return bitmap;

	}

	public static Bitmap decodeFileDescriptorToBitmap(Context context, String filePath, int w, int h) 
	{
		// Decode image size
		Bitmap bitmap = null;
		try
		{	
			File file = new File(context.getFilesDir(), filePath);
			String fileAbsolutePath = file.getAbsolutePath();

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileAbsolutePath, options);

			options.inSampleSize = calculateInSampleSize(options, w, h);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(fileAbsolutePath, options);

		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return bitmap;

	}

	public static Bitmap decodeByteArrayToBitmap(byte[] pic)
	{
		// Decode image size
		Bitmap bitmap = null;
		try{


			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(pic, 0, pic.length,options);

			options.inSampleSize = calculateInSampleSize(options, 240, 180);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length,options);

		}
		catch (Exception e) {

		}

		return bitmap;
	}

	public static Bitmap decodeByteArrayToBitmap(byte[] pic, int w, int h) {
		// Decode image size
		Bitmap bitmap = null;
		try{


			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(pic, 0, pic.length,options);

			options.inSampleSize = calculateInSampleSize(options, w, h);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length,options);

		}
		catch (Exception e) {

		}

		return bitmap;
	}

	public static Bitmap decodeURLstreamToBitmap(URL url) throws IOException {
		// Decode image size
		Bitmap bitmap = null;
		try{

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(url.openConnection().getInputStream(),null,options);

			options.inSampleSize = calculateInSampleSize(options, 150, 180);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream(),null,options);

		}
		catch (Exception e) {

		}
		return bitmap;
	}

	public static Bitmap decodeURLstreamToBitmap(URL url, int w, int h) throws IOException 
	{
		// Decode image size
		Bitmap bitmap = null;
		try{

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(url.openConnection().getInputStream(),null,options);

			options.inSampleSize = calculateInSampleSize(options, w, h);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream(),null,options);				

		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return bitmap;
	}
	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float)height / (float)reqHeight);
			} else {
				inSampleSize = Math.round((float)width / (float)reqWidth);
			}
		}
		return inSampleSize;
	}

	/**
	 * @param displayHeight
	 * @return
	 */
	public static Bitmap scalingImageBitmapFromResources(Resources res, int resId,int displayHeight)
	{	
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		int outHeight = options.outHeight;
		int outWidth = options.outWidth;

		float dhScaling = displayHeight/(float)outHeight;

		int reqWidth = Math.round(outWidth * dhScaling);

		int reqHeight = Math.round(outHeight * dhScaling);

		// Calculate inSampleSize
		options.inSampleSize = ImageHandler.calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap =  BitmapFactory.decodeResource(res, resId, options);
		bitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);			

		return bitmap;
	}

	public static Bitmap scalingImageBitmapFromResourcesWithKnownBounds(Resources res, int resId,float dhScaling,int refWidth, int refHeight)
	{	
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();

		int reqWidth = Math.round(refWidth * dhScaling);

		int reqHeight = Math.round(refHeight * dhScaling);

		// Calculate inSampleSize
		options.inSampleSize = ImageHandler.calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap =  BitmapFactory.decodeResource(res, resId, options);
		bitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);			

		return bitmap;
	}

	public static Bitmap bitmapFromLocalPathOrUrl(Context context, String imageLocalPath, String imageUrl, float dh, int refWidth, int refHeight) 
	{
		float bitmapScaledWidth = refWidth*dh;
		float bitmapScaledHeight = refHeight*dh;	
		int bitmapScaledWidthRounded = Math.round(bitmapScaledWidth);
		int bitmapScaledHeightRounded = Math.round(bitmapScaledHeight);	

		Bitmap bitmap = null;

		if(imageLocalPath!=null)
		{
			bitmap = ImageHandler.decodeFileDescriptorToBitmap(context,imageLocalPath,bitmapScaledWidthRounded,bitmapScaledHeightRounded);
			if(bitmap!=null)
			{	
				final Bitmap auxBitmap = bitmap;
				bitmap = Bitmap.createScaledBitmap(auxBitmap, bitmapScaledWidthRounded, bitmapScaledHeightRounded, true);
				if(!auxBitmap.equals(bitmap))
					auxBitmap.recycle();
			}						
		}
		if(bitmap == null)
		{
			try 
			{
				bitmap = ImageHandler.decodeURLstreamToBitmap(new URL(imageUrl), bitmapScaledWidthRounded,bitmapScaledHeightRounded);
				if(bitmap!=null)
				{
					final Bitmap auxBitmap = bitmap;
					bitmap = Bitmap.createScaledBitmap(auxBitmap, bitmapScaledWidthRounded, bitmapScaledHeightRounded, true);
					if(!auxBitmap.equals(bitmap))
						auxBitmap.recycle();
				}								
			} catch (MalformedURLException e) 
			{					
				e.printStackTrace();
			} catch (IOException e) 
			{					
				e.printStackTrace();
			}						
		}

		return bitmap;
	}

	public static Bitmap scalingImageBitmapFromResourcesWithKnownBounds(Resources res, int resId,int refWidth, int refHeight)
	{	
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();

		int reqWidth = refWidth;

		int reqHeight = refHeight;

		// Calculate inSampleSize
		options.inSampleSize = ImageHandler.calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap =  BitmapFactory.decodeResource(res, resId, options);
		bitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);			

		return bitmap;
	}

	public static Bitmap bitmapFromLocalPathOrUrlWithKnownBounds(Context context, String imageLocalPath, String imageUrl,int bitmapScaledWidthRounded, int bitmapScaledHeightRounded)
	{	
		Bitmap bitmap = null;

		if(imageLocalPath!=null)
		{
			bitmap = ImageHandler.decodeFileDescriptorToBitmap(context,imageLocalPath,bitmapScaledWidthRounded,bitmapScaledHeightRounded);
			if(bitmap!=null)
			{			
				final Bitmap auxBitmap = bitmap;
				bitmap = Bitmap.createScaledBitmap(auxBitmap, bitmapScaledWidthRounded, bitmapScaledHeightRounded, true);
				if(!auxBitmap.equals(bitmap))
					auxBitmap.recycle();
			}						
		}
		if(bitmap == null)
		{
			try 
			{
				bitmap = ImageHandler.decodeURLstreamToBitmap(new URL(imageUrl), bitmapScaledWidthRounded,bitmapScaledHeightRounded);
				if(bitmap!=null)
				{
					final Bitmap auxBitmap = bitmap;
					bitmap = Bitmap.createScaledBitmap(auxBitmap, bitmapScaledWidthRounded, bitmapScaledHeightRounded, true);
					if(!auxBitmap.equals(bitmap))
						auxBitmap.recycle();
				}								
			} catch (MalformedURLException e) 
			{					
				e.printStackTrace();
			} catch (IOException e) 
			{					
				e.printStackTrace();
			}						
		}

		return bitmap;
	}

}
