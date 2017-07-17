package com.tgzzb.cdc;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tgzzb.cdc.adapter.DrivaerExpListAdapter;
import com.tgzzb.cdc.bean.DriverItem;
import com.tgzzb.cdc.bean.DriverYwbhDdcode;
import com.tgzzb.cdc.imagepicker.Bimp;
import com.tgzzb.cdc.imagepicker.GalleryActivity;
import com.tgzzb.cdc.imagepicker.ImageItem;
import com.tgzzb.cdc.imagepicker.PublicWay;
import com.tgzzb.cdc.interfaces.MySingleClickListener;
import com.tgzzb.cdc.utils.Commons;
import com.tgzzb.cdc.utils.SelectPicDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import okhttp3.Call;

public class DriverActivity extends Activity implements OnClickListener, OnChildClickListener, OnItemClickListener {

	private Context context;
	private ExpandableListView expList;
	private DrivaerExpListAdapter expAdapter;
	public static ArrayList<ImageItem> qsd_Datas = new ArrayList<ImageItem>();

	private final int UPLOAD_SUCCESS = 1005;
	private final int UPLOAD_FAILED = 1006;

	private ArrayList<DriverYwbhDdcode> thQsDatas;
	// private ArrayList<THQSData> qsDatas;
	private List<File> tempfiles = new ArrayList<File>();
	private ProgressDialog uploadPicDialog;
	private int numProgress = 1;

