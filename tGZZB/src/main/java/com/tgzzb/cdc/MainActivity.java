package com.tgzzb.cdc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.tgzzb.cdc.LoginActivity.UserPermission;
import com.tgzzb.cdc.utils.Commons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemClickListener {

	private String[] titles;// gride
	private int[] icons;// gride item icon//
	private GridView gvInfo;
	private ArrayList<Map<String, Object>> data_list;// gride item
	// private ProgressDialog m_pDialog;
	private Context context;
	long preTime;
	int delaySecond = 2000; // 默认两次点击的间隔为 500 毫秒

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		initData();
	}

	private void initData() {
		context = this;
		ArrayList<UserPermission> permissions = MyApp.getPermissions();
		if (permissions != null) {

			titles = new String[permissions.size()];
			icons = new int[permissions.size()];

			for (int i = 0; i < permissions.size(); i++) {
				String fun = permissions.get(i).getFun();
				if (TextUtils.equals(fun, "APP_MRQH")) {
					titles[i] = "Mr取货";
					icons[i] = R.drawable.main_mr;
				}
				if (TextUtils.equals(fun, "APP_SJCZ")) {
					titles[i] = "司机操作";
					icons[i] = R.drawable.main_sjcz;
				}
				if (TextUtils.equals(fun, "APP_BGCZ")) {
					titles[i] = "报关操作";
					icons[i] = R.drawable.main_bgcz;
				}
				if (TextUtils.equals(fun, "APP_KACZ")) {
					titles[i] = "口岸代理";
					icons[i] = R.drawable.main_kadl;
				}
			}

			data_list = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < icons.length; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("image", icons[i]);
				map.put("text", titles[i]);
				data_list.add(map);
			}
			String[] from = { "image", "text" };
			int[] to = { R.id.image, R.id.text };
			SimpleAdapter sim_adapter = new SimpleAdapter(this, data_list, R.layout.item_gv_main, from, to);
			gvInfo.setAdapter(sim_adapter);
		} else {
			Commons.ShowToast(context, "没有获取到权限信息。");
		}
	}

	private void initViews() {
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("首页");
		findViewById(R.id.title_left).setVisibility(View.INVISIBLE);

		// m_pDialog = new ProgressDialog(this);
		// // 设置进度条样式
		// m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		gvInfo = (GridView) findViewById(R.id.gvInfo);
		gvInfo.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		TextView tv = (TextView) arg1.findViewById(R.id.text);
		
		if(TextUtils.equals(tv.getText().toString(),"Mr取货")){
			startActivity(new Intent(context, MrActivity.class));
		}
		if(TextUtils.equals(tv.getText().toString(),"司机操作")){
			startActivity(new Intent(context, DriverActivity.class));
		}
		if(TextUtils.equals(tv.getText().toString(),"报关操作")){
			startActivity(new Intent(context, BGCZActivity.class));
		}
		if(TextUtils.equals(tv.getText().toString(),"口岸代理")){
			startActivity(new Intent(context, KADLActivity.class));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {

			long lastTime = System.currentTimeMillis();
			boolean flag = lastTime - preTime < delaySecond ? true : false;
			preTime = lastTime;
			if (!flag) {
				Commons.ShowToast(context, "再次点击返回键退出程序");
				return false;
			} else {
				finish();
				//System.exit(0);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
