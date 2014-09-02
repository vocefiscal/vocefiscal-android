/**
 * 
 */
package org.vocefiscal.communications;

/**
 * @author andre
 *
 */
public interface CommunicationConstants
{
	public final int OK_WITHCONTENT = 200;
	public final int OK_WITHOUTCONTENT = 204;	
	public final int NOTFOUND = 404;
	public final int FORBIDDEN = 403;
	public final int TIME_OUT = 0;
	public final int SEM_INTERNET = 1000;
	public final int JSON_PARSE_ERROR = 1001;
	public final int CANCELED = 1002;

	public final String CONTENTTYPE_PARAM = "Content-Type";
	public final String CONTENTTYPE_JSON = "application/json; charset=utf-8";

	public final Integer WAIT_RETRY = 1000;

	public final String SERVICE_BASE = "http://192.168.0.22:3000"; //tests


	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	//  É muito importante lembrar de não deixar as credenciais
	// quando gerar a versão do cliente.
	// DO NOT EMBED YOUR CREDENTIALS IN PRODUCTION APPS.
	// We offer two solutions for getting credentials to your mobile App.
	// Please read the following article to learn about Token Vending Machine:
	// * http://aws.amazon.com/articles/Mobile/4611615499399490
	// Or consider using web identity federation:
	// * http://aws.amazon.com/articles/Mobile/4617974389850313
	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	public static final String ACCESS_KEY_ID = "<ACCESS KEY>";
	public static final String SECRET_KEY = "<SECRET KEY>";

	public static final String PICTURE_BUCKET = "<BUCKET NAME>";
}
