package org.vocefiscal.models;



public class S3UploadPictureResult 
{	
	private String urlDaFoto;
	private Integer posicaoFoto;
	private Long idFiscalizacao;
	private String slugFiscalizacao;	
	private String zonaFiscalizacao;
	
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
	/**
	 * @return the slugFiscalizacao
	 */
	public String getSlugFiscalizacao() {
		return slugFiscalizacao;
	}
	/**
	 * @param slugFiscalizacao the slugFiscalizacao to set
	 */
	public void setSlugFiscalizacao(String slugFiscalizacao) {
		this.slugFiscalizacao = slugFiscalizacao;
	}
	/**
	 * @return the zonaFiscalizacao
	 */
	public String getZonaFiscalizacao() {
		return zonaFiscalizacao;
	}
	/**
	 * @param zonaFiscalizacao the zonaFiscalizacao to set
	 */
	public void setZonaFiscalizacao(String zonaFiscalizacao) {
		this.zonaFiscalizacao = zonaFiscalizacao;
	}
}