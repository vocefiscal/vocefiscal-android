/*
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.vocefiscal.amazonaws;

import java.io.File;
import java.net.URL;
import java.util.Calendar;

import org.vocefiscal.bitmaps.ImageHandler;
import org.vocefiscal.communications.CommunicationConstants;
import org.vocefiscal.models.S3TaskResult;

import android.content.Context;
import android.util.Log;

import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.exception.PauseException;

/* UploadModel handles the interaction between the Upload and TransferManager.
 * This also makes sure that the file that is uploaded has the same file extension
 *
 * One thing to note is that we always create a copy of the file we are given. This
 * is because we wanted to demonstrate pause/resume which is only possible with a
 * File parameter, but there is no reliable way to get a File from a Uri(mainly
 * because there is no guarantee that the Uri has an associated File).
 *
 * You can easily avoid this by directly using an InputStream instead of a Uri.
 */
public class AWSUploadModel extends AWSTransferModel 
{
	private static final String TAG = "UploadModel";

	private Upload mUpload;
	private PersistableUpload mPersistableUpload;
	private ProgressListener progressListener;
	private Status mStatus;
	private File pictureFile;

	private String picturePath;

	private String pictureName;

	private Long idFiscalizacao;

	private Integer posicaoFoto;
	
	private Integer sleep;
	
	private OnUploadS3PostExecuteListener uploadListener;

	public AWSUploadModel(Context context,OnUploadS3PostExecuteListener uploadListener, String picturePath,Long idFiscalizacao,Integer posicaoFoto,Integer sleep) 
	{
		super(context);
		
		this.uploadListener = uploadListener;

		this.picturePath = picturePath;

		this.idFiscalizacao = idFiscalizacao;

		this.posicaoFoto = posicaoFoto;
		
		this.sleep = sleep;

		mStatus = Status.IN_PROGRESS;
	
		progressListener = new ProgressListener() 
		{
			@Override
			public void progressChanged(ProgressEvent event) 
			{ 
				if(event.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) 
				{
					mStatus = Status.COMPLETED;
					
					try
					{
						ResponseHeaderOverrides override = new ResponseHeaderOverrides();
						override.setContentType("image/jpeg");

						GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(CommunicationConstants.BUCKET_NAME, AWSUtil.getPrefix(getContext())+ pictureName);
						urlRequest.setResponseHeaders(override);
						Calendar calendar = Calendar.getInstance();
						calendar.add(Calendar.YEAR, 12);						
						urlRequest.setExpiration(calendar.getTime());//expiry date 12 years ahead						

						URL urlDaFoto = AWSUtil.getS3Client(getContext()).generatePresignedUrl(urlRequest);


						if(urlDaFoto!=null)
						{
							String url = urlDaFoto.toString();                 	

							S3TaskResult result = new S3TaskResult();					
							result.setUrlDaFoto(url);
							result.setPosicaoFoto(AWSUploadModel.this.posicaoFoto);
							result.setIdFiscalizacao(AWSUploadModel.this.idFiscalizacao);

							AWSUploadModel.this.uploadListener.finishedUploadS3ComResultado(result);	
						} 
					}catch(Exception e)
					{

					}                  
				}else if(event.getEventCode() == ProgressEvent.FAILED_EVENT_CODE) 
				{
					mStatus = Status.CANCELED;

					AWSUploadModel.this.uploadListener.finishedUploadS3ComError(AWSUploadModel.this.idFiscalizacao, AWSUploadModel.this.posicaoFoto);
				}
			}
		};
	}

	public Runnable getUploadRunnable() 
	{
		return new Runnable() 
		{
			@Override
			public void run() 
			{
				upload();
			}
		};
	}

	@Override
	public void abort() 
	{
		if(mUpload != null) 
		{
			mStatus = Status.CANCELED;
			mUpload.abort();
		}
	}

	@Override
	public Status getStatus() 
	{ 
		return mStatus; 
	}
	@Override
	public Transfer getTransfer() 
	{
		return mUpload; 
	}

	@Override
	public void pause() 
	{
		if(mStatus == Status.IN_PROGRESS) 
		{
			if(mUpload != null) 
			{
				mStatus = Status.PAUSED;
				try 
				{
					mPersistableUpload = mUpload.pause();
				} catch(PauseException e) 
				{ 
					Log.d(TAG, "", e);
				}
			}
		}
	}

	@Override
	public void resume() 
	{
		if(mStatus == Status.PAUSED)
		{
			mStatus = Status.IN_PROGRESS;
			if(mPersistableUpload != null)
			{
				//if it paused fine, resume
				mUpload = getTransferManager().resumeUpload(mPersistableUpload);
				mUpload.addProgressListener(progressListener);
				mPersistableUpload = null;
			} else 
			{
				//if it was actually aborted, start a new one
				upload();
			}
		}
	}

	public void upload() 
	{
		if(sleep>0)
		{
			try 
			{
				Thread.sleep(sleep);
			} catch (InterruptedException e) 
			{
			}
		}
		
		pictureFile = new File(picturePath);

		pictureName= ImageHandler.nomeDaMidia(pictureFile) + ".jpg";		

		if(pictureFile != null)
		{
			try 
			{				
				TransferManager mTransferManager = getTransferManager();				
				
				mUpload = mTransferManager.upload(CommunicationConstants.BUCKET_NAME, AWSUtil.getPrefix(getContext()) + pictureName, pictureFile);
				mUpload.addProgressListener(progressListener);
			} catch(Exception e) 
			{
				mStatus = Status.CANCELED;

				uploadListener.finishedUploadS3ComError(idFiscalizacao, posicaoFoto);
			}
		}
	}

	public interface OnUploadS3PostExecuteListener
	{
		public void finishedUploadS3ComResultado(S3TaskResult resultado);
		public void finishedUploadS3ComError(Long idFiscalizacao,Integer posicaoFoto);		
	}
}
