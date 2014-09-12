/**
 * 
 */
package org.vocefiscal.services;

import java.util.ArrayList;

import org.vocefiscal.amazonaws.AWSPictureUploadModel;
import org.vocefiscal.amazonaws.AWSPictureUploadModel.OnPictureUploadS3PostExecuteListener;
import org.vocefiscal.asynctasks.AsyncTask;
import org.vocefiscal.asynctasks.SendEmailAsyncTask;
import org.vocefiscal.asynctasks.SendEmailAsyncTask.OnSentMailListener;
import org.vocefiscal.communications.CommunicationConstants;
import org.vocefiscal.communications.CommunicationUtils;
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
public class UploadManagerService extends Service implements OnPictureUploadS3PostExecuteListener, OnSentMailListener
{
	public static final String ID_FISCALIZACAO = "id_fiscalizacao";

	private VoceFiscalDatabase voceFiscalDatabase;	

	private int backoffS3 = 0;

	private int backoffEmail = 0;
	
	private Municipalities municipalites;

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() 
	{
		super.onCreate();

		voceFiscalDatabase = new VoceFiscalDatabase(this);
		
		municipalites = Municipalities.getInstance(this);
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
				Long idFiscalizacao = bundle.getLong(ID_FISCALIZACAO, -1l);
				if(idFiscalizacao>0l)
				{
					Fiscalizacao fiscalizacao = getFiscalizacaoById(idFiscalizacao);
					if(fiscalizacao!=null)
					{
						startUploadingFiscalizacao(fiscalizacao);
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

					}else if(fiscalizacao.getStatusDoEnvio()!=null&&fiscalizacao.getStatusDoEnvio().equals(StatusEnvioEnum.ENVIADO_S3.ordinal()))
					{
						boolean isOnWiFi = CommunicationUtils.isWifi(getApplicationContext());

						if(isOnWiFi || (fiscalizacao.getPodeEnviarRedeDados()!=null&&fiscalizacao.getPodeEnviarRedeDados().equals(1)))
						{
							// redundância de envio por email. Não tem garantia que vai chegar, apesar de ter backoff
							SendEmailAsyncTask sendEmailAsyncTask = new SendEmailAsyncTask(this,this,fiscalizacao,0);
							sendEmailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	

							//TODO - serviço para a API VF.
						}
					}
				}
			}
		}
	}

	private void startUploadingFiscalizacao(Fiscalizacao fiscalizacao) 
	{
		boolean isOnWiFi = CommunicationUtils.isWifi(getApplicationContext());

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
					AWSPictureUploadModel model = new AWSPictureUploadModel(getApplicationContext(), this, picturePathList.get(quantidadeDeFotosUploaded), municipalites.getSlug(fiscalizacao.getEstado(),fiscalizacao.getMunicipio()), fiscalizacao.getZonaEleitoral(), fiscalizacao.getIdFiscalizacao(), quantidadeDeFotosUploaded,0);
					Thread t = new Thread(model.getUploadRunnable());
					t.start();
				}else
				{		
					fiscalizacao.setStatusDoEnvio(StatusEnvioEnum.ENVIADO_S3.ordinal());
					if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
						voceFiscalDatabase.updateStatusEnvio(fiscalizacao.getIdFiscalizacao(),StatusEnvioEnum.ENVIADO_S3.ordinal());

					// redundância de envio por email. Não tem garantia que vai chegar, apesar de ter backoff
					SendEmailAsyncTask sendEmailAsyncTask = new SendEmailAsyncTask(this,this,fiscalizacao,0);
					sendEmailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	

					//TODO - serviço para a API VF.
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

			Fiscalizacao fiscalizacao = getFiscalizacaoById(idFiscalizacao);

			if(fiscalizacao!=null)
			{
				backoffEmail++;

				SendEmailAsyncTask sendEmailAsyncTask = new SendEmailAsyncTask(this,this,fiscalizacao,CommunicationConstants.WAIT_RETRY*backoffEmail);
				sendEmailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	
			}			
		}

	}

	@Override
	public void finishedPictureUploadS3ComResultado(S3UploadPictureResult resultado) 
	{
		backoffS3 = 0;

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
				boolean isOnWiFi = CommunicationUtils.isWifi(getApplicationContext());	

				if(isOnWiFi || (fiscalizacao.getPodeEnviarRedeDados()!=null&&fiscalizacao.getPodeEnviarRedeDados().equals(1)))
				{
					ArrayList<String> picturePathList = fiscalizacao.getPicturePathList();

					String slugFiscalizacao = resultado.getSlugFiscalizacao();
					String zonaFiscalizacao = resultado.getZonaFiscalizacao();
					Integer posicaoFoto = resultado.getPosicaoFoto();
					posicaoFoto++;
					if(posicaoFoto<picturePathList.size())
					{						
						AWSPictureUploadModel model = new AWSPictureUploadModel(getApplicationContext(), this,picturePathList.get(posicaoFoto), slugFiscalizacao,zonaFiscalizacao, idFiscalizacao, posicaoFoto,0);
						Thread t = new Thread(model.getUploadRunnable());
						t.start();
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
	public void finishedPictureUploadS3ComError(String slugFiscalizacao, String zonaFiscalizacao, Long idFiscalizacao,Integer posicaoFoto) 
	{
		Fiscalizacao fiscalizacao = getFiscalizacaoById(idFiscalizacao);

		if(fiscalizacao!=null)
		{
			backoffS3++;	

			ArrayList<String> picturePathList = fiscalizacao.getPicturePathList();

			AWSPictureUploadModel model = new AWSPictureUploadModel(getApplicationContext(), this,picturePathList.get(posicaoFoto), slugFiscalizacao,zonaFiscalizacao,idFiscalizacao, posicaoFoto,CommunicationConstants.WAIT_RETRY*backoffS3);
			Thread t = new Thread(model.getUploadRunnable());
			t.start();
		}	

	}

}
