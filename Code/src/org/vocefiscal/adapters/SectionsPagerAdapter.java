/**
 * 
 */
package org.vocefiscal.adapters;

import java.util.ArrayList;

import org.vocefiscal.bitmaps.ImageFetcher;
import org.vocefiscal.fragments.ConferirFragment;
import org.vocefiscal.fragments.FiscalizarFragment;
import org.vocefiscal.models.Fiscalizacao;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author andre
 *
 */
/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter 
{

	private ArrayList<Fiscalizacao> listaDeFiscalizacoes;
	
	private ImageFetcher conferirImageFetcher;
	
	private FiscalizarFragment fiscalizarFragment;
	
	private ConferirFragment conferirFragment;
	
    public SectionsPagerAdapter(FragmentManager fm,ImageFetcher conferirImageFetcher,ArrayList<Fiscalizacao> listaDeFiscalizacoes) 
    {
        super(fm);
        this.conferirImageFetcher = conferirImageFetcher;
        this.listaDeFiscalizacoes = listaDeFiscalizacoes;
    }

    @Override
    public Fragment getItem(int position) 
    {
        // getItem is called to instantiate the fragment for the given page.
    	if(position==0)
    	{
    		fiscalizarFragment = new FiscalizarFragment();
    		return fiscalizarFragment;
    	}else if(position==1)
    	{
    		conferirFragment = new ConferirFragment(conferirImageFetcher,listaDeFiscalizacoes);
    		return conferirFragment;
    	}else
    	{
    		return null;
    	}
    }

    @Override
    public int getCount() 
    {
        // Show 2 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) 
    {        
        switch (position) 
        {
            case 0:
                return "Fiscalizar";
            case 1:
                return "Conferir";
        }
        
        return null;
    }

	public void updateListaDeFiscalizacoes(ArrayList<Fiscalizacao> listaDeFiscalizacoes) 
	{
		 this.listaDeFiscalizacoes = listaDeFiscalizacoes;
		 conferirFragment.updateListaDeFiscalizacoes(listaDeFiscalizacoes); 		
	}
}