package org.vocefiscal.activities;

import org.vocefiscal.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FiscalizacaoConcluidaActivity extends Activity 
{

	public static final String TAB_TO_SELECT = "tab_to_select";
	public static final int FISCALIZAR = 0;
	public static final int CONFERIR = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fiscalizacao_concluida);
	}

	/**
	 * Chamada quando o botão Fiscalizar é clicado
	 * @param view
	 */
	public void fiscalizar(View view) 
	{
		
		Intent intent = new Intent(FiscalizacaoConcluidaActivity.this,HomeActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(TAB_TO_SELECT, FISCALIZAR);
		intent.putExtras(bundle);
		startActivity(intent);
		
		finish();
	}

	/**
	 * Chamada quando o botão Conferir é clicado
	 * @param view
	 */
	public void conferir(View view) 
	{
		
		Intent intent = new Intent(FiscalizacaoConcluidaActivity.this,HomeActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(TAB_TO_SELECT, CONFERIR);
		intent.putExtras(bundle);
    	startActivity(intent);
    	
    	finish();
	}
}