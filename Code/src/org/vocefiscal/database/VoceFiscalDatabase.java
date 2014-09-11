/**
 * 
 */
package org.vocefiscal.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.vocefiscal.R;
import org.vocefiscal.models.Fiscalizacao;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

/**
 * @author andre
 *
 */
public class VoceFiscalDatabase 
{
	private static final String DATABASE_NAME = "VoceFiscalDatabase";

	private static final int DATABASE_VERSION = 1;

	/**Fiscalizacao*/
	private static final String TABELA_FISCALIZACAO = "fiscalizacao";
	//CAMPOS	
	private static final String FISCALIZACAO_IDFISCALIZACAO = "idfiscalizacao";	
	private static final String FISCALIZACAO_MUNICIPIO  = "municipio";
	private static final String FISCALIZACAO_ESTADO  = "estado";	
	private static final String FISCALIZACAO_ZONAELEITORAL  = "zonaeleitoral";	
	private static final String FISCALIZACAO_LOCALDAVOTACAO  = "localdavotacao";	
	private static final String FISCALIZACAO_SECAOELEITORAL  = "secaoeleitoral";	
	private static final String FISCALIZACAO_PODEENVIARREDEDADOS  = "podeenviarrededados";
	private static final String FISCALIZACAO_STATUSDOENVIO  = "statusdoenvio";	
	private static final String FISCALIZACAO_DATA  = "data";	

	/**Picture path*/
	private static final String TABELA_PICTUREPATH = "picturepath";
	//CAMPOS		
	private static final String PICTUREPATH_IDFISCALIZACAO  = "idfiscalizacao";
	private static final String PICTUREPATH_PICTUREPATH  = "picturepath";	

	/**Picture path*/
	private static final String TABELA_PICTURETHIRTYPCPATH = "picturethirtypcpath";
	//CAMPOS		
	private static final String PICTURETHIRTYPCPATH_IDFISCALIZACAO  = "idfiscalizacao";
	private static final String PICTURETHIRTYPCPATH_PICTURETHIRTYPCPATH  = "picturethirtypcpath";	

	/**Picture path*/
	private static final String TABELA_PICTUREURL = "pictureurl";
	//CAMPOS	
	private static final String PICTUREURL_IDFISCALIZACAO  = "idfiscalizacao";
	private static final String PICTUREURL_PICTUREURL  = "pictureurl";	

	private static ClienteDatabaseOpenHelper mDatabaseOpenHelper;

	public VoceFiscalDatabase(Context context)
	{
		if(mDatabaseOpenHelper ==null || !mDatabaseOpenHelper.isOpen())
			mDatabaseOpenHelper = new ClienteDatabaseOpenHelper(context);
	}

	public long addFiscalizacao(Fiscalizacao fiscalizacao)
	{
		return mDatabaseOpenHelper.addFiscalizacao(fiscalizacao);
	}
	
	public boolean updateStatusEnvio(Long idFiscalizacao, int statusEnvio) 
	{
		return mDatabaseOpenHelper.updateStatusEnvio(idFiscalizacao,statusEnvio); 
		
	}
	
	public boolean updatePodeEnviarRedeDeDados(Long idFiscalizacao, int i) 
	{
		return mDatabaseOpenHelper.updatePodeEnviarRedeDeDados(idFiscalizacao, i) ;	
	}
	
