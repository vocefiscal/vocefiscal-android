package org.vocefiscal.activities;

import java.util.ArrayList;
import java.util.Collections;

import org.vocefiscal.R;
import org.vocefiscal.adapters.SectionsPagerAdapter;
import org.vocefiscal.bitmaps.ImageCache.ImageCacheParams;
import org.vocefiscal.bitmaps.ImageFetcher;
import org.vocefiscal.database.VoceFiscalDatabase;
import org.vocefiscal.models.Fiscalizacao;
import org.vocefiscal.services.UploadManagerService;
import org.vocefiscal.utils.ImageHandler;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

public class HomeActivity extends ActionBarActivity implements ActionBar.TabListener 
{
	/**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    
    private ImageFetcher conferirFragmentImageFetcher;
    
    private int fotoWidth = -1;

	private int fotoHeight = -1;

	private static final float FOTO_SIZE_REF_WIDTH = 720;

	private static final float FOTO_SIZE_REF_HEIGHT = 218;
	
	private VoceFiscalDatabase voceFiscalDatabase;
	
	private Handler handler;
	
	private ArrayList<Fiscalizacao> listaDeFiscalizacoes;
	
	private Runnable refreshTelaConferir;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        voceFiscalDatabase = new VoceFiscalDatabase(this);
        
        handler = new Handler();
        
        /*
		 * Customização de tamanhos para as diferentes telas dos dispositivos Android
		 */
		Display display = getWindowManager().getDefaultDisplay();			
		int width = display.getWidth();
		int height = display.getHeight();
		float dw = width/720.0f;
		float dh = height/1184.0f;
		float deltaDisplay = Math.max(dw, dh);

		fotoWidth = (int) (FOTO_SIZE_REF_WIDTH*deltaDisplay);	
		fotoHeight = (int) (FOTO_SIZE_REF_HEIGHT*deltaDisplay);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        /* 
		 * ImageFetcher e Cache 
		 */
		ImageCacheParams cacheParams = new ImageCacheParams(this, ImageHandler.IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

		//The ImageFetcher takes care of loading images into our ImageView children asynchronously
		conferirFragmentImageFetcher = new ImageFetcher(ImageFetcher.CARREGAR_DO_DISCO, getApplicationContext(), fotoWidth, fotoHeight);
		conferirFragmentImageFetcher.setLoadingImage(R.drawable.capa_conferir);
		conferirFragmentImageFetcher.addImageCache(cacheParams);
		
		if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
			listaDeFiscalizacoes = voceFiscalDatabase.getFiscalizacoes();
		
		if(listaDeFiscalizacoes!=null)
			Collections.reverse(listaDeFiscalizacoes);
        
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),conferirFragmentImageFetcher,listaDeFiscalizacoes,voceFiscalDatabase);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() 
        {
            @Override
            public void onPageSelected(int position) 
            {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) 
        {
            // Create tabs.  Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
        	
        	Tab tab = actionBar.newTab(); 
        	if(i==0)
        	{
        		tab.setCustomView(R.layout.tab_fiscalizar);        		
        	}        		
        	else
        	{
        		tab.setCustomView(R.layout.tab_conferir);
        	}        		
        	tab.setTabListener(this);
       	
            actionBar.addTab(tab);
        }
        
        refreshTelaConferir = new Runnable() 
        {
			
			@Override
			public void run() 
			{
				ArrayList<Fiscalizacao> novaListaFiscalizacao = null;
				
				if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
					novaListaFiscalizacao = voceFiscalDatabase.getFiscalizacoes();
						
				if(novaListaFiscalizacao!=null)
				{
					Collections.reverse(novaListaFiscalizacao);
					
					if(listaDeFiscalizacoes==null || !novaListaFiscalizacao.equals(listaDeFiscalizacoes))
					{
						listaDeFiscalizacoes = novaListaFiscalizacao;
						mSectionsPagerAdapter.updateListaDeFiscalizacoes(listaDeFiscalizacoes);
					}
				}								
				
				handler.postDelayed(refreshTelaConferir, 1000);
				
			}
		};
		
		Intent intent = new Intent(getApplicationContext(), UploadManagerService.class);
		startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {     
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        // Handle action bar item clicks here. 
        int id = item.getItemId();
        if (id == R.id.sobre) 
        {
        	Intent intent = new Intent(HomeActivity.this,SobreActivity.class);
        	//Intent intent = new Intent(HomeActivity.this,FiscalizacaoConcluidaActivity.class);
        	startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) 
    {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) 
    {
    	//do nothing
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) 
    {
    	//do nothing
    }
    
    /* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() 
	{		
		super.onResume();				

		if(conferirFragmentImageFetcher!=null)
			conferirFragmentImageFetcher.setExitTasksEarly(false);
				
		handler.post(refreshTelaConferir);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() 
	{		
		super.onPause();
		
		handler.removeCallbacks(refreshTelaConferir);

		if(conferirFragmentImageFetcher!=null)
		{
			conferirFragmentImageFetcher.setPauseWork(false);
			conferirFragmentImageFetcher.setExitTasksEarly(true);
			conferirFragmentImageFetcher.flushCache();
		}      
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() 
	{		
		super.onDestroy();

		if(conferirFragmentImageFetcher!=null)
			conferirFragmentImageFetcher.closeCache();
		
		if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
			voceFiscalDatabase.close();
		
		handler.removeCallbacks(refreshTelaConferir);
		
		Intent intent = new Intent(getApplicationContext(), UploadManagerService.class);
		stopService(intent);
	}
	
    @Override
	protected void onNewIntent(Intent intent) 
    {
		super.onNewIntent(intent);
		
		if(intent!=null)
		{
			Bundle bundle = intent.getExtras();
			if(bundle!=null)
			{
				final int tabToSelect = bundle.getInt(FiscalizacaoConcluidaActivity.TAB_TO_SELECT);
				handler.postDelayed(new Runnable() 
				{			
					@Override
					public void run() 
					{
						mViewPager.setCurrentItem(tabToSelect,true);
					}
				}, 500);				
			}
		}
	}
}