/**
 * 
 */
package org.vocefiscal.models.enums;

import org.vocefiscal.R;

import android.hardware.Camera;

/**
 * @author andre
 *
 */
public enum FlashModeEnum 
{
	AUTO,OFF,ON,TORCH;

	public static String getFlashMode(int flashStatus) 
	{
		String flashMode = null;
		
		if(flashStatus==0)
		{
			flashMode = Camera.Parameters.FLASH_MODE_AUTO;
		}else if(flashStatus==1)
		{
			flashMode = Camera.Parameters.FLASH_MODE_OFF;
		}else if(flashStatus==2)
		{
			flashMode = Camera.Parameters.FLASH_MODE_ON;
		}else if(flashStatus==3)
		{
			flashMode = Camera.Parameters.FLASH_MODE_TORCH;
		}
		
		return flashMode;
	}

	public static int nextFlashMode(int flashStatus) 
	{
		flashStatus++;
		
		if(flashStatus>=FlashModeEnum.values().length)
			flashStatus = 0;
		
		return flashStatus;
	}

	public static int getImageResource(int flashStatus) 
	{
		int imageResource = R.drawable.flash_auto;
		
		if(flashStatus==0)
		{
			imageResource = R.drawable.flash_auto;
		}else if(flashStatus==1)
		{
			imageResource = R.drawable.flash_off;
		}else if(flashStatus==2)
		{
			imageResource = R.drawable.flash_on;
		}else if(flashStatus==3)
		{
			imageResource = R.drawable.flash_tocha;
		}
		
		return imageResource;
	}
	
	
}
