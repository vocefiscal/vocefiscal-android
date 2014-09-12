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
public class State implements Serializable
{
	private static final long serialVersionUID = -9109368711525532976L;

	private String name;

	private ArrayList<Municipality> municipalities;

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
	 * @return the municipalities
	 */
	public ArrayList<Municipality> getMunicipalities() {
		return municipalities;
	}

	/**
	 * @param municipalities the municipalities to set
	 */
	public void setMunicipalities(ArrayList<Municipality> municipalities) {
		this.municipalities = municipalities;
	}

}
