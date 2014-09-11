/**
 * 
 */
package org.vocefiscal.amazonaws;

import org.vocefiscal.communications.CommunicationConstants;

import android.content.Context;

import com.amazonaws.android.auth.CognitoCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * @author andre
 * 
 * This class just handles getting the client since we don't need to have more than
 * one per application
 */
public class AWSUtil 
{
	private static AmazonS3Client sS3Client;
    private static CognitoCredentialsProvider sCredProvider;

    public static CognitoCredentialsProvider getCredProvider(Context context) 
    {
        if(sCredProvider == null) 
        {
            sCredProvider = new CognitoCredentialsProvider(context,CommunicationConstants.AWS_ACCOUNT_ID, CommunicationConstants.COGNITO_POOL_ID,CommunicationConstants.COGNITO_ROLE_UNAUTH,null);
            sCredProvider.refresh();
        }
        return sCredProvider;
    }

    public static String getPrefix(Context context) 
    {
        return getCredProvider(context).getIdentityId() + "/";
    }

    public static AmazonS3Client getS3Client(Context context) 
    {
        if(sS3Client == null) 
        {
            sS3Client = new AmazonS3Client(getCredProvider(context));
        }
        return sS3Client;
    }

    public static String getFileName(String path) 
    {
        return path.substring(path.lastIndexOf("/") + 1); 
    }
}
