/**
 * 
 */
package org.vocefiscal.adapters;

import org.vocefiscal.fragments.FlipTourFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author andre
 *
 */
public class TourFlipAdapter extends FragmentPagerAdapter 
{

	public TourFlipAdapter(FragmentManager fm) 
	{
		super(fm);	
	}

	@Override
	public Fragment getItem(int position) 
	{
		Fragment fragment = new FlipTourFragment(position);		
		return fragment;
	}

	@Override
	public int getCount() 
	{		
		return 8;
	}
    
}
