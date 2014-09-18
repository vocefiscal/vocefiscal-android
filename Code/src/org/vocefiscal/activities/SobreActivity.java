/**
 * 
 */
package org.vocefiscal.activities;

import org.vocefiscal.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * @author fredveloso
 *
 */
public class SobreActivity extends AnalyticsActivity 
{
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sobre);

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

	
}
