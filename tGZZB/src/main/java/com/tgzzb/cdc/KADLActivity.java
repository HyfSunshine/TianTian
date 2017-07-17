package com.tgzzb.cdc;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.tgzzb.cdc.adapter.KADL_Lv_Adapter;
import com.tgzzb.cdc.bean.KADLItem;
import com.tgzzb.cdc.interfaces.MySingleClickListener;
import com.tgzzb.cdc.utils.Commons;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zxing.android.CaptureActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import okhttp3.Call;

public class KADLActivity extends Activity implements OnClickListener {

	private ListView lv;
	private Context context;
	private KADL_Lv_Adapter adapter;
//	private CheckBox ckb_ycd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kadl);
		context = this;
		initViews();
		if (savedInstanceState != null) {
			ArrayList<KADLItem> datas = savedInstanceState.getParcelableArrayList("datas");
			adapter.setDatas(datas);
			// adapter.setStatus(savedInstanceState.getBoolean("status"));
			adapter.notifyDataSetChanged();
		}
	}

	private void initViews() {
		Commons.setTitle(this, "口岸代理", this);
		lv = (ListView) findViewById(R.id.lv);
		adapter = new KADL_Lv_Adapter(context);
		lv.setAdapter(adapter);
//		ckb_ycd = (CheckBox) findViewById(R.id.ckb_ycd);
//		ckb_ycd.setOnClickListener(this);
		findViewById(R.id.btn_scan).setOnClickListener(this);

		findViewById(R.id.btn_submit).setOnClickListener(new MySingleClickListener() {

			@Override
			protected void onSingleClick(View view) {

//				if (!ckb_ycd.isChecked()) {
//					Commons.ShowToast(context, "请选择您要进行的操作！");
//					return;
//				}
				if(adapter.getDatas().size()==0){
					Commons.ShowToast(context, "没有可操作的数据，请通过扫描添加。");
					return;
				}
				Gson g = new Gson();
				OkHttpUtils.post().url(getResources().getString(R.string.CDUpdate))
						.addParams("JsonEnterpriseCodeurl", g.toJson(adapter.getDatas())).build()
						.execute(new StringCallback() {

					@Override
					public void onResponse(String arg0, int arg1) {
						String jsonData = Commons.parseXML(arg0);
						try {
							if (Integer.parseInt(jsonData) == 1) {
								// 操作成功！
								Commons.ShowDialog(context, "口岸代理", "操作成功！");
							}
						} catch (Exception e) {
							// 失败
							if (jsonData != null) {
								Commons.ShowDialog(context, "口岸代理", "编号：" + jsonData + " 操作失败！");
							}
						}
					}

					@Override
					public void onError(Call arg0, Exception arg1, int arg2) {
						Commons.ShowDialog(context, "口岸代理", "请求超时！");
					}
				});
			}

		});

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("datas", adapter.getDatas());
//		outState.putBoolean("status", ckb_ycd.isChecked());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {
			String result = data.getStringExtra("result");
			adapter.addData(new KADLItem(result));
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			finish();
			break;
		case R.id.btn_scan:
			startActivityForResult(new Intent(this, CaptureActivity.class), 10011);
			break;
		default:
			break;
		}
	}
}
