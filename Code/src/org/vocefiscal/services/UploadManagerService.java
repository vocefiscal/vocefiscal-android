/**
 * 
 */
package org.vocefiscal.services;

import java.util.ArrayList;

import org.vocefiscal.asynctasks.AsyncTask;
import org.vocefiscal.asynctasks.SalvarFotoS3AsyncTask;
import org.vocefiscal.asynctasks.SalvarFotoS3AsyncTask.OnSalvarFotoS3PostExecuteListener;
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
public class UploadManagerService extends Service   implements OnSalvarFotoS3PostExecuteListener<Object>
{
	private VoceFiscalDatabase voceFiscalDatabase;				
	
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
					if(fiscalizacao.getStatusDoEnvio()!=null&&!fiscalizacao.getStatusDoEnvio().equals(StatusEnvioEnum.ENVIADO.ordinal()))
					{
						if(isOnWiFi || (fiscalizacao.getPodeEnviarRedeDados()!=null&&fiscalizacao.getPodeEnviarRedeDados().equals(1)))
						{														
							ArrayList<String> picturePathList = fiscalizacao.getPicturePathList();
							
							if(picturePathList!=null&&picturePathList.size()>0)
							{		
								int quantidadeDeFotosUploaded = 0;
								ArrayList<String> pictureURLList = fiscalizacao.getPictureURLList(); 
								
								if(pictureURLList!=null)
									quantidadeDeFotosUploaded = picturePathList.size();
								
								if(quantidadeDeFotosUploaded<picturePathList.size())
								{
									SalvarFotoS3AsyncTask salvarFotoS3AsyncTask = new SalvarFotoS3AsyncTask(this, getApplicationContext(), picturePathList.get(quantidadeDeFotosUploaded), fiscalizacao.getIdFiscalizacao(), quantidadeDeFotosUploaded);
									salvarFotoS3AsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
									
									//fiscalizacao.setStatusDoEnvio(StatusEnvioEnum.ENVIANDO.ordinal());
									if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
										voceFiscalDatabase.updateStatusEnvio(fiscalizacao.getIdFiscalizacao(),StatusEnvioEnum.ENVIANDO.ordinal());
								}else
								{
									//fiscalizacao.setStatusDoEnvio(StatusEnvioEnum.ENVIADO.ordinal());
									if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
										voceFiscalDatabase.updateStatusEnvio(fiscalizacao.getIdFiscalizacao(),StatusEnvioEnum.ENVIADO.ordinal());
									
									//TODO - serviço para a API VF. Criar status "transferido" para não correr risco de retransmitir e tratar falhas
								}
							}else
							{
								//fiscalizacao.setStatusDoEnvio(StatusEnvioEnum.ENVIADO.ordinal());
								if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
									voceFiscalDatabase.updateStatusEnvio(fiscalizacao.getIdFiscalizacao(),StatusEnvioEnum.ENVIADO.ordinal());
								
								//TODO - serviço para a API VF. Criar status "transferido" para não correr risco de retransmitir e tratar falhas
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
						if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
							voceFiscalDatabase.updateStatusEnvio(fiscalizacao.getIdFiscalizacao(),StatusEnvioEnum.ENVIAR.ordinal());
					}
				}
			}
		}
		
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
		S3TaskResult resultado = (S3TaskResult) result;
		
		Long idFiscalizacao = resultado.getIdFiscalizacao();	
		if(resultado.getUrlDaFoto()!=null)
		{
			if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
				voceFiscalDatabase.addPictureURL(idFiscalizacao,resultado.getUrlDaFoto().toString());
		}
		
		ArrayList<Fiscalizacao> listaDeFiscalizacoes = null;
		
		if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
			listaDeFiscalizacoes = voceFiscalDatabase.getFiscalizacoes();
		
		boolean isOnWiFi = CommunicationUtils.isWifi(getApplicationContext());

		if(listaDeFiscalizacoes!=null&&listaDeFiscalizacoes.size()>0)
		{
			Fiscalizacao fiscalizacao = getFiscalizacaoById(listaDeFiscalizacoes,idFiscalizacao);

			if(fiscalizacao!=null)
			{
				if(fiscalizacao.getStatusDoEnvio()!=null&&fiscalizacao.getStatusDoEnvio().equals(StatusEnvioEnum.ENVIANDO.ordinal()))
				{
					if(isOnWiFi || (fiscalizacao.getPodeEnviarRedeDados()!=null&&fiscalizacao.getPodeEnviarRedeDados().equals(1)))
					{
						ArrayList<String> picturePathList = fiscalizacao.getPicturePathList();
						
						Integer posicaoFoto = resultado.getPosicaoFoto();
						posicaoFoto++;
						if(posicaoFoto<picturePathList.size())
						{
							SalvarFotoS3AsyncTask salvarFotoS3AsyncTask = new SalvarFotoS3AsyncTask(this, getApplicationContext(), picturePathList.get(posicaoFoto), idFiscalizacao, posicaoFoto);
							salvarFotoS3AsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}else
						{
							//fiscalizacao.setStatusDoEnvio(StatusEnvioEnum.ENVIADO.ordinal());
							if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
								voceFiscalDatabase.updateStatusEnvio(fiscalizacao.getIdFiscalizacao(),StatusEnvioEnum.ENVIADO.ordinal());
							
							//TODO - serviço para a API VF. Criar status "transferido" para não correr risco de retransmitir e tratar falhas
						}
					}else
					{
						Toast.makeText(getApplicationContext(), "Pausando um upload por queda de Wi-Fi", Toast.LENGTH_SHORT).show();
						
						//fiscalizacao.setStatusDoEnvio(StatusEnvioEnum.ENVIAR.ordinal());
						if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
							voceFiscalDatabase.updateStatusEnvio(fiscalizacao.getIdFiscalizacao(),StatusEnvioEnum.ENVIAR.ordinal());
					}
				}
			}
		}		
	}	

	@Override
	public void finishedSalvarFotoS3ComError(int errorCode, String error,Long idFiscalizacao) 
	{
		Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
		
		if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
			voceFiscalDatabase.updateStatusEnvio(idFiscalizacao,StatusEnvioEnum.ENVIAR.ordinal());
		
	}
	
	private Fiscalizacao getFiscalizacaoById(ArrayList<Fiscalizacao> listaDeFiscalizacoes, Long idFiscalizacao) 
	{
		Fiscalizacao fiscalizacao = null;
		
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

}
