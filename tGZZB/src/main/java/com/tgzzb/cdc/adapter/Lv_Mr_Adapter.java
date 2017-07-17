
package com.tgzzb.cdc.adapter;

import java.util.ArrayList;
import java.util.List;

import com.tgzzb.cdc.MrCheckAllPicActivity;
import com.tgzzb.cdc.R;
import com.tgzzb.cdc.bean.Mr_Data;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Lv_Mr_Adapter extends BaseAdapter {

	private List<Mr_Data> datas;
	private Context context;

	public Lv_Mr_Adapter(Context context) {
		super();
		this.context = context;
		datas = new ArrayList<Mr_Data>();
	}

	public List<Mr_Data> getDatas() {
		return datas;
	}

	public void setDatas(List<Mr_Data> datas) {
		this.datas = datas;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_mr, null);
			holder = new ViewHolder();
			holder.tv_dzhm = (TextView) convertView.findViewById(R.id.tv_dzhm);
			holder.tv_ljh = (TextView) convertView.findViewById(R.id.tv_ljh);
			holder.tv_sl = (TextView) convertView.findViewById(R.id.tv_sl);
			holder.tv_checkpic = (TextView) convertView.findViewById(R.id.tv_checkpic);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.setAllView(datas.get(position));
		return convertView;
	}

	class ViewHolder {
		TextView tv_dzhm;
		TextView tv_ljh;
		TextView tv_sl;
		TextView tv_checkpic;


		private void setAllView(final Mr_Data data) {
			tv_dzhm.setText(data.getDzhm());
			tv_ljh.setText(data.getLjh());
			tv_sl.setText(String.valueOf(data.getRequireqty()));
			tv_checkpic.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,MrCheckAllPicActivity.class);
					intent.putExtra("Mr_data", data);
					context.startActivity(intent);
				}
			});
		}
	}
}
