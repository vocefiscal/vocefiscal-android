/**
 * 
 */
package org.vocefiscal.communications;

/**
 * @author andre
 *
 */
public interface MultiPartHandler 
{

	public void handlePart(String stringPart, boolean isFirstPart);
}
