package org.vocefiscal.activities;

import org.vocefiscal.R;
import org.vocefiscal.adapters.HomeFlipAdapter;
import org.vocefiscal.adapters.TourFlipAdapter;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class TourActivity extends ActionBarActivity {


	private ImageView primeiraBolinha;   

	private ImageView segundaBolinha;

	private ImageView terceiraBolinha;

	private ImageView quartaBolinha; 

	private ImageView quintaBolinha; 

	private ImageView sextaBolinha; 

	private ImageView setimaBolinha; 

	private ImageView oitavaBolinha; 

	private ImageView telaTour;
	
	private Handler handler;	

	private Runnable passadorDePassos;

	private ViewPager tourPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tour);

		handler = new Handler();

		 //telaTour         = (ImageView) findViewById(R.id.tutorial_home);
		 primeiraBolinha  = (ImageView) findViewById(R.id.primeira_bolinha);      
		 segundaBolinha   = (ImageView) findViewById(R.id.segunda_bolinha);
		 terceiraBolinha  = (ImageView) findViewById(R.id.terceira_bolinha); 
		 quartaBolinha    = (ImageView) findViewById(R.id.quarta_bolinha); 
		 quintaBolinha    = (ImageView) findViewById(R.id.quinta_bolinha);      
		 sextaBolinha     = (ImageView) findViewById(R.id.sexta_bolinha);
		 setimaBolinha    = (ImageView) findViewById(R.id.setima_bolinha); 
		// oitavaBolinha    = (ImageView) findViewById(R.id.oitava_bolinha); 

		//Pager utilizado na passagem de imagens de 
		//Tour na primeira vez que entra no app
		
		TourFlipAdapter tourFlipAdapter = new TourFlipAdapter(getSupportFragmentManager());
		tourPager = (ViewPager) findViewById(R.id.pagerTour);
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
					handler.removeCallbacks(passadorDePassos);
					handler.postDelayed(passadorDePassos, 5000);
					
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
					handler.removeCallbacks(passadorDePassos);
					handler.postDelayed(passadorDePassos, 5000);
					
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
					handler.removeCallbacks(passadorDePassos);
					handler.postDelayed(passadorDePassos, 5000);
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
					handler.removeCallbacks(passadorDePassos);
					handler.postDelayed(passadorDePassos, 5000);
					
				}else if(position==4)
				{

					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home);
					quintaBolinha.setImageResource(R.drawable.ic_bolinha_home);    
					sextaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					setimaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada); 
					oitavaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					handler.removeCallbacks(passadorDePassos);
					handler.postDelayed(passadorDePassos, 5000);
					
				}else if(position==5)
				{

					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home);
					quintaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);      
					sextaBolinha.setImageResource(R.drawable.ic_bolinha_home);
					setimaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada); 
					oitavaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					handler.removeCallbacks(passadorDePassos);
					handler.postDelayed(passadorDePassos, 5000);
					
				}else if(position==6)
				{

					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home);
					quintaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);      
					sextaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					setimaBolinha.setImageResource(R.drawable.ic_bolinha_home);
					oitavaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada); 
					handler.removeCallbacks(passadorDePassos);
					handler.postDelayed(passadorDePassos, 5000);
				}
				else if(position==7)
				{

					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home);
					quintaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);      
					sextaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					setimaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					 oitavaBolinha.setImageResource(R.drawable.ic_bolinha_home);
					handler.removeCallbacks(passadorDePassos);
					handler.postDelayed(passadorDePassos, 5000);
				}

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
		});
	}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.tour, menu);
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// Handle action bar item clicks here. The action bar will
			// automatically handle clicks on the Home/Up button, so long
			// as you specify a parent activity in AndroidManifest.xml.
			int id = item.getItemId();
			if (id == R.id.action_settings) {
				return true;
			}
			return super.onOptionsItemSelected(item);
		}
	}
