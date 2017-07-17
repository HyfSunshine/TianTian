package com.tgzzb.cdc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.tgzzb.cdc.adapter.BGCZ_Lv_Adapter;
import com.tgzzb.cdc.bean.BGCZItem;
import com.tgzzb.cdc.interfaces.MySingleClickListener;
import com.tgzzb.cdc.utils.Commons;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zxing.android.CaptureActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import okhttp3.Call;

public class BGCZActivity extends Activity implements OnClickListener {

	private ListView lv;
	private BGCZ_Lv_Adapter adapter;
	private CheckBox ckb_yjd;
	private CheckBox ckb_ych;
	private CheckBox ckb_wjd;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bgcz);
		context = this;
		initViews();
		if (savedInstanceState != null) {
			ArrayList<BGCZItem> datas = savedInstanceState.getParcelableArrayList("datas");
			adapter.setDatas(datas);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("datas", adapter.getDatas());
		outState.putBoolean("status1", ckb_yjd.isChecked());
		outState.putBoolean("status2", ckb_ych.isChecked());
		outState.putBoolean("status3", ckb_wjd.isChecked());
	}

	private void initViews() {
		Commons.setTitle(this, getApplication().getResources().getString(R.string.bgcz_title), this);
		findViewById(R.id.btn_scan).setOnClickListener(this);
		ckb_yjd = (CheckBox) findViewById(R.id.ckb_yjd);
		ckb_yjd.setOnClickListener(this);
		ckb_ych = (CheckBox) findViewById(R.id.ckb_ych);
		ckb_ych.setOnClickListener(this);
		ckb_wjd = (CheckBox) findViewById(R.id.ckb_wjd);
		ckb_wjd.setOnClickListener(this);
		lv = (ListView) findViewById(R.id.lv);
		adapter = new BGCZ_Lv_Adapter(this);
		lv.setAdapter(adapter);
		findViewById(R.id.btn_submit).setOnClickListener(new MySingleClickListener() {

			private String status;

			@Override
			protected void onSingleClick(View view) {

				if (ckb_yjd.isChecked()) {
					status = "已接单";
				} else if (ckb_ych.isChecked()) {
					status = "已抽号";
				} else if (ckb_wjd.isChecked()) {
					status = "未接单";
				} else {
					Commons.ShowToast(context, "请选择您要进行的操作！");
					return;
				}
				if (adapter.getDatas().size() == 0) {
					Commons.ShowToast(context, "没有可操作的报关单。");
					return;
				}
				Gson g = new Gson();
				try {
					OkHttpUtils.post().url(getResources().getString(R.string.BGXCUpdate))
							.addParams("jsonbgdhurl", g.toJson(adapter.getDatas()))
							.addParams("statusurl", URLEncoder.encode(status, "utf-8")).build()
							.execute(new StringCallback() {

						@Override
						public void onResponse(String arg0, int arg1) {
							String jsonData = Commons.parseXML(arg0);
							try {
								if (Integer.parseInt(jsonData) == 1) {
									// 操作成功！
									Commons.ShowDialog(context, "报关操作", "操作成功！");
								} else {
									// 失败
									if (jsonData != null) {
										Commons.ShowDialog(context, "报关操作", "单号：" + jsonData + " 操作失败！");
									}
								}
							} catch (Exception e) {
								// 失败
								if (jsonData != null) {
									Commons.ShowDialog(context, "报关操作", "单号：" + jsonData + " 操作失败！");
								}
							}
						}

						@Override
						public void onError(Call arg0, Exception arg1, int arg2) {
							Commons.ShowDialog(context, "报关操作", "请求超时！");
						}
					});
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("TGZZB", "requestCode = " + requestCode);
		if (data != null) {
			String result = data.getStringExtra("result");
			Log.d("TGZZB", "result = " + result);
//			if (result.length() != 10) {
//				Commons.ShowToast(context, "不是正确的报关单号，请重新扫描。");
//				return;
//			}
			adapter.addData(new BGCZItem(result));
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_scan:
			startActivityForResult(new Intent(this, CaptureActivity.class), 10011);
			break;
		case R.id.btn_submit:
			break;
		case R.id.title_left:
			finish();
			break;
		case R.id.ckb_yjd:
			if (ckb_yjd.isChecked()) {
				ckb_ych.setChecked(false);
				ckb_wjd.setChecked(false);
			}
			adapter.notifyDataSetChanged();
			break;
		case R.id.ckb_ych:
			if (ckb_ych.isChecked()) {
				ckb_yjd.setChecked(false);
				ckb_wjd.setChecked(false);

			}
			adapter.notifyDataSetChanged();
			break;
		case R.id.ckb_wjd:
			if (ckb_wjd.isChecked()) {
				ckb_yjd.setChecked(false);
				ckb_ych.setChecked(false);

			}
			adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}
}
