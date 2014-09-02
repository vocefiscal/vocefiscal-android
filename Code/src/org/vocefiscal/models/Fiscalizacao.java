/**
 * 
 */
package org.vocefiscal.models;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author andre
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class Fiscalizacao implements Serializable
{
	private static final long serialVersionUID = 2048054520603455140L;

	private String municipio;
	
	private String estado;
	
	private String zonaEleitoral;
	
	private String localDaVotacao;
	
	private String secaoEleitoral;
	
	private Integer podeEnviar3G;
	
	private Integer statusDoEnvio;

	private ArrayList<String> picturePathList;

	private ArrayList<String> picture30PCPathList;
	
	private ArrayList<String> pictureURLList;

	/**
	 * @return the municipio
	 */
	public String getMunicipio() {
		return municipio;
	}

	/**
	 * @param municipio the municipio to set
	 */
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	/**
	 * @return the estado
	 */
	public String getEstado() {
		return estado;
	}

	/**
	 * @param estado the estado to set
	 */
	public void setEstado(String estado) {
		this.estado = estado;
	}

	/**
	 * @return the zonaEleitoral
	 */
	public String getZonaEleitoral() {
		return zonaEleitoral;
	}

	/**
	 * @param zonaEleitoral the zonaEleitoral to set
	 */
	public void setZonaEleitoral(String zonaEleitoral) {
		this.zonaEleitoral = zonaEleitoral;
	}

	/**
	 * @return the localDaVotacao
	 */
	public String getLocalDaVotacao() {
		return localDaVotacao;
	}

	/**
	 * @param localDaVotacao the localDaVotacao to set
	 */
	public void setLocalDaVotacao(String localDaVotacao) {
		this.localDaVotacao = localDaVotacao;
	}

	/**
	 * @return the secaoEleitoral
	 */
	public String getSecaoEleitoral() {
		return secaoEleitoral;
	}

	/**
	 * @param secaoEleitoral the secaoEleitoral to set
	 */
	public void setSecaoEleitoral(String secaoEleitoral) {
		this.secaoEleitoral = secaoEleitoral;
	}

	/**
	 * @return the podeEnviar3G
	 */
	public Integer getPodeEnviar3G() {
		return podeEnviar3G;
	}

	/**
	 * @param podeEnviar3G the podeEnviar3G to set
	 */
	public void setPodeEnviar3G(Integer podeEnviar3G) {
		this.podeEnviar3G = podeEnviar3G;
	}

	/**
	 * @return the statusDoEnvio
	 */
	public Integer getStatusDoEnvio() {
		return statusDoEnvio;
	}

	/**
	 * @param statusDoEnvio the statusDoEnvio to set
	 */
	public void setStatusDoEnvio(Integer statusDoEnvio) {
		this.statusDoEnvio = statusDoEnvio;
	}

	/**
	 * @return the picturePathList
	 */
	public ArrayList<String> getPicturePathList() {
		return picturePathList;
	}

	/**
	 * @param picturePathList the picturePathList to set
	 */
	public void setPicturePathList(ArrayList<String> picturePathList) {
		this.picturePathList = picturePathList;
	}

	/**
	 * @return the picture30PCPathList
	 */
	public ArrayList<String> getPicture30PCPathList() {
		return picture30PCPathList;
	}

	/**
	 * @param picture30pcPathList the picture30PCPathList to set
	 */
	public void setPicture30PCPathList(ArrayList<String> picture30pcPathList) {
		picture30PCPathList = picture30pcPathList;
	}

	/**
	 * @return the pictureURLList
	 */
	public ArrayList<String> getPictureURLList() {
		return pictureURLList;
	}

	/**
	 * @param pictureURLList the pictureURLList to set
	 */
	public void setPictureURLList(ArrayList<String> pictureURLList) {
		this.pictureURLList = pictureURLList;
	}
}