	private Handler handler = new Handler() {
		private int numProgress = 1;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPLOAD_SUCCESS:
				uploadPicDialog.setProgress(numProgress++);
				if (uploadPicDialog.getMax() == numProgress - 1) {
					// 所有图片上传完成
					uploadPicDialog.setMessage("操作完成！");
					uploadPicDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("确定");
					Commons.ShowToast(context, "操作完成！");
					getDriverData(true);
					for (int i = 0; i < expAdapter.getDatas().size(); i++) {
						expList.collapseGroup(i);
					}
				}
				break;

			case UPLOAD_FAILED:
				uploadPicDialog.dismiss();
				Commons.ShowDialog(context, "上传签收单", "上传失败，请稍后尝试!");
				break;

			default:
				break;
			}
		}
	};
	private View parentView;
	private final int MY_CAMERA_REQUEST_CODE = 10001;
	private QsdAdapter qsdAdapter;
	private SelectPicDialog selectPicDialog;
	private PopupWindow qsd_popWindow;
	private View qsd_popView;
	private GridView qsd_grid;
	private MySingleClickListener singleClickListener;
	private Button btn_yth;
	// private MySingleClickListener singleListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = DriverActivity.this;
		parentView = LayoutInflater.from(context).inflate(R.layout.activity_driver, null);
		setContentView(parentView);
		initMyClickListener();
		initViews();

		selectPicDialog = new SelectPicDialog(context, MY_CAMERA_REQUEST_CODE);
		getDriverData(false);

	}

	private void initMyClickListener() {
		singleClickListener = new MySingleClickListener() {

			@Override
			protected void onSingleClick(View view) {
				switch (view.getId()) {
				case R.id.btn_yth:
					YTH();
					break;
				case R.id.btn_positive:
					try {
						upLoadQsd();
					} catch (NotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
				}
			}
		};

	}

	private void initViews() {
		Commons.setTitle(this, getApplication().getResources().getString(R.string.driver_title), this);
		expList = (ExpandableListView) findViewById(R.id.explist);
		expList.setOnChildClickListener(this);
		expAdapter = new DrivaerExpListAdapter(this);
		expList.setAdapter(expAdapter);
		btn_yth = (Button) findViewById(R.id.btn_yth);
		btn_yth.setOnClickListener(singleClickListener);
		findViewById(R.id.btn_submit_qsd).setOnClickListener(this);

		CheckBox ckb = (CheckBox) findViewById(R.id.ckb);
		ckb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					expAdapter.checkAll();
				} else {
					expAdapter.deleteAll();
				}
				expAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			clearPic();
			finish();
			break;
		// case R.id.btn_yth:
		// YTH();
		// break;
		case R.id.btn_submit_qsd:
			show_qsdPopWindow();
			break;
		case R.id.btn_openCamera:
			if (qsd_Datas.size() >= 5) {
				Commons.ShowToast(context, "照片已超出可选张数。");
				break;
			}
			selectPicDialog.openCamera();
			break;
		case R.id.btn_openAlbum:
			selectPicDialog.openPhotoAlbum();
			break;
		case R.id.btn_positive:
			try {
				upLoadQsd();
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		case R.id.btn_negative:
			qsd_popWindow.dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		Intent intent = new Intent(context, DriverAddInfoActivity.class);
		intent.putExtra("DriverItem", expAdapter.getDatas().get(groupPosition));
		startActivity(intent);
		return false;
	}

	public void show_qsdPopWindow() {

		if (qsd_popWindow == null) {
			// Res.init(context);
			qsd_popView = LayoutInflater.from(context).inflate(R.layout.pop_upload_qsd, null);
			qsd_popView.findViewById(R.id.btn_openCamera).setOnClickListener(this);
			qsd_popView.findViewById(R.id.btn_openAlbum).setOnClickListener(this);
			qsd_popView.findViewById(R.id.btn_positive).setOnClickListener(singleClickListener);
			qsd_popView.findViewById(R.id.btn_negative).setOnClickListener(this);
			qsd_popWindow = new PopupWindow(context);
			qsd_popWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
			qsd_popWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
			qsd_popWindow.setContentView(qsd_popView);
			qsd_popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
			qsd_popWindow.setOutsideTouchable(false);
			qsd_popWindow.setFocusable(true);
			qsd_popWindow.setAnimationStyle(R.style.popwindow_anim_style);

			qsd_popWindow.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					Commons.changeWindowBg(DriverActivity.this, 1f);
				}
			});
			qsd_grid = (GridView) qsd_popView.findViewById(R.id.qsd_grid);
			qsd_grid.setOnItemClickListener(this);
			qsdAdapter = new QsdAdapter();
		}
		Commons.changeWindowBg(this, 0.5f);
		qsd_popWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
		qsd_grid.setAdapter(qsdAdapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0) {
			return;
		}
		switch (requestCode) {
		case MY_CAMERA_REQUEST_CODE:
			Uri imageUri = selectPicDialog.getImageUri();
			if (Bimp.tempSelectBitmap.size() < PublicWay.num && resultCode == RESULT_OK) {
				String[] proj = { MediaStore.Images.Media.DATA };
				// MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				Cursor actualImageCursor = this.getContentResolver().query(imageUri, proj, null, null, null);
				int actual_image_column_index = actualImageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				actualImageCursor.moveToFirst();
				String image_path = actualImageCursor.getString(actual_image_column_index);
				ImageItem takePhoto = new ImageItem();
				takePhoto.setImagePath(image_path);
				Log.d("TGZZB", "image_path = " + image_path);
				// takePhoto.setBitmap(BitmapFactory.decodeFile(image_path));
				Bimp.tempSelectBitmap.add(takePhoto);
				// qsdAdapter.datas.add(takePhoto);
				actualImageCursor.close();
			}

			break;
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (qsdAdapter != null) {
			qsd_Datas = Bimp.tempSelectBitmap;
			qsdAdapter.setDatas(qsd_Datas);
			qsdAdapter.notifyDataSetChanged();
		}
	}

	// class THQSData {
	//
	// String billcode;
	// String ddcode;
	//
	// public THQSData(String billcode, String ddcode) {
	// super();
	// this.billcode = billcode;
	// this.ddcode = ddcode;
	// }
	// }

	class QsdAdapter extends BaseAdapter {

		private ArrayList<ImageItem> datas;

		public QsdAdapter() {
			super();
			datas = new ArrayList<ImageItem>();
		}

		public ArrayList<ImageItem> getDatas() {
			return datas;
		}

		public void setDatas(ArrayList<ImageItem> datas) {
			this.datas = datas;
		}

		public void addDatas(ArrayList<ImageItem> datas) {
			this.datas.addAll(datas);
		}

		@Override
		public int getCount() {
			if (datas.size() == PublicWay.num) {
				return PublicWay.num;
			}
			return datas.size() == 0 ? 1 : datas.size();
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
				convertView = LayoutInflater.from(context).inflate(R.layout.item_published_grida, null);
				holder = new ViewHolder();
				holder.iv = (ImageView) convertView.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (datas.size() == 0) {// 如果没有图片显示一张预设图片
				holder.iv.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_pic));
			} else {// 有图就显示图
				holder.setImage(datas.get(position).getImagePath());
			}
			return convertView;
		}

		class ViewHolder {

			ImageView iv;

			void setImage(String url) {
				Glide.with(context).load(url).into(iv);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		if (qsdAdapter.datas.size() == 0) {
			return;
		}
		Bimp.tempSelectBitmap = qsd_Datas;

		Intent intent = new Intent(this, GalleryActivity.class);
		intent.putExtra("fromAddress", "DriverActivity");
		intent.putExtra("position", "1");
		intent.putExtra("ID", position);
		// intent.putParcelableArrayListExtra("currentBitmapList",
		// qsdAdapter.getDatas());
		startActivity(intent);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			clearPic();
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	public void clearPic() {
		Bimp.tempSelectBitmap = new ArrayList<ImageItem>();
		qsd_Datas.clear();// status

	}

	private void YTH() {

		thQsDatas = new ArrayList<DriverYwbhDdcode>();
		SparseBooleanArray checkStates = expAdapter.getCheckStatus();
		for (int i = 0; i < checkStates.size(); i++) {
			if (checkStates.get(i)) {
				DriverItem item = expAdapter.getDatas().get(i);
				thQsDatas.add(new DriverYwbhDdcode(item.getYwbh(), item.getDdcode()));
			}
		}
		if (thQsDatas.size() == 0) {
			Commons.ShowToast(context, "请先选择单号");
			return;
		}
		Gson g = new Gson();
		OkHttpUtils.post().url(getResources().getString(R.string.UpdateYsStatus))
				.addParams("billcodesJson", g.toJson(thQsDatas)).build().execute(new StringCallback() {

					@Override
					public void onResponse(String arg0, int arg1) {
						String jsonData = Commons.parseXML(arg0);
						try {
							int resultCode = Integer.parseInt(jsonData);
							if (resultCode == 1) {
								Commons.ShowDialog(context, "提货操作", "提货成功!");
								getDriverData(true);
								for (int i = 0; i < expAdapter.getDatas().size(); i++) {
									expList.collapseGroup(i);
								}
							} else {
								Commons.ShowDialog(context, "提货操作", "提货失败!");
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
							Commons.ShowDialog(context, "提货操作", "提货失败!");
						}
					}

					@Override
					public void onError(Call arg0, Exception arg1, int arg2) {
						Commons.ShowDialog(context, "提货操作", "请求超时!");
					}
				});
	}

	private void upLoadQsd() throws NotFoundException, UnsupportedEncodingException {

		thQsDatas = new ArrayList<DriverYwbhDdcode>();
		SparseBooleanArray checkStates = expAdapter.getCheckStatus();
		for (int i = 0; i < checkStates.size(); i++) {
			if (checkStates.get(i)) {
				DriverItem item = expAdapter.getDatas().get(i);
				thQsDatas.add(new DriverYwbhDdcode(item.getYwbh(), item.getDdcode()));
			}
		}
		if (thQsDatas.size() == 0) {
			Commons.ShowToast(context, "请先选择单号");
			return;
		}
		Gson g = new Gson();
		final String jstrBillCodes = g.toJson(thQsDatas);
		Log.d("TGZZB", "jstrBillCodes = " + jstrBillCodes);
		if (qsd_Datas.size() == 0) {
			Commons.ShowToast(context, "请先选择照片");
			return;
		} else {
			uploadPicDialog = Commons.getDialogLoadingPic(context, "上传签收单", qsd_Datas.size(), false);
			qsd_popWindow.dismiss();
			uploadPicDialog.show();

			new Thread() {
				@Override
				public void run() {
					super.run();

					for (int i = 0; i < qsd_Datas.size(); i++) {
						File tempFile = Commons.initFile();
						tempfiles.add(tempFile);
						String str64 = Commons.getImageBase64(qsd_Datas.get(i).getBitmap(), tempFile);
						String uploadResult = Commons.getResponse170(context, "UploadFile", "fileBytes", str64,
								"OrafileNameurl", Commons.getCurrentDaterTimeSSS() + ".jpg", "typeurl",
								expAdapter.getDatas().get(0).getType(), "billcodesJson", jstrBillCodes, "remarkurl", "",
								"filetypeurl", "签收单");
						Log.d("TGZZB", "uploadResult = " + uploadResult);
						System.out.println("jstrBillCodes = " + jstrBillCodes);
						System.out.println("Type() = " + expAdapter.getDatas().get(0).getType());
						System.out.println("OrafileNameurl = " + Commons.getCurrentDaterTimeSSS() + ".jpg");
						try {
							int resultCode = Integer.parseInt(uploadResult);
							if (resultCode == 1) {
								handler.sendEmptyMessage(UPLOAD_SUCCESS);
							} else {
								handler.sendEmptyMessage(UPLOAD_FAILED);
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
							handler.sendEmptyMessage(UPLOAD_FAILED);
						}
					}
				};
			}.start();
		}
	}

	private void getDriverData(final boolean newData) {

		try {
			OkHttpUtils.post().url(getResources().getString(R.string.GetDriveData))
					.addParams("carcodeurl", URLEncoder.encode(MyApp.currentUser.getMogilelogin(), "utf-8")).build()
					.execute(new StringCallback() {

						@Override
						public void onResponse(String arg0, int arg1) {
							String jsonData = Commons.parseXML(arg0);
							ArrayList<DriverItem> datas = Commons.parseJsonList(jsonData,
									new TypeToken<ArrayList<DriverItem>>() {
							}.getType());
							if (datas != null) {
								if (newData) {
									expAdapter.setNewDatas(datas);
								} else {
									expAdapter.setDatas(datas);
								}
								if (TextUtils.equals(datas.get(0).getType(), "XT")) {
									btn_yth.setVisibility(View.VISIBLE);
								} else if (TextUtils.equals(datas.get(0).getType(), "JP")) {
									btn_yth.setVisibility(View.GONE);
								}
								expAdapter.notifyDataSetChanged();
							} else {
								// 没有获取到数据
								expAdapter.setDatas(new ArrayList<DriverItem>());
								expAdapter.notifyDataSetChanged();
								Commons.ShowToast(context, "没有查询到数据！");
							}
						}

						@Override
						public void onError(Call arg0, Exception arg1, int arg2) {
							Commons.ShowToast(context, "请求超时！");
						}

					});
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (File f : tempfiles) {
			if (f.exists()) {
				f.delete();
			}
		}
	}
}
