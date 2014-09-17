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

import org.vocefiscal.bitmaps.ImageHandler;
import org.vocefiscal.communications.CommunicationConstants;
import org.vocefiscal.communications.CommunicationUtils;
import org.vocefiscal.models.S3UploadPictureResult;

import android.content.Context;

import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.exception.PauseException;

/**
 * 
 * @author andre
 *
 */
public class AWSPictureUpload extends AWSTransferModel 
{
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
	
	private OnPictureUploadS3PostExecuteListener uploadListener;
	
	private String slugFiscalizacao;
	
	private String zonaFiscalizacao;
	
	private Integer podeEnviarRedeDados;

	public AWSPictureUpload(Context context,OnPictureUploadS3PostExecuteListener uploadListener, String picturePath,String slugFiscalizacao, String zonaFiscalizacao, Long idFiscalizacao,Integer posicaoFoto, Integer podeEnviarRedeDados, Integer sleep) 
	{
		super(context);
		
		this.uploadListener = uploadListener;

		this.picturePath = picturePath;
		
		this.slugFiscalizacao = slugFiscalizacao;
		
		this.zonaFiscalizacao = zonaFiscalizacao;

		this.idFiscalizacao = idFiscalizacao;

		this.posicaoFoto = posicaoFoto;
		
		this.podeEnviarRedeDados = podeEnviarRedeDados;
		
		this.sleep = sleep;

		mStatus = Status.IN_PROGRESS;
	
		progressListener = new ProgressListener() 
		{
			@Override
			public void progressChanged(ProgressEvent event) 
			{ 
				if(event.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) 
				{										
					boolean hasInternet = CommunicationUtils.verifyConnectivity(getContext());

					if(hasInternet)
					{
						boolean isOnWiFi = CommunicationUtils.isWifi(getContext());
						
						if(isOnWiFi || (AWSPictureUpload.this.podeEnviarRedeDados!=null&&AWSPictureUpload.this.podeEnviarRedeDados.equals(1)))
						{
							try
							{
								ResponseHeaderOverrides override = new ResponseHeaderOverrides();
								override.setContentType("image/jpeg");

								GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(CommunicationConstants.PICTURE_BUCKET_NAME, AWSUtil.getPrefix(getContext())+ AWSPictureUpload.this.slugFiscalizacao + "/zona-" + AWSPictureUpload.this.zonaFiscalizacao + "/"+ pictureName);
								urlRequest.setResponseHeaders(override);						

								URL urlDaFoto = AWSUtil.getS3Client(getContext()).generatePresignedUrl(urlRequest);


								if(urlDaFoto!=null)
								{
									String string = urlDaFoto.toString();
									String[] parts = string.split("\\?");
									String url = parts[0];                	

									S3UploadPictureResult result = new S3UploadPictureResult();					
									result.setUrlDaFoto(url);
									result.setPosicaoFoto(AWSPictureUpload.this.posicaoFoto);
									result.setIdFiscalizacao(AWSPictureUpload.this.idFiscalizacao);
									result.setSlugFiscalizacao(AWSPictureUpload.this.slugFiscalizacao);
									result.setZonaFiscalizacao(AWSPictureUpload.this.zonaFiscalizacao);
									
									mStatus = Status.COMPLETED;
									AWSPictureUpload.this.uploadListener.finishedPictureUploadS3ComResultado(result);	
								}else
								{
									mStatus = Status.CANCELED;
									
									if(mUpload != null) 
									{
										mUpload.abort();
									}

									AWSPictureUpload.this.uploadListener.finishedPictureUploadS3ComError(AWSPictureUpload.this.slugFiscalizacao,AWSPictureUpload.this.zonaFiscalizacao,AWSPictureUpload.this.idFiscalizacao, AWSPictureUpload.this.posicaoFoto);
								}
							}catch(Exception e)
							{
								mStatus = Status.CANCELED;
								
								if(mUpload != null) 
								{
									mUpload.abort();
								}

								AWSPictureUpload.this.uploadListener.finishedPictureUploadS3ComError(AWSPictureUpload.this.slugFiscalizacao,AWSPictureUpload.this.zonaFiscalizacao,AWSPictureUpload.this.idFiscalizacao, AWSPictureUpload.this.posicaoFoto);
							}  
						}else
						{
							mStatus = Status.CANCELED;
							
							if(mUpload != null) 
							{
								mUpload.abort();
							}

							AWSPictureUpload.this.uploadListener.finishedPictureUploadS3ComError(AWSPictureUpload.this.slugFiscalizacao,AWSPictureUpload.this.zonaFiscalizacao,AWSPictureUpload.this.idFiscalizacao, AWSPictureUpload.this.posicaoFoto);
						}							
					}else
					{
						mStatus = Status.CANCELED;
						
						if(mUpload != null) 
						{
							mUpload.abort();
						}

						AWSPictureUpload.this.uploadListener.finishedPictureUploadS3ComError(AWSPictureUpload.this.slugFiscalizacao,AWSPictureUpload.this.zonaFiscalizacao,AWSPictureUpload.this.idFiscalizacao, AWSPictureUpload.this.posicaoFoto);
					}					                
				}else if(event.getEventCode() == ProgressEvent.FAILED_EVENT_CODE) 
				{
					mStatus = Status.CANCELED;
					
					if(mUpload != null) 
					{
						mUpload.abort();
					}

					AWSPictureUpload.this.uploadListener.finishedPictureUploadS3ComError(AWSPictureUpload.this.slugFiscalizacao,AWSPictureUpload.this.zonaFiscalizacao,AWSPictureUpload.this.idFiscalizacao, AWSPictureUpload.this.posicaoFoto);
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
		
		boolean hasInternet = CommunicationUtils.verifyConnectivity(getContext());

		if(hasInternet)
		{
			boolean isOnWiFi = CommunicationUtils.isWifi(getContext());
			
			if(isOnWiFi ||  (AWSPictureUpload.this.podeEnviarRedeDados!=null&&AWSPictureUpload.this.podeEnviarRedeDados.equals(1)))
			{
				pictureFile = new File(picturePath);

				pictureName= ImageHandler.nomeDaMidia(pictureFile) + ".jpg";		

				if(pictureFile != null)
				{
					try 
					{				
						TransferManager mTransferManager = getTransferManager();				
						
						mUpload = mTransferManager.upload(CommunicationConstants.PICTURE_BUCKET_NAME, AWSUtil.getPrefix(getContext())+ AWSPictureUpload.this.slugFiscalizacao + "/zona-" + AWSPictureUpload.this.zonaFiscalizacao + "/"+ pictureName, pictureFile);
						mUpload.addProgressListener(progressListener);
					} catch(Exception e) 
					{
						mStatus = Status.CANCELED;
						
						if(mUpload != null) 
						{
							mUpload.abort();
						}

						uploadListener.finishedPictureUploadS3ComError(slugFiscalizacao,zonaFiscalizacao,idFiscalizacao, posicaoFoto);
					}
				}
			}else
			{
				mStatus = Status.CANCELED;
				
				if(mUpload != null) 
				{
					mUpload.abort();
				}

				uploadListener.finishedPictureUploadS3ComError(slugFiscalizacao,zonaFiscalizacao,idFiscalizacao, posicaoFoto);
			}
		}else
		{
			mStatus = Status.CANCELED;
			
			if(mUpload != null) 
			{
				mUpload.abort();
			}

			uploadListener.finishedPictureUploadS3ComError(slugFiscalizacao,zonaFiscalizacao,idFiscalizacao, posicaoFoto);
		}
	}

	public interface OnPictureUploadS3PostExecuteListener
	{
		public void finishedPictureUploadS3ComResultado(S3UploadPictureResult resultado);
		public void finishedPictureUploadS3ComError(String slugFiscalizacao, String zonaFiscalizacao, Long idFiscalizacao,Integer posicaoFoto);		
	}
}
