/*
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.vocefiscal.amazonaws;

import java.util.LinkedHashMap;

import android.content.Context;

import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;

/*
 * The TransferModel is a class that encapsulates a transfer. It handles the 
 * interaction with the underlying TransferManager and Upload/Download classes
 */
public abstract class AWSTransferModel 
{
    private static final String TAG = "TransferModel";
   
    public static enum Status 
    { 
    	IN_PROGRESS, PAUSED, CANCELED, COMPLETED 
    };

    //all TransferModels have associated id which is their key to sModels
    private static LinkedHashMap<Integer, AWSTransferModel> sModels =  new LinkedHashMap<Integer, AWSTransferModel>();
    private static int sNextId = 1;

    private String mFileName;
    private Context mContext;
    private int mId;
    private TransferManager mManager;

    public static AWSTransferModel getTransferModel(int id) 
    {
        return sModels.get(id);
    }

    public static AWSTransferModel[] getAllTransfers() 
    {
        AWSTransferModel[] models = new AWSTransferModel[sModels.size()];
        return sModels.values().toArray(models);
    }

    public AWSTransferModel(Context context) 
    {
        mContext = context;
        mId = sNextId++;
        sModels.put(mId, this);
    }

    public String getFileName() 
    {
    	return mFileName;
    }

    public int getId() 
    { 
    	return mId; 
    }

    public int getProgress() 
    {
        Transfer transfer = getTransfer();
        if(transfer != null) 
        {
            int ret = (int)transfer.getProgress().getPercentTransferred();
            return ret;
        }
        return 0;
    }

    public abstract void abort();
    
    public abstract Status getStatus();

    public abstract Transfer getTransfer();

    public abstract void pause();

    public abstract void resume();

    protected Context getContext() 
    { 
    	return mContext;
    }

    protected TransferManager getTransferManager() 
    { 
    	if(mManager==null)
		{
    		mManager = new TransferManager(AWSUtil.getCredProvider(mContext));
		}
    	return mManager; 
    }
}
