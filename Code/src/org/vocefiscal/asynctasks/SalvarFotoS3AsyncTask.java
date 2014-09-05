package org.vocefiscal.asynctasks;

import java.io.File;
import java.net.URL;

import org.vocefiscal.communications.CommunicationConstants;
import org.vocefiscal.communications.CommunicationUtils;
import org.vocefiscal.models.S3TaskResult;
import org.vocefiscal.utils.ImageHandler;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

public class SalvarFotoS3AsyncTask extends AsyncTask<Object,Object,Object>
{

	private  OnSalvarFotoS3PostExecuteListener<Object> listener;

	private Context context;

	private String errorMsg="";

	private int errorCode=-1;

	//imagem para ser enviada ao servidor de fotos s3
	private String selectedPath;

	//URL da foto no banco de dados AWS S3
	private URL urlDaFoto;

	private Integer posicaoFoto;

	private Long idFiscalizacao;

	//essa é a variável contendo o cliente AWS S3
	private AmazonS3Client s3Client;

	public SalvarFotoS3AsyncTask(OnSalvarFotoS3PostExecuteListener<Object> listener,Context context, String selectedPath, Long idFiscalizacao, Integer posicaoFoto) 
	{
		super();
		this.listener = listener;
		this.context = context;
		this.selectedPath = selectedPath;
		this.posicaoFoto = posicaoFoto;
		this.idFiscalizacao = idFiscalizacao;
		try
		{
			s3Client = new AmazonS3Client(new BasicAWSCredentials(CommunicationConstants.ACCESS_KEY_ID,	CommunicationConstants.SECRET_KEY));
		}catch(Exception e)
		{
			Log.i("S3 Client", e.getMessage());
		}
	}


	@Override
	protected void onPreExecute() 
	{		
		super.onPreExecute();
	}

	@Override
	protected S3TaskResult doInBackground(Object... params) 
	{
		S3TaskResult result = null;		

		boolean ret = CommunicationUtils.verifyConnectivity(context);

		if(ret)
		{	

			try
			{
				s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));

				ContentResolver resolver = context.getContentResolver();
				String fileSizeColumn[] = {OpenableColumns.SIZE}; 

				String size = null;

				Cursor cursor = resolver.query(Uri.parse(selectedPath),fileSizeColumn, null, null, null);
				if(cursor!=null)
				{
					cursor.moveToFirst();
					int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
					// If the size is unknown, the value stored is null.  But since an int can't be
					// null in java, the behavior is implementation-specific, which is just a fancy
					// term for "unpredictable".  So as a rule, check if it's null before assigning
					// to an int.  This will happen often:  The storage API allows for remote
					// files, whose size might not be locally known.

					if (!cursor.isNull(sizeIndex)) 
					{
						// Technically the column stores an int, but cursor.getString will do the
						// conversion automatically.
						size = cursor.getString(sizeIndex);
					} 
					cursor.close();
				}	                     

				ObjectMetadata metadata = new ObjectMetadata();
				metadata.setContentType(resolver.getType(Uri.parse(selectedPath)));
				if(size != null)
				{
					metadata.setContentLength(Long.parseLong(size));
				}

				// Put the image data into S3.				
				try 
				{			
					File selectedFile = new File(selectedPath);
					String pictureName;

					//define o nome da foto para ser guardada no banco
					pictureName = ImageHandler.nomeDaMidia(selectedFile) + ".jpg";

					//define a região/endpoint
					s3Client.setEndpoint("s3.amazonaws.com");

					//envia a foto para o S3
					Uri selectedImageUri = Uri.fromFile(selectedFile);
					PutObjectRequest por = new PutObjectRequest(CommunicationConstants.PICTURE_BUCKET, pictureName, resolver.openInputStream(selectedImageUri),metadata)
					.withCannedAcl(CannedAccessControlList.PublicRead);
					s3Client.putObject(por);

					//força que a procura seja por imagens/jpeg
					ResponseHeaderOverrides override = new ResponseHeaderOverrides();
					override.setContentType("image/jpeg");

					// Gera a presigned URL
					GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(CommunicationConstants.PICTURE_BUCKET, pictureName);
					urlRequest.setResponseHeaders(override);
					urlDaFoto = s3Client.generatePresignedUrl(urlRequest);

					String string = urlDaFoto.toString();
					String[] parts = string.split("\\?");
					String part1 = parts[0]; // 004

					URL urlFinal = new URL(part1);

					result = new S3TaskResult();					
					result.setUrlDaFoto(urlFinal);
					result.setPosicaoFoto(posicaoFoto);
					result.setIdFiscalizacao(idFiscalizacao);

				} catch (Exception exception) 
				{
					errorCode = -1;
					errorMsg = "Erro ao fazer o upload da imagem.";
				}

			}catch (Exception ex) 
			{  				
				errorCode = -1;
				errorMsg = "Sua conexão está ruim.";
			}				
		}else
		{
			errorCode = CommunicationConstants.SEM_INTERNET;
			errorMsg = "Sem conexão com a Internet.";
		}

		return result;
	}	

	@Override
	protected void onPostExecute(Object result) 
	{				
		if(result!=null)
		{			
			listener.finishedSalvarFotoS3ComResultado(result);
		}			
		else
		{			
			listener.finishedSalvarFotoS3ComError(errorCode,errorMsg,idFiscalizacao);			
		}
	}

	public interface OnSalvarFotoS3PostExecuteListener<K>
	{
		public void finishedSalvarFotoS3ComResultado(Object result);
		public void finishedSalvarFotoS3ComError(int errorCode, String error,Long idFiscalizacao);
	}

}
