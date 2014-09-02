/**
 * 
 */
package org.vocefiscal.communications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;

/**
 * @author andre
 *
 */
public class RestClient 
{
	private ArrayList <NameValuePair> params;
	private ArrayList <NameValuePair> headers;
	private HttpEntity entity;
	private String url;
	private String serviceName;
	private int responseCode;
	private String message;
	private MultiPartHandler multiPartHandler;	
	private Context context;
	private String secret=null;
	private String response;
	
	public RestClient(String serviceBase,String serviceName,Context context/*, String userId, String accessToken*/)
	{
		this.serviceName = serviceName;
		this.url = serviceBase+serviceName;
		this.context = context;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
//		secret="";
//		
//		if(userId!=null)
//		{
//			addApiAuth();
//			addAccessTokenAuth(userId,accessToken);
//		}	
	}


	public MultiPartHandler getMultiPartHandler() 
	{
		return multiPartHandler;
	}

	public void setMultiPartHandler(MultiPartHandler multiPartHandler)
	{
		this.multiPartHandler = multiPartHandler;
	}


	public HttpEntity getEntity() 
	{
		return entity;
	}

	public void setEntity(String entityContent) 
	{
		try 
		{
			this.entity = new StringEntity(entityContent, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e)
		{

		}

	}

	public String getResponse() 
	{
		return response;
	}

	public String getErrorMessage() 
	{
		return message;
	}

	public int getResponseCode() 
	{
		return responseCode;
	}

	public void addParam(String name, String value)
	{
		params.add(new BasicNameValuePair(name, value));
	}

	public void addHeader(String name, String value)
	{
		headers.add(new BasicNameValuePair(name, value));
	}
	
//	private void addApiAuth()
//	{
//		/*
//		 * API authorization
//		 */
//		MessageDigest cript=null;
//		try 
//		{
//			cript = MessageDigest.getInstance("SHA-1");
//		} catch (NoSuchAlgorithmException e1) 
//		{
//			e1.printStackTrace();
//		}
//
//		String timestamp = String.valueOf(System.currentTimeMillis());
//		String apiAuth = serviceName+secret+timestamp;
//
//		cript.reset();
//		try 
//		{
//			cript.update(apiAuth.getBytes("utf8"));
//		} catch (UnsupportedEncodingException e1) 
//		{
//			e1.printStackTrace();
//		}
//		apiAuth = new String(Hex.encodeHex(cript.digest()));
//		
//		addParam("timestamp", timestamp);
//		addParam("apiauth",apiAuth);
//	}
	
	private void addAccessTokenAuth(String userId, String accessToken) 
	{
		addParam("userId", userId);
		if(accessToken!=null)
			addParam("clientAccessToken", accessToken);
	}

//	public void executePost(RequestMethod method) throws Exception
//	{
//
//		HttpPost request = new HttpPost(url);
//		//add headers
//		for(NameValuePair h : headers)
//		{
//			request.addHeader(h.getName(), h.getValue());
//		}
//		
//		if(!params.isEmpty())
//		{
//			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//		}
//		 
//		if(entity!=null)
//			request.setEntity(entity);
//
//		executeRequest(request, url);
//		
//	}
	public void execute(RequestMethod method) throws Exception
	{
		switch(method) {
		case GET:
		{
			//add parameters			
			StringBuffer bufCombinedParams = new StringBuffer();
			if(!params.isEmpty()){				
				bufCombinedParams.append("?");
				for(NameValuePair p : params)
				{
					String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
					if(bufCombinedParams.length() > 1)
					{						
						bufCombinedParams.append("&" + paramString);
					}
					else
					{						
						bufCombinedParams.append(paramString);
					}
				}
			}

			String combinedParams = bufCombinedParams.toString();
			HttpGet request = new HttpGet(url + combinedParams);

			//add headers
			for(NameValuePair h : headers)
			{
				request.addHeader(h.getName(), h.getValue());
			}


			executeRequest(request, url);
			break;
		}
		case POST:	
		{
			//add parameters			
			StringBuffer bufCombinedParams = new StringBuffer();
			if(!params.isEmpty()){				
				bufCombinedParams.append("?");
				for(NameValuePair p : params)
				{
					String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
					if(bufCombinedParams.length() > 1)
					{						
						bufCombinedParams.append("&" + paramString);
					}
					else
					{						
						bufCombinedParams.append(paramString);
					}
				}
			}

			String combinedParams = bufCombinedParams.toString();

			HttpPost request = new HttpPost(url+combinedParams);
			//add headers
			for(NameValuePair h : headers)
			{
				request.addHeader(h.getName(), h.getValue());
			}

			/**
			if(!params.isEmpty()){
				//request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}
			 */
			if(entity!=null)
				request.setEntity(entity);

			executeRequest(request, url);


			break;
		}
		case PUT:	
		{
			//add parameters			
			StringBuffer bufCombinedParams = new StringBuffer();
			if(!params.isEmpty()){				
				bufCombinedParams.append("?");
				for(NameValuePair p : params)
				{
					String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
					if(bufCombinedParams.length() > 1)
					{						
						bufCombinedParams.append("&" + paramString);
					}
					else
					{						
						bufCombinedParams.append(paramString);
					}
				}
			}
			String combinedParams = bufCombinedParams.toString();
			HttpPut request = new HttpPut(url+combinedParams);
			//add headers
			for(NameValuePair h : headers)
			{
				request.addHeader(h.getName(), h.getValue());
			}

			/**
			if(!params.isEmpty()){
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}*/

			if(entity!=null)
				request.setEntity(entity);


			executeRequest(request, url);
			break;
		}

		}
	}

	/**
	 * Execute the Rest Client Request
	 * @param request
	 * @param url
	 */
	private void executeRequest(HttpUriRequest request, String url)
	{

		//PARAMS
		HttpParams httpParams = request.getParams();
		HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
		HttpProtocolParams.setHttpElementCharset(httpParams,  HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(httpParams, true);
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		httpParams.setIntParameter( CoreProtocolPNames.WAIT_FOR_CONTINUE, 2000);
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 10000;
		HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 10000;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);


		HttpClient client = null;
		boolean debug = false; //TODO Enable this one for tests. Accept a self-signed certificate. See the class MyHttpClient for more details
		
		if(debug)
		{
			client = new MyHttpClient(context,httpParams); //Enable this one for tests. Accept a self-signed certificate. See the class MyHttpClient for more details
		}else
		{
			client = new DefaultHttpClient(httpParams);// Enable this for release
		}
			

		HttpResponse httpResponse;

		try {
			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) 
			{

				InputStream instream = entity.getContent();
				if(multiPartHandler!=null)
					convertStreamToStringMultiPart(instream);
				else
					response = convertStreamToString(instream);

				// Closing the input stream will trigger connection release
				instream.close();
			}

		} catch (ClientProtocolException e)  {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		}
	}

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {


				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	private  void convertStreamToStringMultiPart(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		boolean firstPart = true;
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {


				if(line.equalsIgnoreCase("--PART"))
				{
					if(sb.length() > 0)
					{
						
						multiPartHandler.handlePart(sb.toString(),firstPart);
						firstPart =false;
						sb  = new StringBuilder();
					}
				}
				else
				{
					if(line!=null && line.length() > 0 && !line.startsWith("Content-Type") &&!line.startsWith("--PART"))
						sb.append(line + "\n");	
				}

			}

			if(sb.length() > 0)
			{
				multiPartHandler.handlePart(sb.toString(),firstPart);	
			}
			else
			{
				if(firstPart)
					responseCode = CommunicationConstants.OK_WITHOUTCONTENT;						
			}
		} catch (IOException e) {
			responseCode = CommunicationConstants.OK_WITHOUTCONTENT;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				responseCode = CommunicationConstants.OK_WITHOUTCONTENT;
			}
		}
		
		//Log.d("ATUALIZAÇÃO", "PAAAAAASSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSOOOOOOOOOOOOOUUUUUUUUUUUUUUUUUUUUUU "+ responseCode);

	}
}
