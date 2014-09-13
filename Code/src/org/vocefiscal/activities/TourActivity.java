package org.vocefiscal.activities;

import org.vocefiscal.R;
import org.vocefiscal.adapters.TourFlipAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class TourActivity extends FragmentActivity 
{
	private ImageView primeiraBolinha;   

	private ImageView segundaBolinha;

	private ImageView terceiraBolinha;

	private ImageView quartaBolinha; 

	private ImageView quintaBolinha; 

	private ImageView sextaBolinha; 

	private ImageView setimaBolinha; 

	private ImageView oitavaBolinha; 	

	private ViewPager tourPager;
	
	private ImageView comecar;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tour);

		primeiraBolinha  = (ImageView) findViewById(R.id.primeira_bolinha);      
		segundaBolinha   = (ImageView) findViewById(R.id.segunda_bolinha);
		terceiraBolinha  = (ImageView) findViewById(R.id.terceira_bolinha); 
		quartaBolinha    = (ImageView) findViewById(R.id.quarta_bolinha); 
		quintaBolinha    = (ImageView) findViewById(R.id.quinta_bolinha);      
		sextaBolinha     = (ImageView) findViewById(R.id.sexta_bolinha);
		setimaBolinha    = (ImageView) findViewById(R.id.setima_bolinha); 
		oitavaBolinha    = (ImageView) findViewById(R.id.oitava_bolinha); 
		
		comecar = (ImageView)findViewById(R.id.btn_comecar);
		comecar.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{				
				Intent mainIntent = new Intent(TourActivity.this,HomeActivity.class);
				startActivity(mainIntent);
				finish();						
			}
		});		

		//Pager utilizado na passagem de imagens de 
		//Tour na primeira vez que entra no app

		TourFlipAdapter tourFlipAdapter = new TourFlipAdapter(getSupportFragmentManager());
		tourPager = (ViewPager) findViewById(R.id.pager);
		tourPager.setAdapter(tourFlipAdapter);

		tourPager.setOnPageChangeListener(new OnPageChangeListener() 
		{

			public void onPageSelected(int position) 
			{
				if(position==0)
				{
					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quintaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);      
					sextaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					setimaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					oitavaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					
					primeiraBolinha.setVisibility(View.VISIBLE);
					segundaBolinha.setVisibility(View.VISIBLE);
					terceiraBolinha.setVisibility(View.VISIBLE);
					quartaBolinha.setVisibility(View.VISIBLE);
					quintaBolinha.setVisibility(View.VISIBLE);
					sextaBolinha.setVisibility(View.VISIBLE);
					setimaBolinha.setVisibility(View.VISIBLE);
					oitavaBolinha.setVisibility(View.VISIBLE);
					
					comecar.setVisibility(View.GONE);
				}else if(position==1)
				{

					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quintaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);      
					sextaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					setimaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					oitavaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					
					primeiraBolinha.setVisibility(View.VISIBLE);
					segundaBolinha.setVisibility(View.VISIBLE);
					terceiraBolinha.setVisibility(View.VISIBLE);
					quartaBolinha.setVisibility(View.VISIBLE);
					quintaBolinha.setVisibility(View.VISIBLE);
					sextaBolinha.setVisibility(View.VISIBLE);
					setimaBolinha.setVisibility(View.VISIBLE);
					oitavaBolinha.setVisibility(View.VISIBLE);
					
					comecar.setVisibility(View.GONE);
				}else if(position==2)
				{

					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quintaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);      
					sextaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					setimaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada); 
					oitavaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					
					primeiraBolinha.setVisibility(View.VISIBLE);
					segundaBolinha.setVisibility(View.VISIBLE);
					terceiraBolinha.setVisibility(View.VISIBLE);
					quartaBolinha.setVisibility(View.VISIBLE);
					quintaBolinha.setVisibility(View.VISIBLE);
					sextaBolinha.setVisibility(View.VISIBLE);
					setimaBolinha.setVisibility(View.VISIBLE);
					oitavaBolinha.setVisibility(View.VISIBLE);
					
					comecar.setVisibility(View.GONE);
				}else if(position==3)
				{

					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home);
					quintaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);      
					sextaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					setimaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada); 
					oitavaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada); 
					
					primeiraBolinha.setVisibility(View.VISIBLE);
					segundaBolinha.setVisibility(View.VISIBLE);
					terceiraBolinha.setVisibility(View.VISIBLE);
					quartaBolinha.setVisibility(View.VISIBLE);
					quintaBolinha.setVisibility(View.VISIBLE);
					sextaBolinha.setVisibility(View.VISIBLE);
					setimaBolinha.setVisibility(View.VISIBLE);
					oitavaBolinha.setVisibility(View.VISIBLE);
					
					comecar.setVisibility(View.GONE);
				}else if(position==4)
				{

					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quintaBolinha.setImageResource(R.drawable.ic_bolinha_home);    
					sextaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					setimaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada); 
					oitavaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					
					primeiraBolinha.setVisibility(View.VISIBLE);
					segundaBolinha.setVisibility(View.VISIBLE);
					terceiraBolinha.setVisibility(View.VISIBLE);
					quartaBolinha.setVisibility(View.VISIBLE);
					quintaBolinha.setVisibility(View.VISIBLE);
					sextaBolinha.setVisibility(View.VISIBLE);
					setimaBolinha.setVisibility(View.VISIBLE);
					oitavaBolinha.setVisibility(View.VISIBLE);
					
					comecar.setVisibility(View.GONE);
				}else if(position==5)
				{

					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quintaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);      
					sextaBolinha.setImageResource(R.drawable.ic_bolinha_home);
					setimaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada); 
					oitavaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					
					primeiraBolinha.setVisibility(View.VISIBLE);
					segundaBolinha.setVisibility(View.VISIBLE);
					terceiraBolinha.setVisibility(View.VISIBLE);
					quartaBolinha.setVisibility(View.VISIBLE);
					quintaBolinha.setVisibility(View.VISIBLE);
					sextaBolinha.setVisibility(View.VISIBLE);
					setimaBolinha.setVisibility(View.VISIBLE);
					oitavaBolinha.setVisibility(View.VISIBLE);
					
					comecar.setVisibility(View.GONE);
				}else if(position==6)
				{

					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quintaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);      
					sextaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					setimaBolinha.setImageResource(R.drawable.ic_bolinha_home);
					oitavaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada); 
					
					primeiraBolinha.setVisibility(View.VISIBLE);
					segundaBolinha.setVisibility(View.VISIBLE);
					terceiraBolinha.setVisibility(View.VISIBLE);
					quartaBolinha.setVisibility(View.VISIBLE);
					quintaBolinha.setVisibility(View.VISIBLE);
					sextaBolinha.setVisibility(View.VISIBLE);
					setimaBolinha.setVisibility(View.VISIBLE);
					oitavaBolinha.setVisibility(View.VISIBLE);
					
					comecar.setVisibility(View.GONE);	
				}
				else if(position==7)
				{

					primeiraBolinha.setVisibility(View.GONE);
					segundaBolinha.setVisibility(View.GONE);
					terceiraBolinha.setVisibility(View.GONE);
					quartaBolinha.setVisibility(View.GONE);
					quintaBolinha.setVisibility(View.GONE);
					sextaBolinha.setVisibility(View.GONE);
					setimaBolinha.setVisibility(View.GONE);
					oitavaBolinha.setVisibility(View.GONE);
					
					comecar.setVisibility(View.VISIBLE);				
				}								

			}

			@Override
			public void onPageScrollStateChanged(int arg0) 
			{
				// do nothing
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) 
			{
				// do nothing
			}
		});
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() 
	{
		//do nothing
	}
}
