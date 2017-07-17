package com.tgzzb.cdc.adapter;

import java.util.ArrayList;

import com.tgzzb.cdc.R;
import com.tgzzb.cdc.bean.BGCZItem;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BGCZ_Lv_Adapter extends BaseAdapter{

	/**
	 * 
	 */
	private Activity context;
	private ArrayList<BGCZItem> datas;

	public BGCZ_Lv_Adapter(Activity context) {
		super();
		this.context = context;
		datas = new ArrayList<BGCZItem>();
//		datas.add(new BGCZItem("149287873"));
//		datas.add(new BGCZItem("4795sd658"));
//		datas.add(new BGCZItem("479536658"));
//		datas.add(new BGCZItem("479554658"));
	}

	public ArrayList<BGCZItem> getDatas() {
		return datas;
	}

	public void setDatas(ArrayList<BGCZItem> datas) {
		this.datas = datas;
	}
	public ArrayList<BGCZItem> addData(BGCZItem item) {
		boolean contain = false;
		for(BGCZItem ite : datas){
			if(ite.getBgdh().equals(item.getBgdh())){
				contain = true;
			}
		}
		if(!contain){
			datas.add(item);
		}
		return datas;
	}
	
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.bgcz_lv_item, null);
			holder.tv_bgdh = (TextView) convertView.findViewById(R.id.tv_bgdh);
			holder.iv_del =  (ImageView) convertView.findViewById(R.id.iv_del);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_bgdh.setText(datas.get(position).getBgdh());
		holder.iv_del.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				datas.remove(position);
				notifyDataSetChanged();
			}
		});
		return convertView;
	}
	class ViewHolder{
		TextView tv_bgdh;
		ImageView iv_del;
	}
}
