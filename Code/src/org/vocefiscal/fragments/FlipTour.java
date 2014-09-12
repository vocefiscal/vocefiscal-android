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
public class FlipTour extends Fragment 
{
	private int position = -1;	

	public FlipTour(int position) 
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
			imageFlip.setImageResource(R.drawable.tour_1);
		}else if(position==1)
		{
			imageFlip.setImageResource(R.drawable.tour_2);
		}else if(position==2)
		{
			imageFlip.setImageResource(R.drawable.tour_3);
		}else if(position==3)
		{
		imageFlip.setImageResource(R.drawable.tour_4);
		}
		else if(position==4)
		{
		imageFlip.setImageResource(R.drawable.tour_5);
		}
		else if(position==5)
		{
		imageFlip.setImageResource(R.drawable.tour_6);
		}
		else if(position==6)
		{
		imageFlip.setImageResource(R.drawable.tour_7);
		}
		else if(position==7)
		{
		imageFlip.setImageResource(R.drawable.tour_8);
		}
		
		
		
		
		
		

	return rootView;
}
}

