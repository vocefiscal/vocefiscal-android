/**
 * 
 */
package org.vocefiscal.adapters;

import java.util.ArrayList;

import org.vocefiscal.R;
import org.vocefiscal.bitmaps.ImageFetcher;
import org.vocefiscal.bitmaps.RecyclingImageView;
import org.vocefiscal.models.Fiscalizacao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
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

	public FiscalizacaoAdapter(Context mContext, ImageFetcher mImageFetcher) 
	{
		super();
		this.mContext = mContext;
		this.mImageFetcher = mImageFetcher;
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
		Fiscalizacao item = getItem(position);
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
							
							ProgressBar upload_progress = (ProgressBar) convertView.findViewById(R.id.upload_progress);
							holder.upload_progress = upload_progress;
							
							TextView municipio_estado = (TextView) convertView.findViewById(R.id.municipio_estado);
							holder.municipio_estado = municipio_estado;
							
							ImageView status_envio = (ImageView) convertView.findViewById(R.id.status_envio);
							holder.status_envio = status_envio;
							
							TextView porcentagem_envio = (TextView) convertView.findViewById(R.id.porcentagem_envio);
							holder.porcentagem_envio = porcentagem_envio;
							
							TextView zona__local_secao_eleitoral = (TextView) convertView.findViewById(R.id.zona__local_secao_eleitoral);
							holder.zona__local_secao_eleitoral = zona__local_secao_eleitoral;														

							convertView.setTag(holder);
						}
		}else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		if(getItemViewType(position)==3)
		{
			fillViewItem(item, holder.foto,holder.progress_bar_foto,holder.upload_progress,holder.municipio_estado,holder.status_envio,holder.porcentagem_envio,holder.zona__local_secao_eleitoral);
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
			if(mImageFetcher!=null && fiscalizacao!=null)
			{
				mImageFetcher.loadImage(fiscalizacao, foto,progress_bar_foto);
			}		
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
