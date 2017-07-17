package com.tgzzb.cdc.adapter;

import java.util.ArrayList;

import com.tgzzb.cdc.DriverCheckPicActivity;
import com.tgzzb.cdc.R;
import com.tgzzb.cdc.bean.DriverItem;

import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class DrivaerExpListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<DriverItem> datas;
	private SparseBooleanArray checkStatus;  

	public DrivaerExpListAdapter(Context context) {

		this.context = context;
		datas = new ArrayList<DriverItem>();
		checkStatus = new SparseBooleanArray();
	}

	public ArrayList<DriverItem> addDatas(DriverItem item) {
		if (!datas.contains(item)) {
			datas.add(item);
		}
		return datas;
	}
	public SparseBooleanArray getCheckStatus() {
		return checkStatus;
	}
	public void setCheckStatus(SparseBooleanArray checkStatus) {
		this.checkStatus = checkStatus;
	}

	public ArrayList<DriverItem> getDatas() {
		return datas;
	}

	public void setDatas(ArrayList<DriverItem> datas) {
		this.datas = datas;
		for (int i = 0; i < datas.size(); i++) {
			checkStatus.put(i, false);
		}
	}
	public void setNewDatas(ArrayList<DriverItem> datas) {
		this.datas = datas;
		//判断新数据和就数据是否数目相同。
		if(this.datas.size()!=datas.size()){
			for (int i = 0; i < datas.size(); i++) {
				checkStatus.put(i, false);
			}
		}
	}

//	public HashMap<Integer, Boolean> getCheckStates() {
//		return checkStates;
//	}
//
//	public void setCheckStates(HashMap<Integer, Boolean> checkStates) {
//		this.checkStates = checkStates;
//	}

	public void checkAll() {// 更改全部状态为选中；
		for(int i=0;i<checkStatus.size();i++){
			checkStatus.put(i, true);
		}
	}

	public void deleteAll() {// 更改全部状态为取消；
		for(int i=0;i<checkStatus.size();i++){
			checkStatus.put(i,false);
		}
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return datas.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return datas.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		ViewHolderF holderF = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.driver_explistitem_father, null);
			holderF = new ViewHolderF();
			holderF.tv_qyjc = (TextView) convertView.findViewById(R.id.tv_qyjc);
			holderF.tv_fdh = (TextView) convertView.findViewById(R.id.tv_fdh);
			holderF.tv_shdz = (TextView) convertView.findViewById(R.id.tv_shdz);
			holderF.tv_js = (TextView) convertView.findViewById(R.id.tv_js);
			holderF.ckb = (CheckBox) convertView.findViewById(R.id.ckb);

			convertView.setTag(holderF);
		} else {
			holderF = (ViewHolderF) convertView.getTag();
		}
		holderF.setDatas(datas.get(groupPosition));
 
		holderF.ckb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckBox ck = (CheckBox) v;
				if (ck.isChecked()) {
					checkStatus.put(groupPosition, true);
				} else {
					checkStatus.put(groupPosition, false);

				}
			}
		});
		holderF.ckb.setChecked(checkStatus.get(groupPosition));// 每次都从记录中读取状态并设置;
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ViewHolderC holderC = null;
		if (convertView == null) {
			holderC = new ViewHolderC();
			convertView = LayoutInflater.from(context).inflate(R.layout.driver_explistitem_child, null);
			holderC.tv_mz = (TextView) convertView.findViewById(R.id.tv_mz);
			holderC.tv_qybh = (TextView) convertView.findViewById(R.id.tv_qybh);
			holderC.tv_zdh = (TextView) convertView.findViewById(R.id.tv_zdh);
			holderC.tv_fph = (TextView) convertView.findViewById(R.id.tv_fph);
			holderC.tv_ywbh = (TextView) convertView.findViewById(R.id.tv_ywbh);
			holderC.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
			holderC.btn_checkpic =  (Button) convertView.findViewById(R.id.btn_checkpic);
			holderC.tv_isfolding = (TextView) convertView.findViewById(R.id.tv_isfolding);
			holderC.tv_remark = (TextView) convertView.findViewById(R.id.tv_remark);
			convertView.setTag(holderC);
		}else{
			holderC = (ViewHolderC) convertView.getTag();
		}
		holderC.setDatas(datas.get(groupPosition));
		holderC.btn_checkpic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,DriverCheckPicActivity.class);
				intent.putExtra("billcode", datas.get(groupPosition).getYwbh());
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	class ViewHolderF {
		TextView tv_qyjc;
		TextView tv_fdh;
		TextView tv_shdz;
		TextView tv_js;
		CheckBox ckb;

		void setDatas(DriverItem item) {
			tv_qyjc.setText(item.getQyjc());
			tv_fdh.setText(item.getFdh());
			tv_shdz.setText(item.getShdz());
			tv_js.setText(item.getJs());
		}
	}
	class ViewHolderC{
		TextView tv_mz;
		TextView tv_qybh;
		TextView tv_zdh;
		TextView tv_fph;
		TextView tv_ywbh;
		TextView tv_status;
		TextView tv_isfolding;
		TextView tv_remark;
		
		Button btn_checkpic;
		
		void setDatas(DriverItem item) {
			tv_ywbh.setText(item.getYwbh());
			tv_mz.setText(item.getMz());
			tv_qybh.setText(item.getQybh());
			tv_zdh.setText(item.getZdh());
			tv_fph.setText(item.getFph());
			tv_status.setText(item.getStatus());
			tv_isfolding.setText(item.getIsfolding());
			tv_remark.setText(item.getRemark());
		}
	}
}
