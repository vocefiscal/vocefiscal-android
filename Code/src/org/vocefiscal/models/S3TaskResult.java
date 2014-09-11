package org.vocefiscal.models;



public class S3TaskResult 
{	
	private String urlDaFoto = null;
	private Integer posicaoFoto = null;
	private Long idFiscalizacao = null;

	
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
	/**
	 * @return the urlDaFoto
	 */
	public String getUrlDaFoto() {
		return urlDaFoto;
	}
	/**
	 * @param urlDaFoto the urlDaFoto to set
	 */
	public void setUrlDaFoto(String urlDaFoto) {
		this.urlDaFoto = urlDaFoto;
	}

}
