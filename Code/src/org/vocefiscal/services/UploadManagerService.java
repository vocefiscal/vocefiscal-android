/**
 * 
 */
package org.vocefiscal.services;

import java.util.ArrayList;

import org.vocefiscal.asynctasks.AsyncTask;
import org.vocefiscal.asynctasks.SalvarFotoS3AsyncTask;
import org.vocefiscal.asynctasks.SalvarFotoS3AsyncTask.OnSalvarFotoS3PostExecuteListener;
import org.vocefiscal.asynctasks.SendEmailAsyncTask;
import org.vocefiscal.asynctasks.SendEmailAsyncTask.OnSentMailListener;
import org.vocefiscal.communications.CommunicationConstants;
import org.vocefiscal.communications.CommunicationUtils;
import org.vocefiscal.database.VoceFiscalDatabase;
import org.vocefiscal.models.Fiscalizacao;
import org.vocefiscal.models.S3TaskResult;
import org.vocefiscal.models.enums.StatusEnvioEnum;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * @author andre
 *
 */
public class UploadManagerService extends Service implements OnSalvarFotoS3PostExecuteListener<Object>, OnSentMailListener
{
	private VoceFiscalDatabase voceFiscalDatabase;	
	
	private int backoffS3 = 0;
	
	private int backoffEmail = 0;
	
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() 
	{
		super.onCreate();
		
		voceFiscalDatabase = new VoceFiscalDatabase(this);
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		ArrayList<Fiscalizacao> listaDeFiscalizacoes = null;
		
		if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
			listaDeFiscalizacoes = voceFiscalDatabase.getFiscalizacoes();
		
		boolean isOnWiFi = CommunicationUtils.isWifi(getApplicationContext());
		
		if(listaDeFiscalizacoes!=null&&listaDeFiscalizacoes.size()>0)
		{
			for(int i=0;i<listaDeFiscalizacoes.size();i++)
			{
				Fiscalizacao fiscalizacao = listaDeFiscalizacoes.get(i);
				
				if(fiscalizacao!=null)
				{
					if(fiscalizacao.getStatusDoEnvio()!=null&&fiscalizacao.getStatusDoEnvio().equals(StatusEnvioEnum.ENVIANDO.ordinal()))
					{
						if(isOnWiFi || (fiscalizacao.getPodeEnviarRedeDados()!=null&&fiscalizacao.getPodeEnviarRedeDados().equals(1)))
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
									fiscalizacao.setStatusDoEnvio(StatusEnvioEnum.ENVIANDO.ordinal());
									if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
										voceFiscalDatabase.updateStatusEnvio(fiscalizacao.getIdFiscalizacao(),StatusEnvioEnum.ENVIANDO.ordinal());
									
									SalvarFotoS3AsyncTask salvarFotoS3AsyncTask = new SalvarFotoS3AsyncTask(this, getApplicationContext(), picturePathList.get(quantidadeDeFotosUploaded), fiscalizacao.getIdFiscalizacao(), quantidadeDeFotosUploaded,0);
									salvarFotoS3AsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);																		
								}else
								{		
									fiscalizacao.setStatusDoEnvio(StatusEnvioEnum.ENVIADO_S3.ordinal());
									if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
										voceFiscalDatabase.updateStatusEnvio(fiscalizacao.getIdFiscalizacao(),StatusEnvioEnum.ENVIADO_S3.ordinal());
									
									// redundância de envio por email. Não tem garantia que vai chegar, apesar de ter backoff
									SendEmailAsyncTask sendEmailAsyncTask = new SendEmailAsyncTask(this,this,fiscalizacao,0);
									sendEmailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	
									
									//TODO - serviço para a API VF. Criar status "transferido" para não correr risco de retransmitir e tratar falhas
								}
							}
						}
					}
				}
			}
		}
		
		return super.onStartCommand(intent, flags, startId);
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
	
	@Override
	public void finishedSalvarFotoS3ComResultado(Object result) 
	{
		backoffS3 = 0;
		
		S3TaskResult resultado = (S3TaskResult) result;

		Long idFiscalizacao = resultado.getIdFiscalizacao();	
		Fiscalizacao fiscalizacao = getFiscalizacaoById(idFiscalizacao);		

		if(fiscalizacao!=null)
		{
			if(resultado.getUrlDaFoto()!=null)
			{
				ArrayList<String> pictureURLList = fiscalizacao.getPictureURLList();
				if(pictureURLList==null)
					pictureURLList = new ArrayList<String>();
				pictureURLList.add(resultado.getUrlDaFoto().toString());
				fiscalizacao.setPictureURLList(pictureURLList);

				if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
					voceFiscalDatabase.addPictureURL(idFiscalizacao,resultado.getUrlDaFoto().toString());
			}				
			
			if(fiscalizacao.getStatusDoEnvio()!=null&&fiscalizacao.getStatusDoEnvio().equals(StatusEnvioEnum.ENVIANDO.ordinal()))
			{				
				boolean isOnWiFi = CommunicationUtils.isWifi(getApplicationContext());	
				
				if(isOnWiFi || (fiscalizacao.getPodeEnviarRedeDados()!=null&&fiscalizacao.getPodeEnviarRedeDados().equals(1)))
				{
					ArrayList<String> picturePathList = fiscalizacao.getPicturePathList();

					Integer posicaoFoto = resultado.getPosicaoFoto();
					posicaoFoto++;
					if(posicaoFoto<picturePathList.size())
					{
						SalvarFotoS3AsyncTask salvarFotoS3AsyncTask = new SalvarFotoS3AsyncTask(this, getApplicationContext(), picturePathList.get(posicaoFoto), idFiscalizacao, posicaoFoto,0);
						salvarFotoS3AsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}else
					{		
						fiscalizacao.setStatusDoEnvio(StatusEnvioEnum.ENVIADO_S3.ordinal());
						if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
							voceFiscalDatabase.updateStatusEnvio(fiscalizacao.getIdFiscalizacao(),StatusEnvioEnum.ENVIADO_S3.ordinal());
						
						// redundância de envio por email. Não tem garantia que vai chegar, apesar de ter backoff
						SendEmailAsyncTask sendEmailAsyncTask = new SendEmailAsyncTask(this,this,fiscalizacao,0);
						sendEmailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	

						//TODO - serviço para a API VF e no resultado colocar ENVIADO_VF
					}
				}
			}
		}			
	}	

	@Override
	public void finishedSalvarFotoS3ComError(int errorCode, String error,Long idFiscalizacao,Integer posicaoFoto) 
	{
		
		Fiscalizacao fiscalizacao = getFiscalizacaoById(idFiscalizacao);
		
		if(fiscalizacao!=null)
		{
			backoffS3++;	
			
			ArrayList<String> picturePathList = fiscalizacao.getPicturePathList();
			
			SalvarFotoS3AsyncTask salvarFotoS3AsyncTask = new SalvarFotoS3AsyncTask(this, getApplicationContext(), picturePathList.get(posicaoFoto), idFiscalizacao, posicaoFoto,CommunicationConstants.WAIT_RETRY*backoffS3);
			salvarFotoS3AsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			
			Toast.makeText(getApplicationContext(), "Problemas na sua conexão. Continuamos tentando enviar a fiscalização da seção "+fiscalizacao.getSecaoEleitoral()+".", Toast.LENGTH_SHORT).show();
		}		
	}
	
	private Fiscalizacao getFiscalizacaoById(Long idFiscalizacao) 
	{
		Fiscalizacao fiscalizacao = null;
		
		ArrayList<Fiscalizacao> listaDeFiscalizacoes = null;
		
		if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
			listaDeFiscalizacoes = voceFiscalDatabase.getFiscalizacoes();
		
		if(listaDeFiscalizacoes!=null&&listaDeFiscalizacoes.size()>0&&idFiscalizacao!=null)
		{
			for(Fiscalizacao fiscalizaoAnalisada : listaDeFiscalizacoes)
			{
				if(fiscalizaoAnalisada.getIdFiscalizacao().equals(idFiscalizacao))
				{
					fiscalizacao = fiscalizaoAnalisada;
					break;
				}				
			}
		}
		
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

			Fiscalizacao fiscalizacao = getFiscalizacaoById(idFiscalizacao);
			
			if(fiscalizacao!=null)
			{
				backoffEmail++;

				SendEmailAsyncTask sendEmailAsyncTask = new SendEmailAsyncTask(this,this,fiscalizacao,CommunicationConstants.WAIT_RETRY*backoffEmail);
				sendEmailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	
			}			
		}
		
	}

}
