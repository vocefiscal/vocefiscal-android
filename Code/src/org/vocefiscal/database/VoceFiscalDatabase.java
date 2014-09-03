/**
 * 
 */
package org.vocefiscal.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.vocefiscal.R;

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
	
	/**Picture path*/
	private static final String TABELA_PICTUREPATH = "picturepath";
	//CAMPOS	
	private static final String PICTUREPATH_IDPICTUREPATH = "idpicturepath";	
	private static final String PICTUREPATH_IDFISCALIZACAO  = "idfiscalizacao";
	private static final String PICTUREPATH_PICTUREPATH  = "picturepath";	
	
	/**Picture path*/
	private static final String TABELA_PICTURETHIRTYPCPATH = "picturethirtypcpath";
	//CAMPOS	
	private static final String PICTURETHIRTYPCPATH_IDPICTURETHIRTYPCPATH = "idpicturethirtypcpath";	
	private static final String PICTURETHIRTYPCPATH_IDFISCALIZACAO  = "idfiscalizacao";
	private static final String PICTURETHIRTYPCPATH_PICTURETHIRTYPCPATH  = "picturethirtypcpath";	
	
	/**Picture path*/
	private static final String TABELA_PICTUREURL = "pictureurl";
	//CAMPOS	
	private static final String PICTUREURL_IDPICTUREURL = "idpictureurl";	
	private static final String PICTUREURL_IDFISCALIZACAO  = "idfiscalizacao";
	private static final String PICTUREURL_PICTUREURL  = "pictureurl";	

	private static ClienteDatabaseOpenHelper mDatabaseOpenHelper;

	public VoceFiscalDatabase(Context context)
	{
		if(mDatabaseOpenHelper ==null || !mDatabaseOpenHelper.isOpen())
			mDatabaseOpenHelper = new ClienteDatabaseOpenHelper(context);
	}
//	
//	public long addRide(Ride ride) 
//	{
//		return mDatabaseOpenHelper.addRide(ride);		
//	}	
//	
//	public boolean updateRideStatus(Long id, String status)
//	{
//		return mDatabaseOpenHelper.updateRideStatus(id, status);
//		
//	}	
	

//	public List<Amigo> getAmigos() 
//	{
//		List<Amigo> amigos = null;
//
//		try 
//		{
//			Cursor amigosCursor =  query(null, null, null, TABELA_AMIGO);
//
//			if (amigosCursor != null) 
//			{
//				amigos = new ArrayList<Amigo>();
//				do
//				{
//					Amigo amigo = new Amigo();
//
//					int index = amigosCursor.getColumnIndex(AMIGO_NOME);
//					if(!amigosCursor.isNull(index))
//					{
//						String nome = amigosCursor.getString(index);
//						amigo.setName(nome);
//					}				
//
//					index = amigosCursor.getColumnIndex(AMIGO_EMAIL);
//					if(!amigosCursor.isNull(index))
//					{
//						String email = amigosCursor.getString(index);
//						amigo.setEmail(email);
//					}					
//
//					index = amigosCursor.getColumnIndex(AMIGO_ISCONVIDADO);
//					if(!amigosCursor.isNull(index))
//					{
//						int isConvidado = amigosCursor.getInt(index);
//						if(isConvidado==0)
//							amigo.setIsConvidado(false);
//						else if(isConvidado==1)
//							amigo.setIsConvidado(true);
//					}				
//
//					amigos.add(amigo);
//				}while(amigosCursor.moveToNext());	
//				amigosCursor.close();
//			}
//
//		}catch (Exception e) 
//		{
//
//		}
//
//		return amigos ;
//	}
//
//	public Amigo getAmigo(String name, String email) 
//	{
//		Amigo amigo = null;
//
//		try 
//		{
//			String selection = null;
//			String[] selectionArgs = null;
//
//			if(name!=null)
//			{
//				selection = AMIGO_NOME + " = ? AND "+ AMIGO_EMAIL + " = ?";
//				selectionArgs = new String[] {name,email};
//
//				Cursor amigosCursor =  query(selection, selectionArgs, null, TABELA_AMIGO);
//
//				if (amigosCursor != null) 
//				{
//					amigo = new Amigo();
//
//					amigo.setName(name);
//
//					amigo.setEmail(email);
//
//					int index = amigosCursor.getColumnIndex(AMIGO_ISCONVIDADO);
//					if(!amigosCursor.isNull(index))
//					{
//						int isConvidado = amigosCursor.getInt(index);
//						if(isConvidado==0)
//							amigo.setIsConvidado(false);
//						else if(isConvidado==1)
//							amigo.setIsConvidado(true);
//					}					
//
//					amigosCursor.close();
//				}
//			}
//
//		}catch (Exception e) 
//		{
//
//		}
//
//		return amigo ;
//	}

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

