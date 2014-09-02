/**
 * 
 */
package org.vocefiscal.fragments;

import java.util.ArrayList;

import org.vocefiscal.R;
import org.vocefiscal.adapters.FiscalizacaoAdapter;
import org.vocefiscal.bitmaps.ImageFetcher;
import org.vocefiscal.models.Fiscalizacao;
import org.vocefiscal.models.enums.StatusEnvioEnum;

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
		ArrayList<Fiscalizacao> listaDeFiscalizacoes = new ArrayList<Fiscalizacao>();
		
		Fiscalizacao fiscalizacao1 = new Fiscalizacao();
		fiscalizacao1.setMunicipio("Campinas");
		fiscalizacao1.setEstado("SP");
		fiscalizacao1.setZonaEleitoral("0066");
		fiscalizacao1.setLocalDaVotacao("1317");
		fiscalizacao1.setSecaoEleitoral("0319");
		fiscalizacao1.setPodeEnviar3G(0);
		fiscalizacao1.setStatusDoEnvio(StatusEnvioEnum.ENVIADO.ordinal());
		fiscalizacao1.setPicture30PCPathList(new ArrayList<String>());
		fiscalizacao1.setPicturePathList(new ArrayList<String>());
		fiscalizacao1.setPictureURLList(new ArrayList<String>());

		listaDeFiscalizacoes.add(fiscalizacao1);
		
		Fiscalizacao fiscalizacao2 = new Fiscalizacao();
		fiscalizacao2.setMunicipio("São Paulo");
		fiscalizacao2.setEstado("SP");
		fiscalizacao2.setZonaEleitoral("0086");
		fiscalizacao2.setLocalDaVotacao("1318");
		fiscalizacao2.setSecaoEleitoral("0316");
		fiscalizacao2.setPodeEnviar3G(1);
		fiscalizacao2.setStatusDoEnvio(StatusEnvioEnum.ENVIAR.ordinal());
		fiscalizacao2.setPicture30PCPathList(new ArrayList<String>());
		fiscalizacao2.setPicturePathList(new ArrayList<String>());
		fiscalizacao2.setPictureURLList(new ArrayList<String>());

		listaDeFiscalizacoes.add(fiscalizacao2);
		
		Fiscalizacao fiscalizacao3 = new Fiscalizacao();
		fiscalizacao3.setMunicipio("Salvador");
		fiscalizacao3.setEstado("BA");
		fiscalizacao3.setZonaEleitoral("0043");
		fiscalizacao3.setLocalDaVotacao("1654");
		fiscalizacao3.setSecaoEleitoral("0976");
		fiscalizacao3.setPodeEnviar3G(1);
		fiscalizacao3.setStatusDoEnvio(StatusEnvioEnum.ENVIANDO.ordinal());		
		ArrayList<String> listaPicturePathList = new ArrayList<String>();
		listaPicturePathList.add("");
		listaPicturePathList.add("");
		listaPicturePathList.add("");
		listaPicturePathList.add("");
		listaPicturePathList.add("");
		listaPicturePathList.add("");
		listaPicturePathList.add("");
		listaPicturePathList.add("");
		listaPicturePathList.add("");
		listaPicturePathList.add("");
		ArrayList<String> listaPictureURLList = new ArrayList<String>();
		listaPictureURLList.add("");
		listaPictureURLList.add("");
		listaPictureURLList.add("");		
		fiscalizacao3.setPicture30PCPathList(new ArrayList<String>());
		fiscalizacao3.setPicturePathList(listaPicturePathList);
		fiscalizacao3.setPictureURLList(listaPictureURLList);

		listaDeFiscalizacoes.add(fiscalizacao3);
		
		return listaDeFiscalizacoes;
	}

}
