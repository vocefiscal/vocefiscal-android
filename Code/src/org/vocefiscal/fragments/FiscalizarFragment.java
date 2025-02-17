/**
 * 
 */
package org.vocefiscal.fragments;

import org.vocefiscal.R;
import org.vocefiscal.activities.CameraActivity;
import org.vocefiscal.adapters.HomeFlipAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * @author andre
 *
 */

public class FiscalizarFragment extends Fragment 
{
	protected static final long TEMPO_PROXIMA_TENTATIVA = 1000;

	protected static final long TEMPO_PROXIMA_TELA_1 = 7000;

	protected static final long TEMPO_PROXIMA_TELA_2 = 15000;

	protected static final long TEMPO_PROXIMA_TELA_3 = 5000;

	private ImageView primeiraBolinha;   

	private ImageView segundaBolinha;

	private ImageView terceiraBolinha;

	private ImageView quartaBolinha; 

	private Runnable passadorDePassos;

	private ViewPager tutorialHomePager;

	private Handler handler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		handler = new Handler();
		
		passadorDePassos = new Runnable() 
		{			
			@Override
			public void run() 
			{
				if(tutorialHomePager!=null)
				{
					int currentItem = tutorialHomePager.getCurrentItem();
					if(currentItem<3)
						tutorialHomePager.setCurrentItem(currentItem+1);
					else
						tutorialHomePager.setCurrentItem(0);	
				}else
				{
					handler.postDelayed(passadorDePassos, TEMPO_PROXIMA_TENTATIVA);
				}
			}
		};
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) 
	{		
		View rootView = inflater.inflate(R.layout.fragment_fiscalizar, container, false);
		ImageView btnFiscalizar = (ImageView) rootView.findViewById(R.id.btn_fiscalizar);

		 primeiraBolinha  = (ImageView) rootView.findViewById(R.id.primeira_bolinha);      
		 segundaBolinha   = (ImageView) rootView.findViewById(R.id.segunda_bolinha);
		 terceiraBolinha  = (ImageView) rootView.findViewById(R.id.terceira_bolinha); 
		 quartaBolinha    = (ImageView) rootView.findViewById(R.id.quarta_bolinha); 

		//Pager utilizado na passagem de imagens de tutorial da tela Home
		HomeFlipAdapter homeFlipAdapter = new HomeFlipAdapter(getActivity().getSupportFragmentManager());				
		
		tutorialHomePager = (ViewPager) rootView.findViewById(R.id.pagerTour);
		tutorialHomePager.setAdapter(homeFlipAdapter);
		tutorialHomePager.setOnPageChangeListener(new OnPageChangeListener() 
		{

			public void onPageSelected(int position) 
			{
				if(position==0)
				{				
					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					handler.removeCallbacks(passadorDePassos);
					handler.postDelayed(passadorDePassos, TEMPO_PROXIMA_TELA_1);
				}else if(position==1)
				{					
					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					handler.removeCallbacks(passadorDePassos);
					handler.postDelayed(passadorDePassos,  TEMPO_PROXIMA_TELA_2);
				}else if(position==2)
				{					
					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					handler.removeCallbacks(passadorDePassos);
					handler.postDelayed(passadorDePassos,  TEMPO_PROXIMA_TELA_2);
				}else if(position==3)
				{					
					primeiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					segundaBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					terceiraBolinha.setImageResource(R.drawable.ic_bolinha_home_apagada);
					quartaBolinha.setImageResource(R.drawable.ic_bolinha_home);
					handler.removeCallbacks(passadorDePassos);
					handler.postDelayed(passadorDePassos,  TEMPO_PROXIMA_TELA_3);
				}	

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
				// do nothing

			}

			@Override
			public void onPageScrollStateChanged(int arg0) 
			{
				// do nothing

			}
		});

		btnFiscalizar.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(getActivity(), CameraActivity.class);
				startActivity(intent);				
			}
		});

		return rootView;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() 
	{
		super.onResume();
		if(tutorialHomePager!=null)
			tutorialHomePager.setCurrentItem(0);
		handler.postDelayed(passadorDePassos,  TEMPO_PROXIMA_TELA_1);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() 
	{
		super.onPause();
		handler.removeCallbacks(passadorDePassos);
	}
}