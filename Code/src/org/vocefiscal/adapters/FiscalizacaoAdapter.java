/**
 * 
 */
package org.vocefiscal.adapters;

import java.util.ArrayList;

import org.vocefiscal.R;
import org.vocefiscal.bitmaps.ImageFetcher;
import org.vocefiscal.bitmaps.RecyclingImageView;
import org.vocefiscal.database.VoceFiscalDatabase;
import org.vocefiscal.models.Fiscalizacao;
import org.vocefiscal.models.enums.StatusEnvioEnum;
import org.vocefiscal.services.UploadManagerService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author andre
 *
 */
public class FiscalizacaoAdapter extends BaseAdapter 
{

	private ArrayList<Fiscalizacao> resultItemList;

	private boolean onLoading = false;
	private boolean onError = false;
	private Context mContext;	
	private String error = "Erro carregando fiscalizações";

	private ImageFetcher mImageFetcher;
	
	private VoceFiscalDatabase voceFiscalDatabase;

	public FiscalizacaoAdapter(Context mContext, ImageFetcher mImageFetcher,VoceFiscalDatabase voceFiscalDatabase) 
	{
		super();
		this.mContext = mContext;
		this.mImageFetcher = mImageFetcher;
		this.voceFiscalDatabase = voceFiscalDatabase;
	}

	@Override
	public int getViewTypeCount() 
	{
		return 4;
	}

