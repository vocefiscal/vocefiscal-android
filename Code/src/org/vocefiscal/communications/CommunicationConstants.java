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

	//GMAIL
	public final String EMAIL_USER = "EMAIL_USER";  
	public final String EMAIL_PASSWORD = "EMAIL_PASSWORD";
	public final String[] EMAIL_TO = new String[]{"EMAIL_TO","EMAIL_TO","EMAIL_TO"}; 

	//AWS COGNITO	
    public static final String AWS_ACCOUNT_ID = "AWS_ACCOUNT_ID";
    public static final String COGNITO_POOL_ID =  "COGNITO_POOL_ID";
    public static final String COGNITO_ROLE_UNAUTH = "COGNITO_ROLE_UNAUTH";
    
    //AWS S3
    public static final String PICTURE_BUCKET_NAME = "PICTURE_BUCKET_NAME";
    public static final String JSON_BUCKET_NAME = "JSON_BUCKET_NAME"; 
}
