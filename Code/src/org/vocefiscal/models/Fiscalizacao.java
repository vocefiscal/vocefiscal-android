/**
 * 
 */
package org.vocefiscal.models;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

	@JsonIgnore
	private Long idFiscalizacao;
	
	private String municipio;
	
	private String estado;
	
	private String zonaEleitoral;
	
	private String localDaVotacao;
	
	private String secaoEleitoral;
	
	@JsonIgnore
	private Integer podeEnviarRedeDados;
	
	@JsonIgnore
	private Integer statusDoEnvio;
	
	private Long data;

	@JsonIgnore
	private ArrayList<String> picturePathList;

	@JsonIgnore
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

	/**
	 * @return the podeEnviarRedeDados
	 */
	public Integer getPodeEnviarRedeDados() {
		return podeEnviarRedeDados;
	}

	/**
	 * @param podeEnviarRedeDados the podeEnviarRedeDados to set
	 */
	public void setPodeEnviarRedeDados(Integer podeEnviarRedeDados) {
		this.podeEnviarRedeDados = podeEnviarRedeDados;
	}

	/**
	 * @return the idFiscalizacao
	 */
	public Long getIdFiscalizacao() {
		return idFiscalizacao;
	}

	/**
	 * @param idFiscalizacao the idFiscalizacao to set
	 */
	public void setIdFiscalizacao(Long idFiscalizacao) {
		this.idFiscalizacao = idFiscalizacao;
	}

	/**
	 * @return the data
	 */
	public Long getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Long data) {
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((estado == null) ? 0 : estado.hashCode());
		result = prime * result
				+ ((idFiscalizacao == null) ? 0 : idFiscalizacao.hashCode());
		result = prime * result
				+ ((localDaVotacao == null) ? 0 : localDaVotacao.hashCode());
		result = prime * result
				+ ((municipio == null) ? 0 : municipio.hashCode());
		result = prime
				* result
				+ ((picture30PCPathList == null) ? 0 : picture30PCPathList
						.hashCode());
		result = prime * result
				+ ((picturePathList == null) ? 0 : picturePathList.hashCode());
		result = prime * result
				+ ((pictureURLList == null) ? 0 : pictureURLList.hashCode());
		result = prime
				* result
				+ ((podeEnviarRedeDados == null) ? 0 : podeEnviarRedeDados
						.hashCode());
		result = prime * result
				+ ((secaoEleitoral == null) ? 0 : secaoEleitoral.hashCode());
		result = prime * result
				+ ((statusDoEnvio == null) ? 0 : statusDoEnvio.hashCode());
		result = prime * result
				+ ((zonaEleitoral == null) ? 0 : zonaEleitoral.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Fiscalizacao other = (Fiscalizacao) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
			return false;
		if (idFiscalizacao == null) {
			if (other.idFiscalizacao != null)
				return false;
		} else if (!idFiscalizacao.equals(other.idFiscalizacao))
			return false;
		if (localDaVotacao == null) {
			if (other.localDaVotacao != null)
				return false;
		} else if (!localDaVotacao.equals(other.localDaVotacao))
			return false;
		if (municipio == null) {
			if (other.municipio != null)
				return false;
		} else if (!municipio.equals(other.municipio))
			return false;
		if (picture30PCPathList == null) {
			if (other.picture30PCPathList != null)
				return false;
		} else if (!picture30PCPathList.equals(other.picture30PCPathList))
			return false;
		if (picturePathList == null) {
			if (other.picturePathList != null)
				return false;
		} else if (!picturePathList.equals(other.picturePathList))
			return false;
		if (pictureURLList == null) {
			if (other.pictureURLList != null)
				return false;
		} else if (!pictureURLList.equals(other.pictureURLList))
			return false;
		if (podeEnviarRedeDados == null) {
			if (other.podeEnviarRedeDados != null)
				return false;
		} else if (!podeEnviarRedeDados.equals(other.podeEnviarRedeDados))
			return false;
		if (secaoEleitoral == null) {
			if (other.secaoEleitoral != null)
				return false;
		} else if (!secaoEleitoral.equals(other.secaoEleitoral))
			return false;
		if (statusDoEnvio == null) {
			if (other.statusDoEnvio != null)
				return false;
		} else if (!statusDoEnvio.equals(other.statusDoEnvio))
			return false;
		if (zonaEleitoral == null) {
			if (other.zonaEleitoral != null)
				return false;
		} else if (!zonaEleitoral.equals(other.zonaEleitoral))
			return false;
		return true;
	}

	
}
