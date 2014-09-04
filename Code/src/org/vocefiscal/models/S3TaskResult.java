package org.vocefiscal.models;

import java.net.URL;


public class S3TaskResult 
{	
	private URL urlDaFoto = null;
	private Integer posicaoFoto = null;
	private Long idFiscalizacao = null;
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
	 * @return the posicaoFoto
	 */
	public Integer getPosicaoFoto() {
		return posicaoFoto;
	}
	/**
	 * @param posicaoFoto the posicaoFoto to set
	 */
	public void setPosicaoFoto(Integer posicaoFoto) {
		this.posicaoFoto = posicaoFoto;
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

}
