/**
 * 
 */
package org.vocefiscal.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author andre
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class Municipality  implements Serializable
{
	private static final long serialVersionUID = -4659307115475411908L;
	
	private String stateCode;
    private String municipalityName;
    private String slug;
	/**
	 * @return the stateCode
	 */
	public String getStateCode() {
		return stateCode;
	}
	/**
	 * @param stateCode the stateCode to set
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	/**
	 * @return the municipalityName
	 */
	public String getMunicipalityName() {
		return municipalityName;
	}
	/**
	 * @param municipalityName the municipalityName to set
	 */
	public void setMunicipalityName(String municipalityName) {
		this.municipalityName = municipalityName;
	}
	/**
	 * @return the slug
	 */
	public String getSlug() {
		return slug;
	}
	/**
	 * @param slug the slug to set
	 */
	public void setSlug(String slug) {
		this.slug = slug;
	}
}
