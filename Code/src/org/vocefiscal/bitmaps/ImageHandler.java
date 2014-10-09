package org.vocefiscal.bitmaps;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

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

			} catch (IOException e) 
			{

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

		}

		m.update(bytes,0,bytes.length);
		String hash = new BigInteger(1, m.digest()).toString(16);
		return hash;
	}

	public static Bitmap cropBitmapLastThird(Bitmap bmp,int screenHeight,int cutHeight, int screenWidth)
	{	    
		int pictureWidth = bmp.getWidth();
		int pictureHeight = bmp.getHeight();

		//	    Log.e("ImageHandler", "width: "+String.valueOf(width));
		//	    Log.e("ImageHandler", "height: "+String.valueOf(height));
		//	    Log.e("ImageHandler", "pictureHeight: "+String.valueOf(pictureHeight));
		//	    Log.e("ImageHandler", "cutHeight: "+String.valueOf(cutHeight));

		int beginCutWidth = (int) ((pictureWidth-screenWidth)/2.0f);
		if(beginCutWidth<0)
			beginCutWidth=0;

		float dh = pictureHeight/(float) screenHeight;

		int beginCutHeight = (int) (pictureHeight-1.5*cutHeight*dh);

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

			} catch (IOException e) 
			{					

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

			} catch (IOException e) 
			{					

			}						
		}

		return bitmap;
	}

	/**
	 * A copy of the Android internals insertImage method, this method populates the
	 * meta data with DATE_ADDED and DATE_TAKEN. This fixes a common problem where media
	 * that is inserted manually gets saved at the end of the gallery (because date is not populated).
	 * @see android.provider.MediaStore.Images.Media#insertImage(ContentResolver, Bitmap, String, String)
	 */
	public static final String insertImage(ContentResolver cr,
			Bitmap source,
			String title,
			String description) {
		ContentValues values = new ContentValues();
		values.put(Images.Media.TITLE, title);
		values.put(Images.Media.DISPLAY_NAME, title);
		values.put(Images.Media.DESCRIPTION, description);
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		// Add the date meta data to ensure the image is added at the front of the gallery
		values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
		values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());

		Uri url = null;
		String stringUrl = null; /* value to be returned */

		try {
			url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

			if (source != null) {
				OutputStream imageOut = cr.openOutputStream(url);
				try {
					source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
				} finally {
					imageOut.close();
				}

				long id = ContentUris.parseId(url);
				// Wait until MINI_KIND thumbnail is generated.
				Bitmap miniThumb = Images.Thumbnails.getThumbnail(cr, id, Images.Thumbnails.MINI_KIND, null);
				// This is for backward compatibility.
				storeThumbnail(cr, miniThumb, id, 50F, 50F,Images.Thumbnails.MICRO_KIND);
			} else {
				cr.delete(url, null, null);
				url = null;
			}
		} catch (Exception e) {
			if (url != null) {
				cr.delete(url, null, null);
				url = null;
			}
		}

		if (url != null) {
			stringUrl = url.toString();
		}

		return stringUrl;
	}
	/**
	 * A copy of the Android internals StoreThumbnail method, it used with the insertImage to
	 * populate the android.provider.MediaStore.Images.Media#insertImage with all the correct
	 * meta data. The StoreThumbnail method is private so it must be duplicated here.
	 * @see android.provider.MediaStore.Images.Media (StoreThumbnail private method)
	 */
	private static final Bitmap storeThumbnail(
			ContentResolver cr,
			Bitmap source,
			long id,
			float width,
			float height,
			int kind) {
		// create the matrix to scale it
		Matrix matrix = new Matrix();

		float scaleX = width / source.getWidth();
		float scaleY = height / source.getHeight();

		matrix.setScale(scaleX, scaleY);

		Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
				source.getWidth(),
				source.getHeight(), matrix,
				true
				);

		ContentValues values = new ContentValues(4);
		values.put(Images.Thumbnails.KIND,kind);
		values.put(Images.Thumbnails.IMAGE_ID,(int)id);
		values.put(Images.Thumbnails.HEIGHT,thumb.getHeight());
		values.put(Images.Thumbnails.WIDTH,thumb.getWidth());

		Uri url = cr.insert(Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

		try {
			OutputStream thumbOut = cr.openOutputStream(url);
			thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
			thumbOut.close();
			return thumb;
		} catch (FileNotFoundException ex) {
			return null;
		} catch (IOException ex) {
			return null;
		}
	}

}
