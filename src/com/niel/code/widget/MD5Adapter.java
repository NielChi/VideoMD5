package com.niel.code.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.niel.code.videomd5.R;

public class MD5Adapter extends BaseAdapter {
	private final String TAG = "ADAPTER";
	
	private Context mContext;
	private ArrayList<MD5Structure> item;
	
	private MD5Holder holder;

	public MD5Adapter(Context context, ArrayList<MD5Structure> sturcture) {
		this.mContext = context;
		item = (ArrayList<MD5Structure>) sturcture.clone();
	}
	
	public void setAdapterItem(ArrayList<MD5Structure> sturcture) {
		if(item != null) {
			if(item.size() != 0) {
				item.clear();
			}
			item = (ArrayList<MD5Structure>) sturcture.clone();
			notifyDataSetChanged();
		}
	}
	
	@Override
	public int getCount() {
		return item.size();
	}

	@Override
	public Object getItem(int arg0) {
		return item.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if(convertView == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.md5_adapter, null);
			holder = new MD5Holder();
			holder.videomd5_Path = (TextView) view.findViewById(R.id.videomd5_File_Path);
			holder.videomd5_Md5 = (TextView) view.findViewById(R.id.videpmd5_MD5);
			holder.videomd5_percentage = (TextView) view.findViewById(R.id.videomd5_Percentage);
			holder.videomd5_Progress = (ProgressBar) view.findViewById(R.id.videomd5_Progress);
			view.setTag(view.getId(), holder);
		} else {
			view = convertView;
			holder = (MD5Holder) view.getTag(view.getId());
		}
		
		if( (position % 2) == 0) {
			view.setBackgroundColor(Color.parseColor("#CCCCCC"));
		} else {
			view.setBackgroundColor(Color.parseColor("#ffffff"));
		}
		
		if(item.size() != 0) {
			if(item.get(position).getType() == MD5Structure.TYPE_FILE) {
				holder.videomd5_percentage.setVisibility(View.VISIBLE);
				holder.videomd5_Progress.setVisibility(View.VISIBLE);
				holder.videomd5_Path.setText(item.get(position).getPath());
				if(item.get(position).getStatus() == MD5Structure.MD5_EMPTY) {
					holder.videomd5_Md5.setText(item.get(position).getMD5());
					holder.videomd5_percentage.setText(item.get(position).getPercentage() + "%");
					holder.videomd5_Progress.setProgress(Integer.valueOf(item.get(position).getPercentage()));
				} else {
					if(item.get(position).getStatus() == MD5Structure.MD5_FINISH) {
						holder.videomd5_Md5.setText(item.get(position).getMD5());
						holder.videomd5_percentage.setText(item.get(position).getPercentage()+"%");
						holder.videomd5_Progress.setProgress(Integer.valueOf(item.get(position).getPercentage()));
					}
				}
			} else {
				holder.videomd5_Path.setText(item.get(position).getName());
				holder.videomd5_Md5.setText(item.get(position).getPath());
				holder.videomd5_percentage.setVisibility(View.GONE);
				holder.videomd5_Progress.setVisibility(View.GONE);
			}
		}
		return view;
	}
	
}