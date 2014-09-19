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
	public static final int OK_WITHCONTENT = 200;
	public static final int OK_WITHOUTCONTENT = 204;	
	public static final int NOTFOUND = 404;
	public static final int FORBIDDEN = 403;
	public static final int TIME_OUT = 0;
	public static final int SEM_INTERNET = 1000;
	public static final int JSON_PARSE_ERROR = 1001;
	public static final int CANCELED = 1002;

	public static final String CONTENTTYPE_PARAM = "Content-Type";
	public static final String CONTENTTYPE_JSON = "application/json; charset=utf-8";

	public static final Integer WAIT_RETRY = 1000;		

	//GMAIL
	public static final String EMAIL_USER = "EMAIL_USER";  
	public static final String EMAIL_PASSWORD = "EMAIL_PASSWORD";
	public static final String[] EMAIL_TO = new String[]{"EMAIL_TO","EMAIL_TO","EMAIL_TO"};
	

	//AWS COGNITO	
    public static final String AWS_ACCOUNT_ID = "AWS_ACCOUNT_ID";
    public static final String COGNITO_POOL_ID =  "COGNITO_POOL_ID";
    public static final String COGNITO_ROLE_UNAUTH = "COGNITO_ROLE_UNAUTH";
    
    //AWS S3
    public static final String PICTURE_BUCKET_NAME = "PICTURE_BUCKET_NAME";
    public static final String JSON_BUCKET_NAME = "JSON_BUCKET_NAME"; 
    public static final String STATE_STATS_BASE_ADDRESS = "STATE_STATS_BASE_ADDRESS";
  	
  	//Twitter
    
    public static final String TWITTER_API_KEY = "TWITTER_API_KEY";
	public static final String TWITTER_API_SECRET = "TWITTER_API_SECRET ";
}
