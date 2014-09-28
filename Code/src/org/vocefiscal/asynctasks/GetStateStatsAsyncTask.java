/**
 * 
 */
package org.vocefiscal.asynctasks;

import org.vocefiscal.communications.CommunicationConstants;
import org.vocefiscal.communications.CommunicationUtils;
import org.vocefiscal.communications.JsonHandler;
import org.vocefiscal.communications.RequestMethod;
import org.vocefiscal.communications.RestClient;
import org.vocefiscal.models.StateStats;
import org.vocefiscal.utils.Municipalities;

import android.content.Context;

/**
 * @author andre
 *
 */
public class GetStateStatsAsyncTask extends AsyncTask<Object, Object, StateStats> 
{
	private OnGetStateStatsPostExecuteListener<Object> listener;
	private Context context;
	private String errorMsg="";
	private int errorCode=-1;
	private Integer sleep=-1;
	private String stateCode;
	
	private Municipalities municipalities;
	
	public GetStateStatsAsyncTask(Context context,OnGetStateStatsPostExecuteListener<Object> listener,String stateCode,Integer sleep)
	{
		this.context = context;
		this.listener = listener;
		this.stateCode = stateCode;
		this.sleep = sleep;
		
		municipalities = Municipalities.getInstance(context);
	}
	
	@Override
	protected StateStats doInBackground(Object... params) 
	{
		StateStats serverResponse=null;
		boolean ret = CommunicationUtils.verifyConnectivity(context);

		if(ret)
		{	
			try
			{				
				RestClient restClient = new RestClient(CommunicationConstants.STATE_STATS_BASE_ADDRESS,municipalities.getStateSlug(stateCode)+"/stats.json",context);
				restClient.addHeader(CommunicationConstants.CONTENTTYPE_PARAM, CommunicationConstants.CONTENTTYPE_JSON);
				
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

					restClient.execute(RequestMethod.GET);
					
					if(!isCancelled())
					{
						int responseCode = restClient.getResponseCode();

						if (responseCode == CommunicationConstants.OK_WITHCONTENT) 
						{
							JsonHandler jsonHandler = new JsonHandler();
							String response = restClient.getResponse();

							serverResponse = (StateStats) jsonHandler.fromJsonDataToObject(StateStats.class, response);

							if (serverResponse == null) 
							{
								errorCode = CommunicationConstants.JSON_PARSE_ERROR;
								errorMsg = "Problema ao acessar o servidor.";
							}else
							{
								serverResponse.setStateCode(stateCode);
							}
						}else if(responseCode == CommunicationConstants.TIME_OUT)
						{
							errorCode = CommunicationConstants.TIME_OUT;
							errorMsg = "Sua conexão está ruim";
						}else if(responseCode == CommunicationConstants.FORBIDDEN)
						{
							errorCode = CommunicationConstants.FORBIDDEN;
							errorMsg = restClient.getResponse();
						}else
						{
							errorCode = -11;
							errorMsg = "Sua conexão está ruim";
						}
					}else
					{
						errorCode = CommunicationConstants.CANCELED;
					}				
				}else
				{
					errorCode = CommunicationConstants.CANCELED;
				}
			}catch (Exception ex) 
			{  
				ex.printStackTrace();
				errorCode = -12;
				errorMsg = "Sua conexão está ruim";
			}				
		}else
		{
			errorCode = CommunicationConstants.SEM_INTERNET;
			errorMsg = "Sem conexão com a Internet!";
		}
		
		return serverResponse;
	}

	@Override
	protected void onPostExecute(StateStats result) 
	{				
		if(result!=null)
		{			
			listener.finishedGetStateStatsComResultado(result);
		}			
		else if(errorCode!=CommunicationConstants.CANCELED)
		{			
			listener.finishedGetStateStatsComError(errorCode,errorMsg,stateCode);			
		}
	}

	public interface OnGetStateStatsPostExecuteListener<K>
	{
		public void finishedGetStateStatsComResultado(StateStats stateStats);
		public void finishedGetStateStatsComError(int errorCode, String error, String stateCode);
	}
}
