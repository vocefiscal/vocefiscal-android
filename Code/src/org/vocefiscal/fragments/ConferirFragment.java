/**
 * 
 */
package org.vocefiscal.fragments;

import org.vocefiscal.R;
import org.vocefiscal.activities.CameraActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * @author andre
 *
 */
public class ConferirFragment extends Fragment 
{

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		 View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		 ImageView btnFiscalizar = (ImageView) rootView.findViewById(R.id.btn_fiscalizar);
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

}
