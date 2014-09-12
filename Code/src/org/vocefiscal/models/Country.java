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
public class Country implements Serializable 
{
	private static final long serialVersionUID = 97025453559576041L;
	
	private String name;
	
	private ArrayList<State> states;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the states
	 */
	public ArrayList<State> getStates() {
		return states;
	}

	/**
	 * @param states the states to set
	 */
	public void setStates(ArrayList<State> states) {
		this.states = states;
	}

}
