package org.vocefiscal.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
	
	public static Bitmap overlayReference(Bitmap bmp1, Bitmap bmp2,int pixelsFromBottom) 
	{		
		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0, 0, null);
        canvas.drawBitmap(bmp2, 0, bmp1.getHeight()-pixelsFromBottom, null);
        return bmOverlay;
	}
	
	public static Bitmap cropBitmapLastThird(Bitmap bmp)
	{	    
	    int width = bmp.getWidth();
	    int height = bmp.getHeight();
	    int cutHeight = height/4;
	    Bitmap bmOverlay = Bitmap.createBitmap(bmp, 0, height-cutHeight, width, cutHeight);

	    return bmOverlay;
	}
	
//	public static Bitmap drawBmpCircle(Bitmap source, int color, int radius)
//	{	
//		float scaleFactor = 2f;
//		Bitmap scaleBmp = Bitmap.createScaledBitmap(source,	(int)(source.getWidth()*scaleFactor), (int)(source.getHeight()*scaleFactor), true);
//		Bitmap newBmp = Bitmap.createBitmap(scaleBmp.getWidth(), scaleBmp.getHeight(), Bitmap.Config.ARGB_8888);
//		int x = newBmp.getWidth()/2;
//		int y = newBmp.getHeight()/2;
//		for (int xBmp = 0; xBmp < newBmp.getWidth(); xBmp++) 
//		{
//			for (int yBmp = 0; yBmp < newBmp.getHeight(); yBmp++) 
//			{
//				int dist = distance(xBmp,yBmp,x,y);
//				if (dist > radius*scaleFactor)
//				{	
//					newBmp.setPixel(xBmp, yBmp, color);
//				}
//				else 
//				{
//					newBmp.setPixel(xBmp, yBmp, scaleBmp.getPixel(xBmp, yBmp));
//				}
//			}
//		}
//		scaleBmp = null;
//		return Bitmap.createScaledBitmap(newBmp, 
//				(int)(newBmp.getWidth()/scaleFactor), (int)(newBmp.getHeight()/scaleFactor), true);
//	}
//
//	private static int distance (int x1, int y1, int x2, int y2) 
//	{
//		return (int)Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
//	}

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

	/*
	 * Making image in circular shape
//	 */
//	public static Bitmap getRoundedShape(Bitmap sourceBitmap,int targetWidth,int targetHeight) 
//	{
//		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,targetHeight,Bitmap.Config.ARGB_8888);
//
//
//		Canvas canvas = new Canvas(targetBitmap);
//		Path path = new Path();
//		path.addCircle(((float) targetWidth) / 2.0f,((float) targetHeight) / 2.0f,(Math.min(((float) targetWidth),((float) targetHeight)) / 2.0f),Path.Direction.CW);
//
//		Paint paint = new Paint();
//		paint.setColor(Color.WHITE);
//		paint.setStyle(Paint.Style.FILL);
//		paint.setAntiAlias(true);
//		paint.setDither(true);
//		paint.setFilterBitmap(true);
//		canvas.drawCircle(((float) targetWidth) / 2.0f, ((float) targetHeight) / 2.0f, (Math.min(((float) targetWidth),((float) targetHeight)) / 2.0f), paint);
//		
//		canvas.clipPath(path);
//		//Bitmap sourceBitmap = scaleBitmapImage;	
//
//		canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),	sourceBitmap.getHeight()), new RectF(0, 0, targetWidth,targetHeight), paint); 
//
//		return targetBitmap;
//	}



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

	public static Bitmap decodeByteArrayToBitmap(byte[] pic) {
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

//	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int pixels , int w , int h , boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR, boolean withAlpha  ) 
//	{
//
//		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
//		
//		Canvas canvas = new Canvas(output);
//		final float densityMultiplier = context.getResources().getDisplayMetrics().density;
//
//		int color;
//		if(withAlpha)
//			color = 0xAA424242;
//		else
//			color = 0xFF424242;
//
//		final Paint paint = new Paint();
//		final Rect rect = new Rect(0, 0, w, h);
//		final RectF rectF = new RectF(rect);
//
//		//make sure that our rounded corner is scaled appropriately
//		
//		 float roundPx = pixels*densityMultiplier;
//		 float roundPy = roundPx;
//		
//
//		if(pixels < 0)
//		{
//			roundPx = (w*.265f) *densityMultiplier;
//			roundPy = (h*.265f) *densityMultiplier;
//		}
//		paint.setAntiAlias(true);
//		canvas.drawARGB(0, 0, 0, 0);
//		paint.setColor(color);
//		canvas.drawRoundRect(rectF, roundPx, roundPy, paint);
//
//
//		//draw rectangles over the corners we want to be square
//		if (squareTL )
//		{
//			canvas.drawRect(0, 0, w/2, h/2, paint);
//		}
//		if (squareTR )
//		{
//			canvas.drawRect(w/2, 0, w, h/2, paint);
//		}
//		if (squareBL )
//		{
//			canvas.drawRect(0, h/2, w/2, h, paint);
//		}
//		if (squareBR )
//		{
//			canvas.drawRect(w/2, h/2, w, h, paint);
//		}
//
//		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//		canvas.drawBitmap(input, 0,0, paint);
//		
//		input.recycle();
//		
//		return output;
//	}
	
	

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
