/**
 * 
 */
package org.vocefiscal.fragments;

import java.util.ArrayList;

import org.vocefiscal.R;
import org.vocefiscal.adapters.FiscalizacaoAdapter;
import org.vocefiscal.bitmaps.ImageFetcher;
import org.vocefiscal.models.Fiscalizacao;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * @author andre
 *
 */
public class ConferirFragment extends Fragment 
{

	private ImageFetcher imageFetcher;
	

	public ConferirFragment(ImageFetcher imageFetcher) 
	{
		super();
		this.imageFetcher = imageFetcher;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.fragment_conferir, container, false);
		
		FiscalizacaoAdapter fiscalizacaoAdapter = new FiscalizacaoAdapter(getActivity(), imageFetcher);

		ListView listView = (ListView) rootView.findViewById(R.id.listview) ;
		listView.setAdapter(fiscalizacaoAdapter);
		listView.setOnScrollListener(new AbsListView.OnScrollListener() 
		{
			@Override
			public void onScrollStateChanged(AbsListView absListView, int scrollState) 
			{
				// Pause fetcher to ensure smoother scrolling when flinging
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING)
				{
					imageFetcher.setPauseWork(true);
				} else 
				{
					imageFetcher.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
			{
			}
		});
		
		fiscalizacaoAdapter.setResultItemList(fakeResultListItem());
		fiscalizacaoAdapter.notifyDataSetChanged();

		return rootView;
	}

	private ArrayList<Fiscalizacao> fakeResultListItem() 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