	public ArrayList<Fiscalizacao> getFiscalizacoes()
	{
		ArrayList<Fiscalizacao> fiscalizacoes = null;
		
		try
		{
			Cursor fiscalizacoesCursor = query(null,null,null,TABELA_FISCALIZACAO);
			
			if(fiscalizacoesCursor!=null)
			{
				fiscalizacoes = new ArrayList<Fiscalizacao>();
				do
				{
					Fiscalizacao fiscalizacao = new Fiscalizacao();
					
					int index = fiscalizacoesCursor.getColumnIndex(FISCALIZACAO_IDFISCALIZACAO);
					if(!fiscalizacoesCursor.isNull(index))
					{
						Long idFiscalizacao = fiscalizacoesCursor.getLong(index);
						fiscalizacao.setIdFiscalizacao(idFiscalizacao);
					}
					
					index = fiscalizacoesCursor.getColumnIndex(FISCALIZACAO_MUNICIPIO);
					if(!fiscalizacoesCursor.isNull(index))
					{
						String municipio = fiscalizacoesCursor.getString(index);
						fiscalizacao.setMunicipio(municipio);
					}
					
					index = fiscalizacoesCursor.getColumnIndex(FISCALIZACAO_ESTADO);
					if(!fiscalizacoesCursor.isNull(index))
					{
						String estado = fiscalizacoesCursor.getString(index);
						fiscalizacao.setEstado(estado);
					}
					
					index = fiscalizacoesCursor.getColumnIndex(FISCALIZACAO_ZONAELEITORAL);
					if(!fiscalizacoesCursor.isNull(index))
					{
						String zonaEleitoral = fiscalizacoesCursor.getString(index);
						fiscalizacao.setZonaEleitoral(zonaEleitoral); 
					}
					
					index = fiscalizacoesCursor.getColumnIndex(FISCALIZACAO_LOCALDAVOTACAO);
					if(!fiscalizacoesCursor.isNull(index))
					{
						String localDaVotacao = fiscalizacoesCursor.getString(index);
						fiscalizacao.setLocalDaVotacao(localDaVotacao);
					}
					
					index = fiscalizacoesCursor.getColumnIndex(FISCALIZACAO_SECAOELEITORAL);
					if(!fiscalizacoesCursor.isNull(index))
					{
						String secaoEleitoral = fiscalizacoesCursor.getString(index);
						fiscalizacao.setSecaoEleitoral(secaoEleitoral); 
					}
					
					index = fiscalizacoesCursor.getColumnIndex(FISCALIZACAO_PODEENVIARREDEDADOS);
					if(!fiscalizacoesCursor.isNull(index))
					{
						Integer podeEnviarRedeDados = fiscalizacoesCursor.getInt(index);
						fiscalizacao.setPodeEnviarRedeDados(podeEnviarRedeDados);
					}
					
					index = fiscalizacoesCursor.getColumnIndex(FISCALIZACAO_STATUSDOENVIO);
					if(!fiscalizacoesCursor.isNull(index))
					{
						Integer statusDoEnvio = fiscalizacoesCursor.getInt(index);
						fiscalizacao.setStatusDoEnvio(statusDoEnvio);
					}
					
					index = fiscalizacoesCursor.getColumnIndex(FISCALIZACAO_DATA);
					if(!fiscalizacoesCursor.isNull(index))
					{
						Long data = fiscalizacoesCursor.getLong(index);
						fiscalizacao.setData(data);
					}
					
					fiscalizacao.setPicturePathList(getPicturePathList(fiscalizacao.getIdFiscalizacao()));
					
					fiscalizacao.setPicture30PCPathList(getPicture30pcPathList(fiscalizacao.getIdFiscalizacao()));
					
					fiscalizacao.setPictureURLList(getPictureURLList(fiscalizacao.getIdFiscalizacao()));
					
					
					fiscalizacoes.add(fiscalizacao);
				}while(fiscalizacoesCursor.moveToNext());
				fiscalizacoesCursor.close();
			}
		}catch(Exception e)
		{
			
		}
		
		return fiscalizacoes;
	}
	
