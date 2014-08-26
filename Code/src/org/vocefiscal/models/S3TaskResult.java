package org.vocefiscal.models;

import java.net.URL;

import android.net.Uri;


public class S3TaskResult 
{	
	String errorMessage = null;
	Uri uri = null;
	URL urlDaFoto = null;
	Integer idFoto = null;
	String incomingPath = null;
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	/**
	 * @return the uri
	 */
	public Uri getUri() {
		return uri;
	}
	/**
	 * @param uri the uri to set
	 */
	public void setUri(Uri uri) {
		this.uri = uri;
	}
	/**
	 * @return the urlDaFoto
	 */
	public URL getUrlDaFoto() {
		return urlDaFoto;
	}
	/**
	 * @param urlDaFoto the urlDaFoto to set
	 */
	public void setUrlDaFoto(URL urlDaFoto) {
		this.urlDaFoto = urlDaFoto;
	}
	/**
	 * @return the incomingPath
	 */
	public String getIncomingPath() {
		return incomingPath;
	}
	/**
	 * @param incomingPath the incomingPath to set
	 */
	public void setIncomingPath(String incomingPath) {
		this.incomingPath = incomingPath;
	}
	/**
	 * @return the idFoto
	 */
	public Integer getIdFoto() {
		return idFoto;
	}
	/**
	 * @param idFoto the idFoto to set
	 */
	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}
}
