package com.tgzzb.cdc.adapter;

import java.util.ArrayList;

import com.tgzzb.cdc.R;
import com.tgzzb.cdc.bean.KADLItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class KADL_Lv_Adapter extends BaseAdapter {

	private ArrayList<KADLItem> datas;
	private Context context;

	public KADL_Lv_Adapter(Context context) {
		super();
		this.context = context;
		datas = new ArrayList<KADLItem>();
//		for (int i = 0; i < 2; i++) {
//			datas.add(new KADLItem("QYBHXXXXXXXXXX"+i));
//		}
	}

	public ArrayList<KADLItem> getDatas() {
		return datas;
	}

	public void setDatas(ArrayList<KADLItem> datas) {
		this.datas = datas;
	}

	public ArrayList<KADLItem> addData(KADLItem item) {
		boolean contain = false;
		for (KADLItem ite : datas) {
			if (ite.getQybh().equals(item.getQybh())) {
				contain = true;
			}
		}
		if (!contain) {
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
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.kadl_lv_item, null);
			holder.tv_qybh = (TextView) convertView.findViewById(R.id.tv_qybh);
			holder.iv_del = (ImageView) convertView.findViewById(R.id.iv_del);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final String qybh = datas.get(position).getQybh();
		holder.tv_qybh.setText(qybh);
		holder.iv_del.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				datas.remove(position);
				notifyDataSetChanged();
			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView tv_qybh;
		ImageView iv_del;
	}
}
