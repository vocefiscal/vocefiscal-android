/**
 * 
 */
package org.vocefiscal.fragments;

import org.vocefiscal.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author andre
 *
 */
public class FlipFragment extends Fragment 
{
	private int position = -1;	

	public FlipFragment(int position) 
	{
		super();
		this.position = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.fragment_flip, container, false);

		ImageView imageFlip = (ImageView) rootView.findViewById(R.id.imageFlip);

		if(position==0)
		{
			imageFlip.setImageResource(R.drawable.tutorial_home_1);
		}else if(position==1)
		{
			imageFlip.setImageResource(R.drawable.tutorial_home_2);
		}else if(position==2)
		{
			imageFlip.setImageResource(R.drawable.tutorial_home_3);
		}else if(position==3)
		{
		imageFlip.setImageResource(R.drawable.tutorial_home_4);
		}

	return rootView;
}
}

