/**
 * 
 */
package org.vocefiscal.models.enums;

import org.vocefiscal.R;

/**
 * @author andre
 *
 */
public enum StatusEnvioEnum 
{
	ENVIAR,ENVIANDO,ENVIADO;

	public static int getImageResource(Integer statusDoEnvio) 
	{
		int imageResource = -1;
		
		if(statusDoEnvio!=null)
		{
			if(statusDoEnvio.equals(ENVIAR.ordinal()))
			{
				imageResource = R.drawable.ic_enviar;
			}else if(statusDoEnvio.equals(ENVIANDO.ordinal()))
			{
				imageResource = R.drawable.ic_pausar;
			}else if(statusDoEnvio.equals(ENVIADO.ordinal()))
			{
				imageResource = R.drawable.ic_enviado;
			}else
			{
				imageResource = R.drawable.ic_enviar;
			}
		}
		
		return imageResource;
	}
}