	public Fiscalizacao getFiscalizacao(Long idFiscalizacao) 
	{
		Fiscalizacao fiscalizacao = null;

		if(idFiscalizacao!=null)
		{
			try
			{
				String selection = null;
				String[] selectionArgs = null;

				selection = FISCALIZACAO_IDFISCALIZACAO + " = ?";
				selectionArgs = new String[] {String.valueOf(idFiscalizacao)};

				Cursor fiscalizacaoCursor =  query(selection, selectionArgs, null, TABELA_FISCALIZACAO);
				if(fiscalizacaoCursor!=null)
				{
					fiscalizacao = new Fiscalizacao();

					fiscalizacao.setIdFiscalizacao(idFiscalizacao);

					int index = fiscalizacaoCursor.getColumnIndex(FISCALIZACAO_MUNICIPIO);
					if(!fiscalizacaoCursor.isNull(index))
					{
						String municipio = fiscalizacaoCursor.getString(index);
						fiscalizacao.setMunicipio(municipio);
					}

					index = fiscalizacaoCursor.getColumnIndex(FISCALIZACAO_ESTADO);
					if(!fiscalizacaoCursor.isNull(index))
					{
						String estado = fiscalizacaoCursor.getString(index);
						fiscalizacao.setEstado(estado);
					}

					index = fiscalizacaoCursor.getColumnIndex(FISCALIZACAO_ZONAELEITORAL);
					if(!fiscalizacaoCursor.isNull(index))
					{
						String zonaEleitoral = fiscalizacaoCursor.getString(index);
						fiscalizacao.setZonaEleitoral(zonaEleitoral); 
					}

					index = fiscalizacaoCursor.getColumnIndex(FISCALIZACAO_LOCALDAVOTACAO);
					if(!fiscalizacaoCursor.isNull(index))
					{
						String localDaVotacao = fiscalizacaoCursor.getString(index);
						fiscalizacao.setLocalDaVotacao(localDaVotacao);
					}

					index = fiscalizacaoCursor.getColumnIndex(FISCALIZACAO_SECAOELEITORAL);
					if(!fiscalizacaoCursor.isNull(index))
					{
						String secaoEleitoral = fiscalizacaoCursor.getString(index);
						fiscalizacao.setSecaoEleitoral(secaoEleitoral); 
					}

					index = fiscalizacaoCursor.getColumnIndex(FISCALIZACAO_PODEENVIARREDEDADOS);
					if(!fiscalizacaoCursor.isNull(index))
					{
						Integer podeEnviarRedeDados = fiscalizacaoCursor.getInt(index);
						fiscalizacao.setPodeEnviarRedeDados(podeEnviarRedeDados);
					}

					index = fiscalizacaoCursor.getColumnIndex(FISCALIZACAO_STATUSDOENVIO);
					if(!fiscalizacaoCursor.isNull(index))
					{
						Integer statusDoEnvio = fiscalizacaoCursor.getInt(index);
						fiscalizacao.setStatusDoEnvio(statusDoEnvio);
					}

					index = fiscalizacaoCursor.getColumnIndex(FISCALIZACAO_DATA);
					if(!fiscalizacaoCursor.isNull(index))
					{
						Long data = fiscalizacaoCursor.getLong(index);
						fiscalizacao.setData(data);
					}

					fiscalizacao.setPicturePathList(getPicturePathList(idFiscalizacao));

					fiscalizacao.setPicture30PCPathList(getPicture30pcPathList(idFiscalizacao));

					fiscalizacao.setPictureURLList(getPictureURLList(idFiscalizacao));

					fiscalizacaoCursor.close();	
				}				
			}catch(Exception e)
			{

			}
		}

		return fiscalizacao;
	}

	private ArrayList<String> getPictureURLList(Long idFiscalizacao) 
	{
		ArrayList<String> pictureURLList = null;
		
		try
		{
			String selection = null;
			String[] selectionArgs = null;
			
			if(idFiscalizacao!=null)
			{
				selection = PICTUREURL_IDFISCALIZACAO + " = ?";
				selectionArgs = new String[] {String.valueOf(idFiscalizacao)};
				
				Cursor pictureURLListCursor =  query(selection, selectionArgs, null, TABELA_PICTUREURL);
				if(pictureURLListCursor!=null)
				{
					pictureURLList = new ArrayList<String>();
					
					do
					{
						String pictureURL = null;
						
						int index = pictureURLListCursor.getColumnIndex(PICTUREURL_PICTUREURL);
						if(!pictureURLListCursor.isNull(index))
						{
							pictureURL = pictureURLListCursor.getString(index);
						}
						
						if(pictureURL!=null)							
							pictureURLList.add(pictureURL);
					}while(pictureURLListCursor.moveToNext());
					pictureURLListCursor.close();
				}				
			}
		}catch(Exception e)
		{
			
		}
		return pictureURLList;
	}