//		public boolean updateRideStatus(Long id, String status) 
//		{
//			boolean resposta = false;
//
//			if(id!=null&&status!=null)
//			{
//				String whereClause = RIDE_IDRIDE + " = ?";
//				String[] whereClauseArgs = new String[] {String.valueOf(id)};
//
//				ContentValues initialValues = new ContentValues();
//
//				initialValues.put(RIDE_STATUS,status);
//
//				if(mDatabase!=null)
//				{
//					resposta =  mDatabase.update(TABELA_RIDE,initialValues, whereClause, whereClauseArgs)>0;											
//				}					
//			}			
//			return resposta;
//		}
//
//		
//		public long addRide(Ride ride) 
//		{
//			long id = -1;
//
//			if(ride!=null)
//			{
//				ContentValues initialValues = new ContentValues();	
//
//				if(ride.get_id()!=null)
//					initialValues.put(RIDE_IDRIDE,ride.get_id());
//				if(ride.getDriverId()!=null)
//					initialValues.put(RIDE_IDDRIVER,ride.getDriverId());
//				if(ride.getId_pass()!=null)
//					initialValues.put(RIDE_IDPASS,ride.getId_pass());
//				if(ride.getLat_ini()!=null)
//					initialValues.put(RIDE_LATINI,ride.getLat_ini());
//				if(ride.getLng_ini()!=null)
//					initialValues.put(RIDE_LNGINI,ride.getLng_ini());
//				if(ride.getAddress1_ori()!=null)
//					initialValues.put(RIDE_ADDRESS1ORI,ride.getAddress1_ori());
//				if(ride.getAddress2_ori()!=null)
//					initialValues.put(RIDE_ADDRESS2ORI,ride.getAddress2_ori());
//				if(ride.getLat_fim()!=null)
//					initialValues.put(RIDE_LATFIM,ride.getLat_fim());
//				if(ride.getLng_fim()!=null)
//					initialValues.put(RIDE_LNGFIM,ride.getLng_fim());
//				if(ride.getAddress1_dest()!=null)
//					initialValues.put(RIDE_ADDRESS1DEST,ride.getAddress1_dest());
//				if(ride.getAddress2_dest()!=null)
//					initialValues.put(RIDE_ADDRESS2DEST,ride.getAddress2_dest());
//				if(ride.getValor_pago()!=null)
//					initialValues.put(RIDE_VALORPAGO,ride.getValor_pago());
//				if(ride.getValor_sugerido()!=null)
//					initialValues.put(RIDE_VALORSUGERIDO,ride.getValor_sugerido());
//				if(ride.getStatus()!=null)
//					initialValues.put(RIDE_STATUS,ride.getStatus());
//				if(ride.getLogPosition()!=null)
//				{
//					String logPosition = RouteUtils.encodePoly(ride.getLogPosition());
//					initialValues.put(RIDE_LOGPOSITION,logPosition);
//				}				
//				if(ride.getDate_ini()!=null)
//					initialValues.put(RIDE_DATEINI,ride.getDate_ini());
//				if(ride.getDate_fim()!=null)
//					initialValues.put(RIDE_DATEFIM,ride.getDate_fim());
//				if(ride.getNota_pass()!=null)
//					initialValues.put(RIDE_NOTAPASS,ride.getNota_pass());
//				if(ride.getNota_driver()!=null)
//					initialValues.put(RIDE_NOTADRIVER,ride.getNota_driver());
//				if(ride.getRota_sugerida()!=null)
//					initialValues.put(RIDE_ROTASUGERIDA,ride.getRota_sugerida());
//				if(ride.getEta()!=null)
//					initialValues.put(RIDE_ETA,ride.getEta());
//				if(ride.getReciboEnviadoDriver()!=null)
//				{
//					if(ride.getReciboEnviadoDriver())
//						initialValues.put(RIDE_RECIBOENVIADODRIVER,1);
//					else
//						initialValues.put(RIDE_RECIBOENVIADODRIVER,0);
//				}
//				
//				if(ride.getReciboEnviadoPass()!=null)
//				{
//					if(ride.getReciboEnviadoPass())
//						initialValues.put(RIDE_RECIBOENVIADOPASS,1);
//					else
//						initialValues.put(RIDE_RECIBOENVIADOPASS,0);
//				}		
//
//				if(mDatabase!=null)
//				{
//					id = mDatabase.insert(TABELA_RIDE, null, initialValues);
//				}
//			}
//
//			return id;
//		}


