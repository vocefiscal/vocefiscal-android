/**
 * 
 */
package org.vocefiscal.adapters;

import java.util.ArrayList;

import org.vocefiscal.R;
import org.vocefiscal.bitmaps.ImageFetcher;
import org.vocefiscal.bitmaps.RecyclingImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author andre
 *
 */
public class FotoAdapter extends BaseAdapter 
{
	private ArrayList<String> resultItemList;

	private boolean onLoading = false;
	private boolean onError = false;
	private Context mContext;	
	private String error = "Erro carregando fotos";

	private ImageFetcher mImageFetcher;


	public FotoAdapter(Context mContext, ImageFetcher mImageFetcher) 
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
	public String getItem(int position) 
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
		String item = getItem(position);
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
							convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_foto, parent, false);

							holder = new ViewHolder();						

							RecyclingImageView foto = (RecyclingImageView) convertView.findViewById(R.id.foto);
							holder.foto = foto;

							ProgressBar progress_bar_foto = (ProgressBar) convertView.findViewById(R.id.progress_bar_foto);
							holder.progress_bar_foto = progress_bar_foto;

							convertView.setTag(holder);
						}
		}else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		if(getItemViewType(position)==3)
		{
			fillViewItem(item, holder.foto,holder.progress_bar_foto);
		}
		else
			if(getItemViewType(position)==2)
			{

				fillViewInfo("Não há fotos disponíveis", convertView);
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

	private void fillViewItem(String item, RecyclingImageView foto, ProgressBar progress_bar_foto) 
	{
		//Preparando elementos para receber as informacoes
		if(item!=null)
		{
			if(mImageFetcher!=null && item!=null)
			{
				mImageFetcher.loadImage(item, foto,progress_bar_foto);
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
		textView.setText("Buscando fotos...");
	}

	public class ViewHolder
	{
		RecyclingImageView foto;
		ProgressBar progress_bar_foto;
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

	public ArrayList<String> getResultItemList() 
	{
		return resultItemList;
	}

	public void setResultItemList(ArrayList<String> resultItemList) 
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
