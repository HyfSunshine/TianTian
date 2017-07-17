package com.tgzzb.cdc;

import java.util.ArrayList;

import com.google.gson.reflect.TypeToken;
import com.tgzzb.cdc.adapter.DriverCheckPicAdapter;
import com.tgzzb.cdc.bean.DriverIMGAddress;
import com.tgzzb.cdc.imagepicker.GalleryActivity;
import com.tgzzb.cdc.utils.Commons;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import okhttp3.Call;

public class DriverCheckPicActivity extends Activity implements OnClickListener, OnItemClickListener {
	private Context context;

	private DriverCheckPicAdapter adapter;
	private GridView grid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mrcheckpic);
		context = this;
		initViews();
		grid = (GridView) findViewById(R.id.mr_checkpic_gr);

		adapter = new DriverCheckPicAdapter(context);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(this);
		Intent intent = getIntent();
		final String billCode = intent.getStringExtra("billcode");

		OkHttpUtils.post().url(getResources().getString(R.string.GetImagAdd)).addParams("billcodeurl", billCode).build()
				.execute(new StringCallback() {

					@Override
					public void onResponse(String arg0, int arg1) {
						String jsonData = Commons.parseXML(arg0);
						ArrayList<DriverIMGAddress> imgadds = Commons.parseJsonList(jsonData,
								new TypeToken<ArrayList<DriverIMGAddress>>() {
						}.getType());
						if (imgadds != null) {
							adapter.setImgAddress(imgadds);
							adapter.notifyDataSetChanged();
						} else {
							Commons.ShowToast(context, "您还没有上传图片!");
							grid.setVisibility(View.GONE);
							findViewById(R.id.ll_no_pic).setVisibility(View.VISIBLE);
						}

					}

					@Override
					public void onError(Call arg0, Exception arg1, int arg2) {
						Commons.ShowToast(context, "请检查您的网络连接。");
						grid.setVisibility(View.GONE);
						findViewById(R.id.ll_no_pic).setVisibility(View.VISIBLE);
					}

				});
	}

	private void initViews() {
		Commons.setTitle(this, "查看图片", this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Intent intent = new Intent(context, GalleryActivity.class);
		intent.putExtra("fromAddress", "DriverCheckPicActivity");
		intent.putStringArrayListExtra("imgUrls", adapter.getImgUrls());
		intent.putExtra("position", "1");
		intent.putExtra("ID", position);
		startActivity(intent);

	}

}
