/**
 * 
 */
package org.vocefiscal.asynctasks;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import org.vocefiscal.communications.CommunicationConstants;
import org.vocefiscal.communications.CommunicationUtils;
import org.vocefiscal.communications.JsonHandler;
import org.vocefiscal.email.GMailSender;
import org.vocefiscal.models.Fiscalizacao;

import android.content.Context;

/**
 * @author andre
 *
 */
public class SendEmailAsyncTask extends AsyncTask <Void, Void, Boolean>
{	    
	private OnSentMailListener listener;	
	private Context context; 		
	private Fiscalizacao fiscalizacao;

	private int errorCode = -1;
	private Integer sleep;		

	public SendEmailAsyncTask(Context context,OnSentMailListener listener,Fiscalizacao fiscalizacao, Integer sleep) 
	{
		super();
		this.listener = listener;		
		this.context = context;
		this.sleep = sleep; 
		this.fiscalizacao = fiscalizacao;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... params) 
	{
		boolean hasInternet = CommunicationUtils.verifyConnectivity(context);

		if(hasInternet)
		{
			try 
			{
				if(!isCancelled())
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

					String subject = "Fiscalização - "+fiscalizacao.getMunicipio()+","+fiscalizacao.getEstado()+"; Zona: "+fiscalizacao.getZonaEleitoral()+"; Seção: "+fiscalizacao.getSecaoEleitoral();

					JsonHandler jsonHandler = new JsonHandler();
					String body =  jsonHandler.fromObjectToJsonData(fiscalizacao);

					GMailSender m = new GMailSender(CommunicationConstants.EMAIL_USER, CommunicationConstants.EMAIL_PASSWORD);	       
					m.setTo(CommunicationConstants.EMAIL_TO); 
					m.setFrom(CommunicationConstants.EMAIL_USER); 
					m.setSubject(subject); 
					m.setBody(body); 
					//						if(attachments!=null)
					//						{
					//							for(String file : attachments)
					//							{
					//								try 
					//								{
					//									m.addAttachment(file);
					//								} catch (Exception e) 
					//								{
					//									e.printStackTrace();
					//								} 
					//							}     
					//						} 

					m.send();
					return true;
				}else
				{
					errorCode = CommunicationConstants.CANCELED;
					return false;
				}
			} catch (AuthenticationFailedException e) 
			{				 
				return false;
			} catch (MessagingException e) 
			{				 
				return false;
			} catch (Exception e) 
			{				 
				return false;
			}	
		}else
		{
			errorCode = CommunicationConstants.SEM_INTERNET;
			return false;
		}	 	 
	}

	@Override
	protected void onPostExecute(Boolean result) 
	{		
		if(result!=null&&result)
		{
			listener.finishedSendingEmail(true,errorCode,fiscalizacao.getIdFiscalizacao());
		}
		else  if(errorCode!=CommunicationConstants.CANCELED)
		{
			listener.finishedSendingEmail(false,errorCode,fiscalizacao.getIdFiscalizacao());
		}
	}

	public interface OnSentMailListener
	{
		public void finishedSendingEmail(Boolean result, int errorCode, Long idFiscalizacao);		
	}
}
