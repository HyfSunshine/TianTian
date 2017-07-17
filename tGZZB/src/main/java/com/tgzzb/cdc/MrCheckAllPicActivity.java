package com.tgzzb.cdc;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.tgzzb.cdc.adapter.MrCheckPicGRAdapter;
import com.tgzzb.cdc.bean.Mr_Data;
import com.tgzzb.cdc.bean.Traffic_Message;
import com.tgzzb.cdc.imagepicker.GalleryActivity;
import com.tgzzb.cdc.imagepicker.ImageItem;
import com.tgzzb.cdc.imagepicker.Res;
import com.tgzzb.cdc.utils.Commons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class MrCheckAllPicActivity extends Activity implements OnClickListener {
	private Context context;
	private MrCheckPicGRAdapter adapter;
	private final int ADAPTER_CHANGE = 10001;
	private final int NOPIC = 10002;
	private GridView mr_checkpic_gr;
	public static ArrayList<ImageItem> checkBitmap;
	public static boolean fromCheckPicActivity = false;
	
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == ADAPTER_CHANGE) {
				Type typeOfT = new TypeToken<List<Traffic_Message>>() {
				}.getType();
				List<Traffic_Message> list = Commons.parseJsonList((String) msg.obj, typeOfT);
				adapter.setList(list);
				adapter.notifyDataSetChanged();
			}
			if (msg.what == NOPIC) {
				mr_checkpic_gr.setVisibility(View.GONE);
				findViewById(R.id.ll_no_pic).setVisibility(View.VISIBLE);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mrcheckpic);
		context = this;
		fromCheckPicActivity = true;
		initViews();
		Res.init(context);
		checkBitmap = new ArrayList<ImageItem>();
		Intent intent = getIntent();
		final Mr_Data data = (Mr_Data) intent.getSerializableExtra("Mr_data");
		 
		new Thread() {
			@Override
			public void run() {
				String result = Commons.getResponse(context, "GetImage", "dzhm", data.getDzhm(), "ljh", data.getLjh(),
						"carno", MyApp.currentUser.getMogilelogin());
				try {
					Integer.parseInt(result);
				 
					Message msg = new Message();
					msg.what = NOPIC;
					msg.obj = result;
					handler.sendMessage(msg);
				} catch (Exception e) {
					 
					if (result != null) {
						Message msg = new Message();
						msg.what = ADAPTER_CHANGE;
						msg.obj = result;
						handler.sendMessage(msg);
					}
				}
			};

		}.start();

	}

	private void initViews() {
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("查看照片");
		findViewById(R.id.title_left).setOnClickListener(this);
		mr_checkpic_gr = (GridView) findViewById(R.id.mr_checkpic_gr);

		// WindowManager wm = (WindowManager)
		// getSystemService(Context.WINDOW_SERVICE);
		// int width = wm.getDefaultDisplay().getWidth();
		// mr_checkpic_gr.setColumnWidth(width / 4);
		adapter = new MrCheckPicGRAdapter(context);
		mr_checkpic_gr.setAdapter(adapter);
		mr_checkpic_gr.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(context, GalleryActivity.class);
				intent.putExtra("fromAddress", "MrCheckAllPicActivity");
				intent.putExtra("position", "1");
				intent.putExtra("ID", position);
				startActivity(intent);

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			fromCheckPicActivity = false;
			finish();
			break;
		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
