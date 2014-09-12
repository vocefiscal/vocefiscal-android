/**
 * 
 */
package org.vocefiscal.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.vocefiscal.R;
import org.vocefiscal.communications.JsonHandler;
import org.vocefiscal.models.Country;
import org.vocefiscal.models.Municipality;
import org.vocefiscal.models.State;

import android.content.Context;

/**
 * @author andre
 *
 */
public class Municipalities 
{
	private static Municipalities instance = null;
	
	private Country country;
	
	private ArrayList<String> nomesEstados;
	
	private HashMap<String, ArrayList<String>> nomesMunicipiosPorEstado;
	
	private Municipalities(Context context) 
	{
		String municipiosJSON = null;
		
		InputStream inputStream = context.getResources().openRawResource(R.raw.municipios);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int ctr;
		try 
		{
		    ctr = inputStream.read();
		    while (ctr != -1) 
		    {
		        byteArrayOutputStream.write(ctr);
		        ctr = inputStream.read();
		    }
		    inputStream.close();
		    
		    municipiosJSON = byteArrayOutputStream.toString();
		    
		    byteArrayOutputStream.close();
		} catch (IOException e) 
		{
		    e.printStackTrace();
		}
		
		if(municipiosJSON!=null)
		{
			try
			{
				JsonHandler jsonHandler = new JsonHandler();			
				country = (Country) jsonHandler.fromJsonDataToObject(Country.class, municipiosJSON);
			}catch(Exception e)
			{
				
			}		
		}
		
		if(country!=null)
		{
			nomesEstados = new ArrayList<String>();
			
			nomesMunicipiosPorEstado = new HashMap<String, ArrayList<String>>();
			
			ArrayList<State> states = country.getStates();
			
			if(states!=null)
			{
				for(State state : states)
				{
					nomesEstados.add(state.getName());
					ArrayList<Municipality> municipalities = state.getMunicipalities();
					if(municipalities!=null)
					{
						ArrayList<String> nomesMunicipalities = new ArrayList<String>();
						for(Municipality municipality : municipalities)
						{
							nomesMunicipalities.add(municipality.getMunicipalityName());
						}
						Collections.sort(nomesMunicipalities);
						nomesMunicipiosPorEstado.put(state.getName(), nomesMunicipalities);
					}
				}
			}
						
		}
	}

	// Esta classe eh um Singleton!
	public static Municipalities getInstance(Context context) 
	{
		if (instance == null) 
		{
			instance = new Municipalities(context);
		}
		return instance;
	}

	/**
	 * @return the country
	 */
	public Country getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * @return the nomesEstados
	 */
	public ArrayList<String> getNomesEstados() {
		return nomesEstados;
	}

	/**
	 * @param nomesEstados the nomesEstados to set
	 */
	public void setNomesEstados(ArrayList<String> nomesEstados) {
		this.nomesEstados = nomesEstados;
	}

	/**
	 * @return the nomesMunicipiosPorEstado
	 */
	public HashMap<String, ArrayList<String>> getNomesMunicipiosPorEstado() {
		return nomesMunicipiosPorEstado;
	}

	/**
	 * @param nomesMunicipiosPorEstado the nomesMunicipiosPorEstado to set
	 */
	public void setNomesMunicipiosPorEstado(
			HashMap<String, ArrayList<String>> nomesMunicipiosPorEstado) {
		this.nomesMunicipiosPorEstado = nomesMunicipiosPorEstado;
	}

	public String getSlug(String estado, String municipio) 
	{
		String slug = null;
		
		if(estado!=null&&municipio!=null)
		{
			if(country!=null)
			{
				ArrayList<State> states = country.getStates();
				
				if(states!=null)
				{
					for(State state : states)
					{
						if(state.getName().equalsIgnoreCase(estado))
						{
							ArrayList<Municipality> municipalities = state.getMunicipalities();
							if(municipalities!=null)
							{
								for(Municipality municipality : municipalities)
								{
									if(municipality.getMunicipalityName().equalsIgnoreCase(municipio))
									{
										slug = municipality.getSlug();
										break;
									}
								}
							}
						}						
					}
				}
							
			}
		}
		
		return slug;
	}
}
