/**
 * 
 */
package org.vocefiscal.communications;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 * @author andre
 *
 */
public class JsonHandler 
{


	public JsonHandler() {
	}

	/**
	 * Convert a String to a Object from a specific class
	 * @param <T>
	 * @param objectClass
	 * @param jsonData
	 * @return
	 */
	public <T> Object fromJsonDataToObject(Class<T> objectClass, String jsonData )
	{
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectReader reader =  mapper.reader(objectClass);
		T parsed = null;
		try 
		{
			parsed = reader.readValue(jsonData);
		} catch (JsonProcessingException e) 
		{
			
		} catch (IOException e) 
		{
			
		}

		return parsed;
	}

	/**
	 * Convert an object to String json formatted using GOOGLE GSON API
	 * @param object
	 * @return
	 */
	public String fromObjectToJsonData(Object object )
	{
		ObjectMapper mapper = new ObjectMapper();
		Writer strWriter = new StringWriter();

		try {
			mapper.writeValue(strWriter, object);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String jsonData = strWriter.toString();
		return jsonData;
	}
}
