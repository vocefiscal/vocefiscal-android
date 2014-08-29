/**
 * 
 */
package org.vocefiscal.adapters;

import org.vocefiscal.bitmaps.ImageFetcher;
import org.vocefiscal.fragments.ConferirFragment;
import org.vocefiscal.fragments.FiscalizarFragment;

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

	private ImageFetcher conferirImageFetcher;
	
    public SectionsPagerAdapter(FragmentManager fm,ImageFetcher conferirImageFetcher) 
    {
        super(fm);
        this.conferirImageFetcher = conferirImageFetcher;
    }

    @Override
    public Fragment getItem(int position) 
    {
    	Fragment fragment = null;
        // getItem is called to instantiate the fragment for the given page.
    	if(position==0)
    	{
    		fragment = new FiscalizarFragment();
    	}else if(position==1)
    	{
    		fragment = new ConferirFragment(conferirImageFetcher);
    	}
    	
        return fragment;
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
}