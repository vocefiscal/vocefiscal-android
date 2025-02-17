/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vocefiscal.bitmaps;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.vocefiscal.BuildConfig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

/**
 * A simple subclass of {@link ImageResizer} that fetches and resizes images fetched from a URL.
 */
public class ImageFetcher extends ImageResizer 
{
    private static final String TAG = "ImageFetcher";
    private static final int HTTP_CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String HTTP_CACHE_DIR = "http";
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    private DiskLruCache mHttpDiskCache;
    private File mHttpCacheDir;
    private boolean mHttpDiskCacheStarting = true;
    private final Object mHttpDiskCacheLock = new Object();
    private static final int DISK_CACHE_INDEX = 0;
    
    private int modoExecucao = -1;
    public static final int DOWNLOAD = 0;
    public static final int CARREGAR_DO_DISCO = 1;
    public static final int CARREGAR_DOS_RECURSOS = 2;

    /**
     * Initialize providing a target image width and height for the processing images.
     *
     * @param context
     * @param imageWidth
     * @param imageHeight
     */
    public ImageFetcher(int modoExecucao,Context context, int imageWidth, int imageHeight) 
    {
        super(context, imageWidth, imageHeight);
        init(context);
        this.modoExecucao = modoExecucao;
    }

    /**
     * Initialize providing a single target image size (used for both width and height);
     *
     * @param context
     * @param imageSize
     */
    public ImageFetcher(int modoExecucao, Context context, int imageSize) 
    {
        super(context, imageSize);
        init(context);
        this.modoExecucao = modoExecucao;
    }

    private void init(Context context) 
    {
        checkConnection(context);
        mHttpCacheDir = ImageCache.getDiskCacheDir(context, HTTP_CACHE_DIR);
    }

    @Override
    protected void initDiskCacheInternal() 
    {
        super.initDiskCacheInternal();
        initHttpDiskCache();
    }

    private void initHttpDiskCache() 
    {
        if (!mHttpCacheDir.exists()) 
        {
            mHttpCacheDir.mkdirs();
        }
        synchronized (mHttpDiskCacheLock) 
        {
            if (ImageCache.getUsableSpace(mHttpCacheDir) > HTTP_CACHE_SIZE) 
            {
                try 
                {
                    mHttpDiskCache = DiskLruCache.open(mHttpCacheDir, 1, 1, HTTP_CACHE_SIZE);
//                    if (BuildConfig.DEBUG) 
//                    {
//                        Log.d(TAG, "HTTP cache initialized");
//                    }
                } catch (IOException e) 
                {
                    mHttpDiskCache = null;
                }
            }
            mHttpDiskCacheStarting = false;
            mHttpDiskCacheLock.notifyAll();
        }
    }

    @Override
    protected void clearCacheInternal() 
    {
        super.clearCacheInternal();
        synchronized (mHttpDiskCacheLock) 
        {
            if (mHttpDiskCache != null && !mHttpDiskCache.isClosed()) 
            {
                try 
                {
                    mHttpDiskCache.delete();
//                    if (BuildConfig.DEBUG) 
//                    {
//                        Log.d(TAG, "HTTP cache cleared");
//                    }
                } catch (IOException e) 
                {
                   // Log.e(TAG, "clearCacheInternal - " + e);
                }
                mHttpDiskCache = null;
                mHttpDiskCacheStarting = true;
                initHttpDiskCache();
            }
        }
    }

    @Override
    protected void flushCacheInternal() 
    {
        super.flushCacheInternal();
        synchronized (mHttpDiskCacheLock) 
        {
            if (mHttpDiskCache != null)
            {
                try 
                {
                    mHttpDiskCache.flush();
//                    if (BuildConfig.DEBUG) 
//                    {
//                        Log.d(TAG, "HTTP cache flushed");
//                    }
                } catch (IOException e) 
                {
                   // Log.e(TAG, "flush - " + e);
                }
            }
        }
    }

    @Override
    protected void closeCacheInternal()
    {
        super.closeCacheInternal();
        synchronized (mHttpDiskCacheLock)
        {
            if (mHttpDiskCache != null) 
            {
                try 
                {
                    if (!mHttpDiskCache.isClosed()) 
                    {
                        mHttpDiskCache.close();
                        mHttpDiskCache = null;
//                        if (BuildConfig.DEBUG) 
//                        {
//                            Log.d(TAG, "HTTP cache closed");
//                        }
                    }
                } catch (IOException e) 
                {
                   // Log.e(TAG, "closeCacheInternal - " + e);
                }
            }
        }
    }

