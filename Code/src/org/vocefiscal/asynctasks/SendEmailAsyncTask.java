/**
 * 
 */
package org.vocefiscal.asynctasks;

import java.util.List;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import org.vocefiscal.email.GMailSender;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author andre
 *
 */
public class SendEmailAsyncTask extends AsyncTask <Void, Void, Boolean>
{
	private static final String EMAIL_USER = "<EMAIL>";  
	private static final String EMAIL_PASSWORD = "<SENHA>";
	public static final int SEM_CONEXAO_COM_A_INTERNET = 0;    
	private OnSentMailListener listener; 	
	private GMailSender m;	
	private Context context;
	
	private LinearLayout progressBarLayout;
	private LinearLayout progressLayout;     	
	
	private int errorCode = -1;
	
	public SendEmailAsyncTask(Context context,OnSentMailListener listener,String[] toArr, String from, String subject, String body, List<String> attachments,LinearLayout progressBar, LinearLayout progressBarLayout) 
	{
		super();
		this.listener = listener;
		this.progressBarLayout = progressBar;	
		this.progressLayout = progressBarLayout;		
		this.context = context;
		
		m = new GMailSender(EMAIL_USER, EMAIL_PASSWORD);	       
        m.setTo(toArr); 
        m.setFrom(from); 
        m.setSubject(subject); 
        m.setBody(body); 
        if(attachments!=null)
        {
        	for(String file : attachments)
            {
            	 try 
            	 {
    				m.addAttachment(file);
    			} catch (Exception e) 
    			{
    				e.printStackTrace();
    			} 
            }     
        }        
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();
		
		if(progressBarLayout!=null)
			progressBarLayout.setVisibility(View.VISIBLE);
		if(progressLayout!=null)
			progressLayout.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected Boolean doInBackground(Void... params) 
	{
		final ConnectivityManager cm =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) 
        {
			errorCode = SEM_CONEXAO_COM_A_INTERNET;
            return false;
        }else
        {
        	try 
    		{
                m.send();
                return true;
            } catch (AuthenticationFailedException e) 
            {
                Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                return false;
            } catch (MessagingException e) 
            {
                Log.e(SendEmailAsyncTask.class.getName(), m.getTo() + "failed");
                e.printStackTrace();
                return false;
            } catch (Exception e) 
            {
                e.printStackTrace();
                return false;
            }
        }	
	}
	
	@Override
	protected void onPostExecute(Boolean result) 
	{
		if(progressBarLayout!=null)
			progressBarLayout.setVisibility(View.GONE);
		if(progressLayout!=null)
			progressLayout.setVisibility(View.GONE);
		
		if(result!=null&&listener!=null)
		{
			listener.finishedSendingEmail(result,errorCode);
		}
		else
		{
			listener.finishedSendingEmail(false,errorCode);
		}
	}
	
	public interface OnSentMailListener
	{
		public void finishedSendingEmail(Boolean result, int errorCode);		
	}

}
