/**
 * 
 */
package org.vocefiscal.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import org.vocefiscal.fragments.FlipHomeFragment;

/**
 * @author andre
 *
 */
public class HomeFlipAdapter extends FragmentPagerAdapter 
{

	public HomeFlipAdapter(FragmentManager fm) 
	{
		super(fm);	
	}

	@Override
	public Fragment getItem(int position) 
	{
		Fragment fragment = new FlipHomeFragment(position);		
		return fragment;
	}

	@Override
	public int getCount() 
	{		
		return 4;
	}
    
}
