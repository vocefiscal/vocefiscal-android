/**
 * 
 */
package org.vocefiscal.activities;

import org.vocefiscal.R;
import org.vocefiscal.asynctasks.AsyncTask;
import org.vocefiscal.asynctasks.GetStateStatsAsyncTask;
import org.vocefiscal.asynctasks.GetStateStatsAsyncTask.OnGetStateStatsPostExecuteListener;
import org.vocefiscal.communications.CommunicationConstants;
import org.vocefiscal.models.StateStats;
import org.vocefiscal.models.enums.BrazilStateCodesEnum;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


/**
 * @author fredveloso
 *
 */
public class SobreActivity extends AnalyticsActivity implements OnGetStateStatsPostExecuteListener<Object>
{
	private int backoffStats = 0;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sobre);
		
		//GetStateStatsAsyncTask getStateStatsAsyncTask = new GetStateStatsAsyncTask(getApplicationContext(), this, "SP", 0);
		GetStateStatsAsyncTask getStateStatsAsyncTask = new GetStateStatsAsyncTask(getApplicationContext(), this, BrazilStateCodesEnum.getStateCode("Sao Paulo"), 0);
		getStateStatsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() 
	{
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() 
	{
		super.onResume();
	}
	
	/**
	 * Chamada quando o botão de voltar da activity Sobre é clicado
	 * @param view
	 */
	public void voltar(View view) 
	{
	    finish();
	}
	
	/**
	 * Chamado para expandir a foto da tela Sobre
	 * @param view
	 */
	public void expandirFoto (View v)
	{
		Intent intent = new Intent(SobreActivity.this,SobreFullScreenActivity.class);
		startActivity(intent);
	}

	@Override
	public void finishedGetStateStatsComResultado(StateStats stateStats) 
	{
		backoffStats = 0;
		
		//Do whatever you need with the stats
		Toast.makeText(getApplicationContext(), stateStats.getStateCode()+": "+stateStats.getPollTapesCount(), Toast.LENGTH_LONG).show();
		
	}

	@Override
	public void finishedGetStateStatsComError(int errorCode, String error, String stateCode) 
	{
		backoffStats++;
		
		GetStateStatsAsyncTask getStateStatsAsyncTask = new GetStateStatsAsyncTask(getApplicationContext(), this, stateCode, CommunicationConstants.WAIT_RETRY*backoffStats);
		getStateStatsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	}
}