//		public long addUser(User user) 
//		{
//			long idUser = -1;
//
//			if(user!=null)
//			{
//				ContentValues initialValues = new ContentValues();	
//				if(user.getCar_model()!=null)
//					initialValues.put(USER_CARMODEL,user.getCar_model());
//				if(user.getCar_placa()!=null)
//					initialValues.put(USER_CARPLACA,user.getCar_placa());
//				if(user.getCompleted_rides()!=null)
//					initialValues.put(USER_COMPLETED_RIDES,user.getCompleted_rides());
//				if(user.getCpf()!=null)
//					initialValues.put(USER_CPF,user.getCpf());
//				if(user.getEmail()!=null)
//					initialValues.put(USER_EMAIL,user.getEmail());
//				if(user.getFbToken()!=null)
//					initialValues.put(USER_FBTOKEN,user.getFbToken());
//				if(user.getFbTokenValidity()!=null)
//					initialValues.put(USER_FBTOKENVALIDITY,user.getFbTokenValidity());
//				if(user.getFoto()!=null)
//					initialValues.put(USER_FOTO,user.getFoto());
//				if(user.getFoto_car()!=null)
//					initialValues.put(USER_FOTOCAR,user.getFoto_car());				
//				if(user.getGot_rides()!=null)
//					initialValues.put(USER_GOTRIDES,user.getGot_rides());
//				if(user.get_id()!=null)
//					initialValues.put(USER_IDUSER,user.get_id());
//				if(user.getIs_driver()!=null)
//				{
//					if(user.getIs_driver())
//						initialValues.put(USER_ISDRIVER,1);
//					else
//						initialValues.put(USER_ISDRIVER,0);
//				}
//				if(user.getLastServerUpdate()!=null)
//					initialValues.put(USER_LASTSERVERUPDATES,user.getLastServerUpdate());
//				if(user.getMember_since()!=null)
//					initialValues.put(USER_MEMBERSINCE,user.getMember_since());
//				if(user.getNome()!=null)
//					initialValues.put(USER_NOME,user.getNome());
//				if(user.getOS()!=null)
//					initialValues.put(USER_OS,user.getOS());
//				if(user.getPushToken()!=null)
//					initialValues.put(USER_PUSHTOKEN,user.getPushToken());
//				if(user.getRank_driver()!=null)
//					initialValues.put(USER_RANKDRIVER,user.getRank_driver());
//				if(user.getRank_pass()!=null)
//					initialValues.put(USER_RANKPASS,user.getRank_pass());
//				if(user.getTelefone()!=null)
//					initialValues.put(USER_TELEFONE,user.getTelefone());
//				if(user.getToken()!=null)
//					initialValues.put(USER_TOKEN,user.getToken());
//				if(user.getCarHasAir()!=null)
//				{
//					if(user.getCarHasAir())
//						initialValues.put(USER_CARHASAIR,1);
//					else
//						initialValues.put(USER_CARHASAIR,0);
//				}
//				if(user.getCarHasSound()!=null)
//				{
//					if(user.getCarHasSound())
//						initialValues.put(USER_CARHASSOUND, 1);
//					else
//						initialValues.put(USER_CARHASSOUND,0);
//				}
//				if(user.getCarDoors()!=null)
//					initialValues.put(USER_CARDOORS, user.getCarDoors());
//				if(user.getCarYear()!=null)
//					initialValues.put(USER_CARYEAR, user.getCarYear());
//
//				if(mDatabase!=null)
//				{
//					idUser = mDatabase.insert(TABELA_USER, null, initialValues);
//					if(idUser>=0l)
//					{
//						List<User> listaMotoristasBloqueados = user.getList_bloqueados();
//						if(listaMotoristasBloqueados!=null)
//						{
//							for(User motoristaBloqueado : listaMotoristasBloqueados)
//							{
//								addMotoristaBloqueado(user.get_id(),motoristaBloqueado.get_id());
//							}
//						}
//
//						List<User> listaMotoristasFavorito = user.getList_favorito();
//						if(listaMotoristasFavorito!=null)
//						{
//							for(User motoristaFavorito : listaMotoristasFavorito)
//							{
//								addMotoristaFavorito(user.get_id(),motoristaFavorito.get_id());
//							}
//						}
//					}
//				}
//			}
//
//			return idUser;
//		}

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