    /**
    * Simple network connection check.
    *
    * @param context
    */
    private void checkConnection(Context context) 
    {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) 
        {
            //Toast.makeText(context, R.string.no_network_connection_toast, Toast.LENGTH_LONG).show();
            //Log.e(TAG, "checkConnection - no connection found");
        }
    }

    /**
     * The main process method, which will be called by the ImageWorker in the AsyncTask background
     * thread.
     *
     * @param data The data to load the bitmap, in this case, a regular http URL
     * @return The downloaded and resized bitmap
     */
    private Bitmap processBitmap(String data) 
    {
    	 Bitmap bitmap = null;
    	 
    	if(modoExecucao==DOWNLOAD)
    	{
//            if (BuildConfig.DEBUG) 
//            {
//                Log.d(TAG, "processBitmap - " + data);
//            }

            final String key = ImageCache.hashKeyForDisk(data);
            FileDescriptor fileDescriptor = null;
            FileInputStream fileInputStream = null;
            DiskLruCache.Snapshot snapshot;
            synchronized (mHttpDiskCacheLock) 
            {
                // Wait for disk cache to initialize
                while (mHttpDiskCacheStarting) 
                {
                    try 
                    {
                        mHttpDiskCacheLock.wait();
                    } catch (InterruptedException e) {}
                }

                if (mHttpDiskCache != null) 
                {
                    try 
                    {
                        snapshot = mHttpDiskCache.get(key);
                        if (snapshot == null) {
//                            if (BuildConfig.DEBUG) 
//                            {
//                                Log.d(TAG, "processBitmap, not found in http cache, downloading...");
//                            }
                            DiskLruCache.Editor editor = mHttpDiskCache.edit(key);
                            if (editor != null) 
                            {
                                if (downloadUrlToStream(data, editor.newOutputStream(DISK_CACHE_INDEX))) 
                                {
                                    editor.commit();
                                } else 
                                {
                                    editor.abort();
                                }
                            }
                            snapshot = mHttpDiskCache.get(key);
                        }
                        if (snapshot != null) 
                        {
                            fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
                            fileDescriptor = fileInputStream.getFD();
                        }
                    } catch (IOException e) 
                    {
                       // Log.e(TAG, "processBitmap - " + e);
                    } catch (IllegalStateException e) 
                    {
                       // Log.e(TAG, "processBitmap - " + e);
                    } finally 
                    {
                        if (fileDescriptor == null && fileInputStream != null)
                        {
                            try 
                            {
                                fileInputStream.close();
                            } catch (IOException e) {}
                        }
                    }
                }
            }
            
            if (fileDescriptor != null) 
            {
                bitmap = decodeSampledBitmapFromDescriptor(fileDescriptor, mImageWidth, mImageHeight, getImageCache());
            }
            
            if (fileInputStream != null) 
            {
                try 
                {
                    fileInputStream.close();
                } catch (IOException e) {}
            }
            
    	}else if(modoExecucao==CARREGAR_DO_DISCO)
    	{    		
    		bitmap = decodeSampledBitmapFromFile(data, mImageWidth, mImageHeight, getImageCache());
    		try
    		{
    			ExifInterface ei = new ExifInterface(data);
    			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
    			Matrix matrix = new Matrix();
    			boolean isRotated = false;
    			switch(orientation) 
    			{
    			case ExifInterface.ORIENTATION_ROTATE_90:
    				matrix.postRotate(90);
    				isRotated = true;
    				break;
    			case ExifInterface.ORIENTATION_ROTATE_180:
    				matrix.postRotate(180);
    				isRotated = true;
    				break;
    			case ExifInterface.ORIENTATION_ROTATE_270:
    				matrix.postRotate(-90);
    				isRotated = true;
    				break;
    			}
    			if (isRotated)
    			{
    				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    			}
    		}
    		catch (Exception e)
    		{
    			
    		}
    	}else if(modoExecucao==CARREGAR_DOS_RECURSOS)
    	{    		   
    		bitmap = decodeSampledBitmapFromResource(mResources, Integer.valueOf(data), mImageWidth, mImageHeight, getImageCache());
    	}
    	
        return bitmap;
    }

    @Override
    protected Bitmap processBitmap(Object data) 
    {
        return processBitmap(String.valueOf(data));
    }

    /**
     * Download a bitmap from a URL and write the content to an output stream.
     *
     * @param urlString The URL to fetch
     * @return true if successful, false otherwise
     */
    public boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        disableConnectionReuseIfNecessary();
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
           // Log.e(TAG, "Error in downloadBitmap - " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {}
        }
        return false;
    }

    /**
     * Workaround for bug pre-Froyo, see here for more info:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     */
    public static void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }
}