	private ArrayList<String> getPicture30pcPathList(Long idFiscalizacao) 
	{
		ArrayList<String> picture30pcPathList = null;
		
		try
		{
			String selection = null;
			String[] selectionArgs = null;
			
			if(idFiscalizacao!=null)
			{
				selection = PICTURETHIRTYPCPATH_IDFISCALIZACAO + " = ?";
				selectionArgs = new String[] {String.valueOf(idFiscalizacao)};
				
				Cursor picture30pcPathListCursor = query(selection, selectionArgs, null, TABELA_PICTURETHIRTYPCPATH);
				if(picture30pcPathListCursor!=null)
				{
					picture30pcPathList = new ArrayList<String>();
					
					do
					{
						String picture30pcPath = null;
						
						int index = picture30pcPathListCursor.getColumnIndex(PICTURETHIRTYPCPATH_PICTURETHIRTYPCPATH);
						if(!picture30pcPathListCursor.isNull(index))
						{
							picture30pcPath = picture30pcPathListCursor.getString(index);
						}
						
						if(picture30pcPath!=null)
							picture30pcPathList.add(picture30pcPath);
					}while(picture30pcPathListCursor.moveToNext());
					picture30pcPathListCursor.close();
				}
				
			}
		}catch(Exception e)
		{
			
		}
		
		return picture30pcPathList;
	}

	private ArrayList<String> getPicturePathList(Long idFiscalizacao) 
	{
		ArrayList<String> picturePathList = null;
		
		try
		{
			String selection = null;
			String[] selectionArgs = null;
			
			if(idFiscalizacao!=null)
			{
				selection = PICTUREPATH_IDFISCALIZACAO + " = ?";
				selectionArgs = new String[] {String.valueOf(idFiscalizacao)};
				
				Cursor picturePathListCursor = query(selection, selectionArgs, null, TABELA_PICTUREPATH);
				
				if(picturePathListCursor!=null)
				{
					picturePathList = new ArrayList<String>();
					
					do
					{
						String picturePath = null;
						
						int index = picturePathListCursor.getColumnIndex(PICTUREPATH_PICTUREPATH);
						if(!picturePathListCursor.isNull(index))
						{
							picturePath = picturePathListCursor.getString(index);
						}
						
						if(picturePath!=null)
							picturePathList.add(picturePath);
					}while(picturePathListCursor.moveToNext());
					picturePathListCursor.close();
				}
			}
		}catch(Exception e)
		{
			
		}
		
		return picturePathList;
	}
	
	public long addPictureURL(Long idFiscalizacao, String string) 
	{
		return mDatabaseOpenHelper.addPictureURL(idFiscalizacao, string);
		
	}

	public void close()
	{
		mDatabaseOpenHelper.close();	
	}

	public boolean isOpen() 
	{
		return mDatabaseOpenHelper.isOpen();
	}

	/**
	 * Performs a database query.
	 * @param selection The selection clause
	 * @param selectionArgs Selection arguments for "?" components in the selection
	 * @param columns The columns to return
	 * @return A Cursor over all rows matching the query
	 */
	private Cursor query(String selection, String[] selectionArgs, String[] columns, String table) 
	{
		/* The SQLiteBuilder provides a map for all possible columns requested to
		 * actual columns in the database, creating a simple column alias mechanism
		 * by which the ContentProvider does not need to know the real column names
		 */
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(table);

		Cursor cursor = builder.query(mDatabaseOpenHelper.getWritableDatabase(),
				columns, selection, selectionArgs, null, null, null);

		if (cursor == null) 
		{
			return null;
		} else if (!cursor.moveToFirst()) 
		{
			cursor.close();
			return null;
		}

		return cursor;
	}

