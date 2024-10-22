/**
 * 
 */
package org.vocefiscal.services;

import java.util.ArrayList;
import java.util.HashMap;

import org.vocefiscal.amazonaws.AWSFiscalizacaoUpload;
import org.vocefiscal.amazonaws.AWSFiscalizacaoUpload.OnFiscalizacaoUploadS3PostExecuteListener;
import org.vocefiscal.amazonaws.AWSPictureUpload;
import org.vocefiscal.amazonaws.AWSPictureUpload.OnPictureUploadS3PostExecuteListener;
import org.vocefiscal.asynctasks.AsyncTask;
import org.vocefiscal.asynctasks.SendEmailAsyncTask;
import org.vocefiscal.asynctasks.SendEmailAsyncTask.OnSentMailListener;
import org.vocefiscal.communications.CommunicationConstants;
import org.vocefiscal.database.VoceFiscalDatabase;
import org.vocefiscal.models.Fiscalizacao;
import org.vocefiscal.models.S3UploadPictureResult;
import org.vocefiscal.models.enums.StatusEnvioEnum;
import org.vocefiscal.utils.Municipalities;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

/**
 * @author andre
 *
 */
public class UploadManagerService extends Service implements OnPictureUploadS3PostExecuteListener, OnSentMailListener, OnFiscalizacaoUploadS3PostExecuteListener
{
	public static final String ID_FISCALIZACAO = "id_fiscalizacao";

	public static final String COMMAND = "command";

	public static final Integer STOP_UPLOADING = 0;

	public static final Integer START_UPLOADING = 1;

	private VoceFiscalDatabase voceFiscalDatabase;	

	private int backoffPictures = 0;

	private int backoffEmail = 0;
	
	private int backoffFiscalizacao = 0;
	
	private Municipalities municipalites;
	
	private HashMap<Long, AWSPictureUpload> pictureUploadGoingOn;
	
	private HashMap<Long, AWSFiscalizacaoUpload> fiscalizacaoUploadsGoingOn;

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() 
	{
		super.onCreate();

		voceFiscalDatabase = new VoceFiscalDatabase(this);
		
		municipalites = Municipalities.getInstance(this);
		
		pictureUploadGoingOn = new HashMap<Long, AWSPictureUpload>();
		
		fiscalizacaoUploadsGoingOn = new HashMap<Long, AWSFiscalizacaoUpload>();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		if(intent!=null)
		{
			Bundle bundle = intent.getExtras();

			if(bundle!=null)
			{
				Integer command = bundle.getInt(COMMAND,-1);
				if(command >= 0)
				{
					Long idFiscalizacao = bundle.getLong(ID_FISCALIZACAO, -1l);
					if(idFiscalizacao>0l)
					{
						Fiscalizacao fiscalizacao = getFiscalizacaoById(idFiscalizacao);
						if(fiscalizacao!=null)
						{
							if(command.equals(STOP_UPLOADING))
							{
								stopUploadingFiscalizacao(fiscalizacao);
							}else if(command.equals(START_UPLOADING))
							{															
								startUploadingFiscalizacao(fiscalizacao);								
							}	
						}									
					}else
					{
						startUploadingAllFiscalizacoes();
					}
				}else
				{
					startUploadingAllFiscalizacoes();
				}				
			}else
			{
				startUploadingAllFiscalizacoes();
			}
		}else
		{
			startUploadingAllFiscalizacoes();
		}

		return super.onStartCommand(intent, flags, startId);
	}	

	private void startUploadingAllFiscalizacoes() 
	{
		ArrayList<Fiscalizacao> listaDeFiscalizacoes = null;

		if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
			listaDeFiscalizacoes = voceFiscalDatabase.getFiscalizacoes();				

		if(listaDeFiscalizacoes!=null&&listaDeFiscalizacoes.size()>0)
		{
			for(int i=0;i<listaDeFiscalizacoes.size();i++)
			{
				Fiscalizacao fiscalizacao = listaDeFiscalizacoes.get(i);

				if(fiscalizacao!=null)
				{
					if(fiscalizacao.getStatusDoEnvio()!=null&&fiscalizacao.getStatusDoEnvio().equals(StatusEnvioEnum.ENVIANDO.ordinal()))
					{
						startUploadingFiscalizacao(fiscalizacao);

					}else if(fiscalizacao.getStatusDoEnvio()!=null&&fiscalizacao.getStatusDoEnvio().equals(StatusEnvioEnum.ENVIADO_PICTURES.ordinal()))
					{															
						AWSFiscalizacaoUpload awsFiscalizacaoUpload = fiscalizacaoUploadsGoingOn.get(fiscalizacao.getIdFiscalizacao());
						
						if(awsFiscalizacaoUpload==null)
						{
							awsFiscalizacaoUpload = new AWSFiscalizacaoUpload(getApplicationContext(), this, fiscalizacao,0);
							Thread t = new Thread(awsFiscalizacaoUpload.getUploadRunnable());
							t.start();
							
							fiscalizacaoUploadsGoingOn.put(fiscalizacao.getIdFiscalizacao(), awsFiscalizacaoUpload);
						}else
						{
							awsFiscalizacaoUpload.setSleep(0);
							Thread t = new Thread(awsFiscalizacaoUpload.getUploadRunnable());
							t.start();
						}	
					}
				}
			}
		}
	}
	
