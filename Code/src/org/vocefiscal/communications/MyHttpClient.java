/**
 * 
 */
package org.vocefiscal.communications;

import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpParams;
import org.vocefiscal.R;

import android.content.Context;

/**
 * @author andre
 * 
 * References:
 * http://stackoverflow.com/questions/2012497/accepting-a-certificate-for-https-on-android
 * 
 * 
 *
 */
public class MyHttpClient extends DefaultHttpClient 
{
	final Context context;

	public MyHttpClient(Context context,HttpParams httpParams) 
	{
		super(httpParams);
		this.context = context;
	}

	@Override
	protected ClientConnectionManager createClientConnectionManager() 
	{
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		// Register for port 443 our SSLSocketFactory with our keystore
		// to the ConnectionManager
		//TODO registry.register(new Scheme("https", newSslSocketFactory(), 443));
		return new SingleClientConnManager(getParams(), registry);
	}

//	private SSLSocketFactory newSslSocketFactory() 
//	{
//		try 
//		{
//			// Get an instance of the Bouncy Castle KeyStore format
//			KeyStore trusted = KeyStore.getInstance("BKS");
//			// Get the raw resource, which contains the keystore with
//			// your trusted certificates (root and any intermediate certs)
//			InputStream in = context.getResources().openRawResource(R.raw.mykeystore);
//			try
//			{
//				// Initialize the keystore with the provided trusted certificates
//				// Also provide the password of the keystore
//				trusted.load(in, "secret".toCharArray());
//			} finally
//			{
//				in.close();
//			}
//			// Pass the keystore to the SSLSocketFactory. The factory is responsible
//			// for the verification of the server certificate.
//			SSLSocketFactory sf = new SSLSocketFactory(trusted);
//			// Hostname verification from certificate
//			// http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
//			sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
//			return sf;
//		} catch (Exception e)
//		{
//			throw new AssertionError(e);
//		}
//	}
}