	/**
	 * This creates/opens the database.
	 */
	private static class ClienteDatabaseOpenHelper extends SQLiteOpenHelper 
	{

		private final Context mHelperContext;
		private SQLiteDatabase mDatabase;


		ClienteDatabaseOpenHelper(Context context) 
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			mHelperContext = context;

			getWritableDatabase();					
		}						

		public long addFiscalizacao(Fiscalizacao fiscalizacao) 
		{
			long idFiscalizacao = -1;

			if(fiscalizacao!=null)
			{
				ContentValues initialValues = new ContentValues();	
				if(fiscalizacao.getMunicipio()!=null)
					initialValues.put(FISCALIZACAO_MUNICIPIO, fiscalizacao.getMunicipio());
				if(fiscalizacao.getEstado()!=null)
					initialValues.put(FISCALIZACAO_ESTADO, fiscalizacao.getEstado());
				if(fiscalizacao.getZonaEleitoral()!=null)
					initialValues.put(FISCALIZACAO_ZONAELEITORAL, fiscalizacao.getZonaEleitoral());
				if(fiscalizacao.getLocalDaVotacao()!=null)
					initialValues.put(FISCALIZACAO_LOCALDAVOTACAO, fiscalizacao.getLocalDaVotacao());
				if(fiscalizacao.getSecaoEleitoral()!=null)
					initialValues.put(FISCALIZACAO_SECAOELEITORAL, fiscalizacao.getSecaoEleitoral());
				if(fiscalizacao.getPodeEnviarRedeDados()!=null)
					initialValues.put(FISCALIZACAO_PODEENVIARREDEDADOS, fiscalizacao.getPodeEnviarRedeDados());
				if(fiscalizacao.getStatusDoEnvio()!=null)
					initialValues.put(FISCALIZACAO_STATUSDOENVIO, fiscalizacao.getStatusDoEnvio());
				if(fiscalizacao.getData()!=null)
					initialValues.put(FISCALIZACAO_DATA, fiscalizacao.getData());

				if(mDatabase!=null)
				{
					idFiscalizacao = mDatabase.insert(TABELA_FISCALIZACAO, null, initialValues);
					if(idFiscalizacao>=0l)
					{
						fiscalizacao.setIdFiscalizacao(idFiscalizacao);

						ArrayList<String> picturePathList = fiscalizacao.getPicturePathList();
						if(picturePathList!=null)
						{
							for(String picturePath : picturePathList)
							{
								addPicturePath(idFiscalizacao,picturePath);
							}
						}

						ArrayList<String> picture30PCPathList = fiscalizacao.getPicture30PCPathList();
						if(picture30PCPathList!=null)
						{
							for(String picture30PCPath : picture30PCPathList)
							{
								addPicture30PCPath(idFiscalizacao,picture30PCPath);
							}
						}

						ArrayList<String> pictureURLList = fiscalizacao.getPictureURLList();
						if(pictureURLList!=null)
						{
							for(String pictureURL : pictureURLList)
							{
								addPictureURL(idFiscalizacao,pictureURL);
							}
						}

					}
				}
			}

			return idFiscalizacao ;
		}

		private long addPictureURL(long idFiscalizacao, String pictureURL) 
		{
			long id = -1;
			
			ContentValues initialValues = new ContentValues();	
			initialValues.put(PICTUREURL_IDFISCALIZACAO,idFiscalizacao);
			initialValues.put(PICTUREURL_PICTUREURL,pictureURL);
			
			if(mDatabase!=null)
			{
				id = mDatabase.insert(TABELA_PICTUREURL, null, initialValues);
			}

			return id;
			
		}