	private void stopUploadingFiscalizacao(Fiscalizacao fiscalizacao) 
	{	
		ArrayList<String> picturePathList = fiscalizacao.getPicturePathList();

		if(picturePathList!=null&&picturePathList.size()>0)
		{		
			int quantidadeDeFotosUploaded = 0;
			ArrayList<String> pictureURLList = fiscalizacao.getPictureURLList(); 

			if(pictureURLList!=null)
				quantidadeDeFotosUploaded = pictureURLList.size();

			if(quantidadeDeFotosUploaded<picturePathList.size())
			{														
				AWSPictureUpload model = pictureUploadGoingOn.get(fiscalizacao.getIdFiscalizacao());
				if(model!=null)
					model.abort();				
			}else
			{						
				AWSFiscalizacaoUpload awsFiscalizacaoUpload = fiscalizacaoUploadsGoingOn.get(fiscalizacao.getIdFiscalizacao());
				if(awsFiscalizacaoUpload!=null)
					awsFiscalizacaoUpload.abort();
			}
		}	
		
	}

	private void startUploadingFiscalizacao(Fiscalizacao fiscalizacao) 
	{	
		ArrayList<String> picturePathList = fiscalizacao.getPicturePathList();

		if(picturePathList!=null&&picturePathList.size()>0)
		{		
			int quantidadeDeFotosUploaded = 0;
			ArrayList<String> pictureURLList = fiscalizacao.getPictureURLList(); 

			if(pictureURLList!=null)
				quantidadeDeFotosUploaded = pictureURLList.size();

			if(quantidadeDeFotosUploaded<picturePathList.size())
			{			
				AWSPictureUpload model = pictureUploadGoingOn.get(fiscalizacao.getIdFiscalizacao());
				if(model==null)
				{
					model = new AWSPictureUpload(getApplicationContext(), this, picturePathList.get(quantidadeDeFotosUploaded), municipalites.getMunicipalitySlug(fiscalizacao.getEstado(),fiscalizacao.getMunicipio()), fiscalizacao.getZonaEleitoral(), fiscalizacao.getIdFiscalizacao(), quantidadeDeFotosUploaded,fiscalizacao.getPodeEnviarRedeDados(),0);
					Thread t = new Thread(model.getUploadRunnable());
					t.start();
					
					pictureUploadGoingOn.put(fiscalizacao.getIdFiscalizacao(), model);
					
				}else
				{
					model.setNewPictureUploadData(picturePathList.get(quantidadeDeFotosUploaded),quantidadeDeFotosUploaded,fiscalizacao.getPodeEnviarRedeDados(),0);
					Thread t = new Thread(model.getUploadRunnable());
					t.start();
					
				}
			}else
			{		
				fiscalizacao.setStatusDoEnvio(StatusEnvioEnum.ENVIADO_PICTURES.ordinal());
				if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
					voceFiscalDatabase.updateStatusEnvio(fiscalizacao.getIdFiscalizacao(),StatusEnvioEnum.ENVIADO_PICTURES.ordinal());	

				AWSFiscalizacaoUpload awsFiscalizacaoUpload = fiscalizacaoUploadsGoingOn.get(fiscalizacao.getIdFiscalizacao());
						
				if(awsFiscalizacaoUpload==null)
				{
					awsFiscalizacaoUpload = new AWSFiscalizacaoUpload(getApplicationContext(), this, fiscalizacao,0);
					Thread t = new Thread(awsFiscalizacaoUpload.getUploadRunnable());
					t.start();
					
					fiscalizacaoUploadsGoingOn.put(fiscalizacao.getIdFiscalizacao(), awsFiscalizacaoUpload);
				}else
				{
					awsFiscalizacaoUpload.setSleep(0);
					Thread t = new Thread(awsFiscalizacaoUpload.getUploadRunnable());
					t.start();
				}			
			}
		}	
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() 
	{
		super.onDestroy();		
	}

	/* (non-Javaoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onUnbind(android.content.Intent)
	 */
	@Override
	public boolean onUnbind(Intent intent) 
	{
		return super.onUnbind(intent);
	}

	private Fiscalizacao getFiscalizacaoById(Long idFiscalizacao) 
	{		
		Fiscalizacao fiscalizacao = null;

		if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
			fiscalizacao = voceFiscalDatabase.getFiscalizacao(idFiscalizacao);

		return fiscalizacao;
	}

	@Override
	public void finishedSendingEmail(Boolean result, int errorCode, Long idFiscalizacao) 
	{
		if(result)
		{
			backoffEmail = 0;
		}else
		{
			backoffEmail++;
			
			Fiscalizacao fiscalizacao = getFiscalizacaoById(idFiscalizacao);

			if(fiscalizacao!=null)
			{				
				SendEmailAsyncTask sendEmailAsyncTask = new SendEmailAsyncTask(this,this,fiscalizacao,CommunicationConstants.WAIT_RETRY*backoffEmail);
				sendEmailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	
			}			
		}

	}

	@Override
	public void finishedPictureUploadS3ComResultado(S3UploadPictureResult resultado) 
	{	
		backoffPictures = 0;

		Long idFiscalizacao = resultado.getIdFiscalizacao();	
		Fiscalizacao fiscalizacao = getFiscalizacaoById(idFiscalizacao);		

		if(fiscalizacao!=null)
		{
			if(resultado.getUrlDaFoto()!=null)
			{
				ArrayList<String> pictureURLList = fiscalizacao.getPictureURLList();
				if(pictureURLList==null)
					pictureURLList = new ArrayList<String>();
				pictureURLList.add(resultado.getUrlDaFoto());
				fiscalizacao.setPictureURLList(pictureURLList);

				if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
					voceFiscalDatabase.addPictureURL(idFiscalizacao,resultado.getUrlDaFoto());
			}				

			if(fiscalizacao.getStatusDoEnvio()!=null&&fiscalizacao.getStatusDoEnvio().equals(StatusEnvioEnum.ENVIANDO.ordinal()))
			{				
				ArrayList<String> picturePathList = fiscalizacao.getPicturePathList();

				String slugFiscalizacao = resultado.getSlugFiscalizacao();
				String zonaFiscalizacao = resultado.getZonaFiscalizacao();
				Integer posicaoFoto = resultado.getPosicaoFoto();
				posicaoFoto++;
				if(posicaoFoto<picturePathList.size())
				{											
					AWSPictureUpload model = pictureUploadGoingOn.get(fiscalizacao.getIdFiscalizacao());
					if(model==null)
					{
						model = new AWSPictureUpload(getApplicationContext(), this,picturePathList.get(posicaoFoto), slugFiscalizacao,zonaFiscalizacao, idFiscalizacao, posicaoFoto,fiscalizacao.getPodeEnviarRedeDados(),0);
						Thread t = new Thread(model.getUploadRunnable());
						t.start();
						
						pictureUploadGoingOn.put(fiscalizacao.getIdFiscalizacao(), model);
						
					}else
					{
						model.setNewPictureUploadData(picturePathList.get(posicaoFoto),posicaoFoto,fiscalizacao.getPodeEnviarRedeDados(),0);
						Thread t = new Thread(model.getUploadRunnable());
						t.start();

					}					
				}else
				{		
					fiscalizacao.setStatusDoEnvio(StatusEnvioEnum.ENVIADO_PICTURES.ordinal());
					if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
						voceFiscalDatabase.updateStatusEnvio(fiscalizacao.getIdFiscalizacao(),StatusEnvioEnum.ENVIADO_PICTURES.ordinal());						
					
					AWSFiscalizacaoUpload awsFiscalizacaoUpload = fiscalizacaoUploadsGoingOn.get(fiscalizacao.getIdFiscalizacao());
					
					if(awsFiscalizacaoUpload==null)
					{
						awsFiscalizacaoUpload = new AWSFiscalizacaoUpload(getApplicationContext(), this, fiscalizacao,0);
						Thread t = new Thread(awsFiscalizacaoUpload.getUploadRunnable());
						t.start();
						
						fiscalizacaoUploadsGoingOn.put(fiscalizacao.getIdFiscalizacao(), awsFiscalizacaoUpload);
					}else
					{
						awsFiscalizacaoUpload.setSleep(0);
						Thread t = new Thread(awsFiscalizacaoUpload.getUploadRunnable());
						t.start();
					}	
				}
			}
		}	

	}

	@Override
	public void finishedPictureUploadS3ComError(String slugFiscalizacao, String zonaFiscalizacao, Long idFiscalizacao,Integer posicaoFoto) 
	{		
		backoffPictures++;	
		
		Fiscalizacao fiscalizacao = getFiscalizacaoById(idFiscalizacao);

		if(fiscalizacao!=null)
		{
			if(fiscalizacao.getStatusDoEnvio()!=null&&fiscalizacao.getStatusDoEnvio().equals(StatusEnvioEnum.ENVIANDO.ordinal()))
			{
				ArrayList<String> picturePathList = fiscalizacao.getPicturePathList();			
				
				AWSPictureUpload model = pictureUploadGoingOn.get(fiscalizacao.getIdFiscalizacao());
				if(model==null)
				{
					model = new AWSPictureUpload(getApplicationContext(), this,picturePathList.get(posicaoFoto), slugFiscalizacao,zonaFiscalizacao,idFiscalizacao, posicaoFoto, fiscalizacao.getPodeEnviarRedeDados(), CommunicationConstants.WAIT_RETRY*backoffPictures);
					Thread t = new Thread(model.getUploadRunnable());
					t.start();
					
					pictureUploadGoingOn.put(fiscalizacao.getIdFiscalizacao(), model);

				}else
				{
					model.setNewPictureUploadData(picturePathList.get(posicaoFoto),posicaoFoto,fiscalizacao.getPodeEnviarRedeDados(),CommunicationConstants.WAIT_RETRY*backoffPictures);
					Thread t = new Thread(model.getUploadRunnable());
					t.start();

				}	
			}else
			{
				backoffPictures = 0;
			}
		}	

	}

	@Override
	public void finishedFiscalizacaoUploadS3ComResultado(Long idFiscalizacao)
	{
		backoffFiscalizacao = 0;
		
		if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
			voceFiscalDatabase.updateStatusEnvio(idFiscalizacao,StatusEnvioEnum.ENVIADO_TOTAL.ordinal());
		
		Fiscalizacao fiscalizacao = getFiscalizacaoById(idFiscalizacao);

		if(fiscalizacao!=null)
		{
			// redundância de envio por email. Não tem garantia que vai chegar, apesar de ter backoff
			SendEmailAsyncTask sendEmailAsyncTask = new SendEmailAsyncTask(this,this,fiscalizacao,0);
			sendEmailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	
		}			
	}

	@Override
	public void finishedFiscalizacaoUploadS3ComError(Long idFiscalizacao)
	{
		backoffFiscalizacao++;	

		Fiscalizacao fiscalizacao = getFiscalizacaoById(idFiscalizacao);

		if(fiscalizacao!=null)
		{			
			if(fiscalizacao.getStatusDoEnvio()!=null&&fiscalizacao.getStatusDoEnvio().equals(StatusEnvioEnum.ENVIADO_PICTURES.ordinal()))
			{		
				AWSFiscalizacaoUpload awsFiscalizacaoUpload = fiscalizacaoUploadsGoingOn.get(fiscalizacao.getIdFiscalizacao());
				
				if(awsFiscalizacaoUpload==null)
				{
					awsFiscalizacaoUpload = new AWSFiscalizacaoUpload(getApplicationContext(), this, fiscalizacao,CommunicationConstants.WAIT_RETRY*backoffFiscalizacao);
					Thread t = new Thread(awsFiscalizacaoUpload.getUploadRunnable());
					t.start();	
					
					fiscalizacaoUploadsGoingOn.put(fiscalizacao.getIdFiscalizacao(), awsFiscalizacaoUpload);
				}else
				{
					awsFiscalizacaoUpload.setSleep(CommunicationConstants.WAIT_RETRY*backoffFiscalizacao);
					Thread t = new Thread(awsFiscalizacaoUpload.getUploadRunnable());
					t.start();
				}
			}else
			{
				backoffFiscalizacao = 0;
			}				
		}		
	}
}
