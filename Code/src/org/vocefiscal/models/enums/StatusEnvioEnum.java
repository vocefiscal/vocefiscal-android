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
	PAUSADO,ENVIANDO,ENVIADO_PICTURES,ENVIADO_TOTAL;

	public static int getImageResource(Integer statusDoEnvio) 
	{
		int imageResource = -1;
		
		if(statusDoEnvio!=null)
		{
			if(statusDoEnvio.equals(PAUSADO.ordinal()))
			{
				imageResource = R.drawable.ic_enviar;
			}else if(statusDoEnvio.equals(ENVIANDO.ordinal()))
			{
				imageResource = R.drawable.ic_pausar;
			}else if(statusDoEnvio.equals(ENVIADO_PICTURES.ordinal()))
			{
				imageResource = R.drawable.ic_pausar;
			}else if(statusDoEnvio.equals(ENVIADO_TOTAL.ordinal()))
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