	@Override
	public int getItemViewType(int position) 
	{
		if (onLoading)
			return 0; 
		if (onError)
			return 1; //msg de tempo insuficiente
		if (resultItemList==null || resultItemList.isEmpty())
			return 2; //msg de sem servidor
		return 3; //normal row
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() 
	{
		if(!onError &&  !onLoading && resultItemList!=null && !resultItemList.isEmpty())
		{
			return resultItemList.size();
		}
		else
		{
			return 1;
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Fiscalizacao getItem(int position) 
	{
		if(!onError && !onLoading && resultItemList!=null && !resultItemList.isEmpty())
		{
			return resultItemList.get(position);
		}
		else
		{
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position)
	{		
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public  View getView(int position, View convertView, ViewGroup parent) 
	{
		final Fiscalizacao fiscalizacao = getItem(position);
		ViewHolder holder=null;

		if(convertView==null)
		{

			if(getItemViewType(position) == 0 )
			{
				convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_row_loading, null);
			}
			else
				if(getItemViewType(position) == 1)
				{
					convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_row_info, null);
				}
				else
					if(getItemViewType(position) == 2)
					{
						convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_row_info, null);
					}
					else
						if(getItemViewType(position) == 3)
						{
							convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_fiscalizacao, parent, false);

							holder = new ViewHolder();						

							RecyclingImageView foto = (RecyclingImageView) convertView.findViewById(R.id.foto);
							holder.foto = foto;

							ProgressBar progress_bar_foto = (ProgressBar) convertView.findViewById(R.id.progress_bar_foto);
							holder.progress_bar_foto = progress_bar_foto;

							final ProgressBar upload_progress = (ProgressBar) convertView.findViewById(R.id.upload_progress);
							holder.upload_progress = upload_progress;

							TextView municipio_estado = (TextView) convertView.findViewById(R.id.municipio_estado);
							Typeface unisansheavy = Typeface.createFromAsset(mContext.getAssets(),"fonts/unisansheavy.otf");
							municipio_estado.setTypeface(unisansheavy);
							holder.municipio_estado = municipio_estado;														

							final TextView porcentagem_envio = (TextView) convertView.findViewById(R.id.porcentagem_envio);
							porcentagem_envio.setTypeface(unisansheavy);
							holder.porcentagem_envio = porcentagem_envio;

							TextView zona__local_secao_eleitoral = (TextView) convertView.findViewById(R.id.zona__local_secao_eleitoral);
							holder.zona__local_secao_eleitoral = zona__local_secao_eleitoral;		

							final ImageView status_envio = (ImageView) convertView.findViewById(R.id.status_envio);
							status_envio.setOnClickListener(new OnClickListener() 
							{								
								@Override
								public void onClick(View v) 
								{									
									if(fiscalizacao.getStatusDoEnvio()!=null)
									{
										if(fiscalizacao.getStatusDoEnvio().equals(StatusEnvioEnum.ENVIANDO.ordinal()))
										{
											fiscalizacao.setStatusDoEnvio(StatusEnvioEnum.ENVIAR.ordinal());

											if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
												voceFiscalDatabase.updateStatusEnvio(fiscalizacao.getIdFiscalizacao(),StatusEnvioEnum.ENVIAR.ordinal());

											status_envio.setImageResource(StatusEnvioEnum.getImageResource(fiscalizacao.getStatusDoEnvio()));
											porcentagem_envio.setVisibility(View.GONE);
											upload_progress.setVisibility(View.INVISIBLE);		

										}else if(fiscalizacao.getStatusDoEnvio().equals(StatusEnvioEnum.ENVIAR.ordinal()))										
										{										
											fiscalizacao.setStatusDoEnvio(StatusEnvioEnum.ENVIANDO.ordinal());

											if(voceFiscalDatabase!=null&&voceFiscalDatabase.isOpen())
												voceFiscalDatabase.updateStatusEnvio(fiscalizacao.getIdFiscalizacao(),StatusEnvioEnum.ENVIANDO.ordinal());
											
											//TODO Verificar Dados x Wi-Fi e perguntar novamente 

											Intent intent = new Intent(mContext, UploadManagerService.class);
											mContext.startService(intent);

											status_envio.setImageResource(StatusEnvioEnum.getImageResource(fiscalizacao.getStatusDoEnvio()));

											int numeroTotalDeFotos = 0;
											int numeroDeFotosEnviadas = 0;
											int porcentagemEnviado = 0;

											if(fiscalizacao.getPicturePathList()!=null)
												numeroTotalDeFotos = fiscalizacao.getPicturePathList().size();

											if(fiscalizacao.getPictureURLList()!=null)
												numeroDeFotosEnviadas = fiscalizacao.getPictureURLList().size();

											if(numeroTotalDeFotos>0)
												porcentagemEnviado = (int) ((numeroDeFotosEnviadas / (numeroTotalDeFotos*1.0f)) * 100);										

											porcentagem_envio.setText(porcentagemEnviado+"%");
											upload_progress.setProgress(porcentagemEnviado);

											porcentagem_envio.setVisibility(View.VISIBLE);
											upload_progress.setVisibility(View.VISIBLE);
										}
									}else
									{
										status_envio.setImageResource(StatusEnvioEnum.getImageResource(0));
										porcentagem_envio.setVisibility(View.GONE);
										upload_progress.setVisibility(View.INVISIBLE);
									}

								}
							});
							holder.status_envio = status_envio;

							convertView.setTag(holder);
						}
		}else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		if(getItemViewType(position)==3)
		{
			fillViewItem(fiscalizacao, holder.foto,holder.progress_bar_foto,holder.upload_progress,holder.municipio_estado,holder.status_envio,holder.porcentagem_envio,holder.zona__local_secao_eleitoral);
		}
		else
			if(getItemViewType(position)==2)
			{

				fillViewInfo("Não há fiscalizações disponíveis", convertView);
			}
			else
				if(getItemViewType(position)==1)
				{

					fillViewInfo(error, convertView);
				}
				else
					if(getItemViewType(position)==0)
					{
						fillViewInfoLoanding( convertView);
					}
		return convertView;
	}

	private void fillViewItem(Fiscalizacao fiscalizacao, RecyclingImageView foto, ProgressBar progress_bar_foto, ProgressBar upload_progress, TextView municipio_estado, ImageView status_envio, TextView porcentagem_envio, TextView zona__local_secao_eleitoral) 
	{	
		//Preparando elementos para receber as informacoes
		if(fiscalizacao!=null)
		{									
			//municipio,estado
			if(fiscalizacao.getMunicipio()!=null&&fiscalizacao.getEstado()!=null)
				municipio_estado.setText(fiscalizacao.getMunicipio()+","+fiscalizacao.getEstado());
			else
				municipio_estado.setText("");

			//zona eleitoral | local de votação | Seção eleitoral
			if(fiscalizacao.getZonaEleitoral()!=null&&fiscalizacao.getLocalDaVotacao()!=null&&fiscalizacao.getSecaoEleitoral()!=null)
				zona__local_secao_eleitoral.setText("Zona Eleitoral: "+fiscalizacao.getZonaEleitoral()+" | Local de Votação: "+fiscalizacao.getLocalDaVotacao()+" | Seção Eleitoral: "+fiscalizacao.getSecaoEleitoral());
			else
				zona__local_secao_eleitoral.setText("");

			//foto de capa
			if(mImageFetcher!=null && fiscalizacao.getPicturePathList()!=null&&fiscalizacao.getPicturePathList().size()>0)
			{
				mImageFetcher.loadImage(fiscalizacao.getPicturePathList().get(0), foto,progress_bar_foto);
			}else
			{
				foto.setImageResource(R.drawable.capa_conferir);
			}

			if(fiscalizacao.getStatusDoEnvio()!=null)
			{
				if(fiscalizacao.getStatusDoEnvio().equals(StatusEnvioEnum.ENVIANDO.ordinal()))
				{				
					status_envio.setImageResource(StatusEnvioEnum.getImageResource(fiscalizacao.getStatusDoEnvio()));

					int numeroTotalDeFotos = 0;
					int numeroDeFotosEnviadas = 0;
					int porcentagemEnviado = 0;

					if(fiscalizacao.getPicturePathList()!=null)
						numeroTotalDeFotos = fiscalizacao.getPicturePathList().size();

					if(fiscalizacao.getPictureURLList()!=null)
						numeroDeFotosEnviadas = fiscalizacao.getPictureURLList().size();

					if(numeroTotalDeFotos>0)
						porcentagemEnviado = (int) ((numeroDeFotosEnviadas / (numeroTotalDeFotos*1.0f)) * 100);										

					porcentagem_envio.setText(porcentagemEnviado+"%");
					upload_progress.setProgress(porcentagemEnviado);

					porcentagem_envio.setVisibility(View.VISIBLE);
					upload_progress.setVisibility(View.VISIBLE);
				}else
				{
					status_envio.setImageResource(StatusEnvioEnum.getImageResource(fiscalizacao.getStatusDoEnvio()));
					porcentagem_envio.setVisibility(View.GONE);
					upload_progress.setVisibility(View.INVISIBLE);									
				}
			}else
			{
				status_envio.setImageResource(StatusEnvioEnum.getImageResource(0));
				porcentagem_envio.setVisibility(View.GONE);
				upload_progress.setVisibility(View.INVISIBLE);
			}
		}else
		{
			municipio_estado.setText("");
			zona__local_secao_eleitoral.setText("");
			status_envio.setImageResource(StatusEnvioEnum.getImageResource(0));
			porcentagem_envio.setVisibility(View.GONE);
			upload_progress.setVisibility(View.INVISIBLE);
		}
	}

	private void fillViewInfo(String msg, View convertView) 
	{

		TextView textView = (TextView)convertView.findViewById(R.id.error);
		textView.setText(msg);
	}

	private void fillViewInfoLoanding(View convertView) 
	{
		TextView textView = (TextView)convertView.findViewById(R.id.loading);
		textView.setText("Buscando fiscalizações...");
	}

	public class ViewHolder
	{
		RecyclingImageView foto;
		ProgressBar progress_bar_foto;	
		ProgressBar upload_progress;
		TextView municipio_estado;
		ImageView status_envio;
		TextView porcentagem_envio;
		TextView zona__local_secao_eleitoral;
	}

	public boolean isOnLoading() 
	{
		return onLoading;
	}

	public void setOnLoading(boolean onLoading) 
	{
		this.onLoading = onLoading;
	}

	public boolean isOnError() 
	{
		return onError;
	}

	public void setOnError(boolean onError) 
	{
		this.onError = onError;
	}

	public ArrayList<Fiscalizacao> getResultItemList() 
	{
		return resultItemList;
	}

	public void setResultItemList(ArrayList<Fiscalizacao> resultItemList) 
	{
		this.resultItemList = resultItemList;
	}

	public String getError() 
	{
		return error;
	}

	public void setError(String error) 
	{
		this.error = error;
	}
}
