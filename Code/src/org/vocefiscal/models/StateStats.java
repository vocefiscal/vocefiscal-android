/**
 * 
 */
package org.vocefiscal.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author andre
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class StateStats implements Serializable
{


	private static final long serialVersionUID = -6680411984767337439L;
	
	private String stateCode;
	
	private Integer pollTapesCount;
	

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
	 * @return the pollTapesCount
	 */
	public Integer getPollTapesCount() {
		return pollTapesCount;
	}

	/**
	 * @param pollTapesCount the pollTapesCount to set
	 */
	public void setPollTapesCount(Integer pollTapesCount) {
		this.pollTapesCount = pollTapesCount;
	}

}
