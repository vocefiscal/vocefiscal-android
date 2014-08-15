package org.vocefiscal.dialogs;


import org.vocefiscal.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener 
{
	public Context c;
	public Dialog d;
	public Button yes, no;
	private String titulo;
	private String pergunta;
	private String titulopositiveBtn;
	private String titulonegativeBtn;
	private BtnsControl btnsControl;
	
	private TextView tituloTV;
	private TextView perguntaTV;
	
	public CustomDialogClass(Context context, String titulo, String pergunta) 
	{
		super(context);
		this.titulo=titulo;
		this.pergunta = pergunta;
		this.c = context;
	}	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_dialog);
		yes = (Button) findViewById(R.id.positiveBtn);
		no = (Button) findViewById(R.id.negativeBtn);
		
		//Typeface myTypeface = Typeface.createFromAsset(c.getAssets(),"proximanova_regular.otf");	
		tituloTV = (TextView)findViewById(R.id.titulo);
		//tituloTV.setTypeface(myTypeface);
		perguntaTV = (TextView)findViewById(R.id.pergunta);
		//perguntaTV.setTypeface(myTypeface);
		
		//yes.setTypeface(myTypeface);
		//no.setTypeface(myTypeface);
		
		if(this.titulo!=null)
			tituloTV.setText(this.titulo);
		if(this.pergunta!=null)
			perguntaTV.setText(this.pergunta);
		if(this.titulopositiveBtn!=null)
			yes.setText(titulopositiveBtn);
		if(this.titulonegativeBtn!=null)
			no.setText(titulonegativeBtn);

		yes.setOnClickListener(this);
		no.setOnClickListener(this);

	}

	public void setBtnsControl(BtnsControl btnsControl, String tituloPositiveBtn, String tituloNegativeBtn) 
	{
		this.btnsControl = btnsControl;
		this.titulopositiveBtn=tituloPositiveBtn;
		this.titulonegativeBtn = tituloNegativeBtn;
		
		if(yes!=null)
			yes.setVisibility(View.VISIBLE);
		if(no!=null)
			no.setVisibility(View.VISIBLE);
		
		if(this.titulopositiveBtn!=null&&yes!=null)
			yes.setText(titulopositiveBtn);
		if(this.titulonegativeBtn!=null&&no!=null)
			no.setText(titulonegativeBtn);
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
		case R.id.positiveBtn:
		{
			if(btnsControl!=null)
				btnsControl.positiveBtnClicked();
			dismiss();


			break;
		}
		case R.id.negativeBtn:
		{
			if(btnsControl!=null)
				btnsControl.negativeBtnClicked();
			dismiss();
			break;
		}
		default:
			break;
		}
		dismiss();
	}

	public void negativeButtonGone()
	{
		if(no!=null)
			no.setVisibility(View.GONE);
	}
	
	public interface BtnsControl
	{
		public void positiveBtnClicked();
		public void negativeBtnClicked();

	}
	
	
	
	public void setTitulo(String titulo) 
	{
		if(titulo!=null)
		{
			this.titulo = titulo;
			if(tituloTV!=null)
				tituloTV.setText(this.titulo);
		}
	}
	
	public void setPergunta(String pergunta) 
	{
		if(pergunta!=null)
		{
			this.pergunta = pergunta;
			if(perguntaTV!=null)
				perguntaTV.setText(this.pergunta);
		}
	}
}


