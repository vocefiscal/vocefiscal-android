/**
 * 
 */
package org.vocefiscal.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.vocefiscal.R;
import org.vocefiscal.communications.JsonHandler;
import org.vocefiscal.models.Country;

import android.content.Context;

/**
 * @author andre
 *
 */
public class Municipalities 
{
	private static Municipalities instance = null;
	
	private Country country;
	
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
}