		private long addPicture30PCPath(long idFiscalizacao, String picture30pcPath) 
		{
			long id = -1;
			
			ContentValues initialValues = new ContentValues();	
			initialValues.put(PICTURETHIRTYPCPATH_IDFISCALIZACAO,idFiscalizacao);
			initialValues.put(PICTURETHIRTYPCPATH_PICTURETHIRTYPCPATH,picture30pcPath);
			
			if(mDatabase!=null)
			{
				id = mDatabase.insert(TABELA_PICTURETHIRTYPCPATH, null, initialValues);
			}

			return id;

		}

		private long addPicturePath(long idFiscalizacao, String picturePath) 
		{
			long id = -1;
			
			ContentValues initialValues = new ContentValues();	
			initialValues.put(PICTUREPATH_IDFISCALIZACAO, idFiscalizacao);
			initialValues.put(PICTUREPATH_PICTUREPATH, picturePath);
			
			if(mDatabase!=null)
			{
				id = mDatabase.insert(TABELA_PICTUREPATH, null, initialValues);
			}

			return id;

		}
		
		public boolean updateStatusEnvio(Long idFiscalizacao, int statusEnvio) 
		{
			boolean resposta = false;
			
			if(idFiscalizacao!=null)
			{
				String whereClause = FISCALIZACAO_IDFISCALIZACAO + " = ?";
				String[] whereClauseArgs = new String[] {String.valueOf(idFiscalizacao)};
				
				ContentValues initialValues = new ContentValues();
				initialValues.put(FISCALIZACAO_STATUSDOENVIO, statusEnvio);
				
				if(mDatabase!=null)
				{
					resposta =  mDatabase.update(TABELA_FISCALIZACAO,initialValues, whereClause, whereClauseArgs)>0;				
				}
			}
			
			return resposta;
		}

		public boolean updatePodeEnviarRedeDeDados(Long idFiscalizacao, int podeEnviarRedeDeDados) 
		{
			boolean resposta = false;

			if(idFiscalizacao!=null)
			{
				String whereClause = FISCALIZACAO_IDFISCALIZACAO + " = ?";
				String[] whereClauseArgs = new String[] {String.valueOf(idFiscalizacao)};

				ContentValues initialValues = new ContentValues();
				initialValues.put(FISCALIZACAO_PODEENVIARREDEDADOS, podeEnviarRedeDeDados);

				if(mDatabase!=null)
				{
					resposta =  mDatabase.update(TABELA_FISCALIZACAO,initialValues, whereClause, whereClauseArgs)>0;				
				}
			}

			return resposta;
		}	

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			mDatabase = db;
			String sqls = loadDatabaseTableCreation();
			String[] sql  = sqls.split("[;]");

			for(int i=0; i < sql.length;i++)
			{
				String sqlcommand =sql[i];
				if(sqlcommand.length() > 0)
					mDatabase.execSQL(sqlcommand);
			}
		}

		@Override
		public void onOpen(SQLiteDatabase db) 
		{
			super.onOpen(db);
			mDatabase = db;
		}

		private String loadDatabaseTableCreation() 
		{
			final Resources resources = mHelperContext.getResources();
			InputStream inputStream = resources.openRawResource(R.raw.vocefiscaldb);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder sb = new StringBuilder();
			try 
			{
				String line = null;
				while ((line = reader.readLine()) != null) 
				{
					sb.append(line); 
				}
			} catch (IOException e) 
			{

			} finally 
			{
				try 
				{
					reader.close();
				} catch (IOException e) 
				{

				}
			}
			return sb.toString();
		}

		public boolean isOpen() 
		{
			if(mDatabase!=null)
				return mDatabase.isOpen();
			else
				return false;
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			if(oldVersion ==1 && newVersion ==2)
			{
				updateVersionDB1toVersionDB2(db);
			}
		}

		private void updateVersionDB1toVersionDB2(SQLiteDatabase db) 
		{
			// To be implemented in case the database gets modified
		}
	}
}
