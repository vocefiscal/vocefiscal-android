/**
 * 
 */
package org.vocefiscal.amazonaws;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.vocefiscal.bitmaps.ImageHandler;
import org.vocefiscal.communications.CommunicationConstants;
import org.vocefiscal.communications.CommunicationUtils;
import org.vocefiscal.communications.JsonHandler;
import org.vocefiscal.models.Fiscalizacao;
import org.vocefiscal.utils.Municipalities;

import android.content.Context;
import android.os.Environment;

import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.exception.PauseException;

/**
 * @author andre
 *
 */
public class AWSFiscalizacaoUpload extends AWSTransferModel 
{
	private Upload mUpload;
	private PersistableUpload mPersistableUpload;
	private ProgressListener progressListener;
	private Status mStatus;
	private File fiscalizacaoJSONFile;
	private String fiscalizacaoJSONFileName;

	private Fiscalizacao fiscalizacao;

	private Integer sleep;

	private OnFiscalizacaoUploadS3PostExecuteListener listener;

	private Municipalities municipalities;

	public AWSFiscalizacaoUpload(Context context,OnFiscalizacaoUploadS3PostExecuteListener listener,Fiscalizacao fiscalizacao,Integer sleep)
	{
		super(context);

		this.listener = listener;

		this.fiscalizacao = fiscalizacao;

		this.sleep = sleep;

		municipalities = Municipalities.getInstance(context);

		mStatus = Status.IN_PROGRESS;

		progressListener = new ProgressListener() 
		{
			@Override
			public void progressChanged(ProgressEvent event) 
			{ 
				if(event.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) 
				{
					mStatus = Status.COMPLETED;					

					AWSFiscalizacaoUpload.this.listener.finishedFiscalizacaoUploadS3ComResultado(AWSFiscalizacaoUpload.this.fiscalizacao.getIdFiscalizacao());

				}else if(event.getEventCode() == ProgressEvent.FAILED_EVENT_CODE) 
				{
					mStatus = Status.CANCELED;
					
					if(mUpload != null) 
					{
						mUpload.abort();
					}

					AWSFiscalizacaoUpload.this.listener.finishedFiscalizacaoUploadS3ComError(AWSFiscalizacaoUpload.this.fiscalizacao.getIdFiscalizacao());
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

	/* (non-Javadoc)
	 * @see org.vocefiscal.amazonaws.AWSTransferModel#abort()
	 */
	@Override
	public void abort() 
	{
		if(mUpload != null) 
		{
			mStatus = Status.CANCELED;
			mUpload.abort();
		}
	}

	/* (non-Javadoc)
	 * @see org.vocefiscal.amazonaws.AWSTransferModel#getStatus()
	 */
	@Override
	public Status getStatus() 
	{
		return mStatus; 
	}

	/* (non-Javadoc)
	 * @see org.vocefiscal.amazonaws.AWSTransferModel#getTransfer()
	 */
	@Override
	public Transfer getTransfer() 
	{
		return mUpload; 
	}

	/* (non-Javadoc)
	 * @see org.vocefiscal.amazonaws.AWSTransferModel#pause()
	 */
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

	/* (non-Javadoc)
	 * @see org.vocefiscal.amazonaws.AWSTransferModel#resume()
	 */
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

			if(isOnWiFi || (fiscalizacao.getPodeEnviarRedeDados()!=null&&fiscalizacao.getPodeEnviarRedeDados().equals(1)))
			{
				JsonHandler jsonHandler = new JsonHandler();

				try 
				{
					String fiscalizacaoJSON = jsonHandler.fromObjectToJsonData(fiscalizacao);

					fiscalizacaoJSONFile = getOutputMediaFile();
					
					Writer writer = new BufferedWriter(new FileWriter(fiscalizacaoJSONFile));
					writer.write(fiscalizacaoJSON);
					writer.close();
					
					fiscalizacaoJSONFileName= ImageHandler.nomeDaMidia(fiscalizacaoJSONFile) + ".json";		

					if(fiscalizacaoJSONFile != null)
					{
						try 
						{				
							TransferManager mTransferManager = getTransferManager();				

							mUpload = mTransferManager.upload(CommunicationConstants.JSON_BUCKET_NAME, AWSUtil.getPrefix(getContext())+ municipalities.getMunicipalitySlug(fiscalizacao.getEstado(), fiscalizacao.getMunicipio()) + "/zona-" + fiscalizacao.getZonaEleitoral() + "/"+ fiscalizacaoJSONFileName, fiscalizacaoJSONFile);
							mUpload.addProgressListener(progressListener);
						} catch(Exception e) 
						{
							mStatus = Status.CANCELED;
							
							if(mUpload != null) 
							{
								mUpload.abort();
							}

							listener.finishedFiscalizacaoUploadS3ComError(fiscalizacao.getIdFiscalizacao());
						}
					}
				} catch (Exception e) 
				{
					mStatus = Status.CANCELED;
					
					if(mUpload != null) 
					{
						mUpload.abort();
					}

					listener.finishedFiscalizacaoUploadS3ComError(fiscalizacao.getIdFiscalizacao());
				}
			}else
			{
				mStatus = Status.CANCELED;
				
				if(mUpload != null) 
				{
					mUpload.abort();
				}

				listener.finishedFiscalizacaoUploadS3ComError(fiscalizacao.getIdFiscalizacao());
			}
		}else
		{
			mStatus = Status.CANCELED;
			
			if(mUpload != null) 
			{
				mUpload.abort();
			}

			listener.finishedFiscalizacaoUploadS3ComError(fiscalizacao.getIdFiscalizacao());
		}			
	}

	/** Create a File for saving */
	private File getOutputMediaFile()
	{
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		String filePath = "BU_JSON_"+ timeStamp + ".json";

		File mediaFile = new File(Environment.getExternalStorageDirectory(), filePath);		

		return mediaFile;
	}

	public interface OnFiscalizacaoUploadS3PostExecuteListener
	{
		public void finishedFiscalizacaoUploadS3ComResultado(Long idFiscalizacao);
		public void finishedFiscalizacaoUploadS3ComError(Long idFiscalizacao);		
	}

}
