package com.tgzzb.cdc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.kobjects.base64.Base64;

import com.google.gson.reflect.TypeToken;
import com.tgzzb.cdc.adapter.ImgPickerGRAdapter;
import com.tgzzb.cdc.bean.Mr_Data;
import com.tgzzb.cdc.bean.Mr_Route;
import com.tgzzb.cdc.imagepicker.Bimp;
import com.tgzzb.cdc.imagepicker.GalleryActivity;
import com.tgzzb.cdc.imagepicker.ImageItem;
import com.tgzzb.cdc.imagepicker.PublicWay;
import com.tgzzb.cdc.imagepicker.Res;
import com.tgzzb.cdc.interfaces.MySingleClickListener;
import com.tgzzb.cdc.utils.Commons;
import com.tgzzb.cdc.utils.SelectPicDialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

public class MrAddInfoActivity extends Activity
		implements OnCheckedChangeListener, OnItemClickListener, OnClickListener {
	private TextView tv_dzhm;
	private TextView tv_ljh;
	private TextView tv_sl;
	private TextView tv_pszp;
	private TextView tv_slbfzp;
	private TextView tv_qszp;
	private Spinner sp_route;
	private CheckBox ckb_ps;
	private CheckBox ckb_qs;
	private CheckBox ckb_slbf;
	private String[] routes;
	private Context context;

	private final int GETLINE_CODE = 10001;
	private final int SETPROGRESS_CODE = 1002;
	private final int SETPMSEEAGE_CODE = 1003;
	private final int GETFILECOUNT_CODE = 1004;

	private final int UPLOADPIC_FAILED = 10005;
	/*
	 * 图片相关
	 */
	private Uri imageUri;
	private String fileString = "";
	private GridView gr_ps;
	private GridView gr_slbf;
	private GridView gr_qs;
	private ImgPickerGRAdapter gr_psAdapter;
	private ImgPickerGRAdapter gr_slbfAdapter;
	private ImgPickerGRAdapter gr_qsAdapter;
	public static ArrayList<ImageItem> SelectBitmap;
	public static ArrayList<ImageItem> SelectBitmap1;
	public static ArrayList<ImageItem> SelectBitmap2;
	public static int currentGR = -1;
	private final int MY_CAMERA_REQUEST_CODE = 100;
	private final int MY_IMAGE_OPEN_REQUEST_CODE = 200;
	private final int MY_CROP_RESULT_CODE = 300;

	private List<File> tempfiles = new ArrayList<File>();
	private int fileCount = 1;
	private ProgressDialog uploadPicDialog;
	private int pgdProgress = 1;
	private String currentRoute;
	// *****//
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == GETLINE_CODE) {
				try {
					if (msg.obj == null) {
						Commons.ShowToast(context, "请求失败,请检查您的网络连接。");
						return;
					}
					if (Integer.parseInt((String) msg.obj) == 0) {
						Commons.ShowToast(context, "没有查询到节点数据,请重新尝试。");
					}
				} catch (Exception e) {
					Type typeOfT = new TypeToken<List<Mr_Route>>() {
					}.getType();
					List<Mr_Route> routeList = Commons.parseJsonList((String) msg.obj, typeOfT);
					routes = routeList.get(0).getRoute().split(",");
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
							android.R.layout.simple_spinner_item, routes);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sp_route.setAdapter(adapter);
				}
			}
			if (msg.what == SETPROGRESS_CODE) {
				uploadPicDialog.setProgress(pgdProgress++);
			}
			if (msg.what == SETPMSEEAGE_CODE) {
				uploadPicDialog.setMessage("操作完成！");
				uploadPicDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("确定");
				Commons.ShowToast(context, "操作完成！");
			}
			if (msg.what == GETFILECOUNT_CODE) {
				Commons.ShowToast(context, "网络异常,请稍后再试！");
			}
			if (msg.what == UPLOADPIC_FAILED) {
				uploadPicDialog.dismiss();
				Commons.ShowDialog(context, "图片上传", "图片上传失败。");
			}
		};
	};
	private SelectPicDialog selectPicDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mr_addinfo);
		SelectBitmap = new ArrayList<ImageItem>();
		SelectBitmap1 = new ArrayList<ImageItem>();
		SelectBitmap2 = new ArrayList<ImageItem>();
		Res.init(this);
		PublicWay.num = 5;// 设置图片最大数量为5
		initViews();
		initDatas();

		new Thread() {
			@Override
			public void run() {
				String resultCode = Commons.getResponse(context, getResources().getString(R.string.method_getline),
						getResources().getString(R.string.method_getline_params_carno),
						MyApp.currentUser.getMogilelogin(),
						getResources().getString(R.string.method_getline_params_dzhm), tv_dzhm.getText().toString());
				Message msg = new Message();
				msg.what = GETLINE_CODE;
				msg.obj = resultCode;
				handler.sendMessage(msg);
			};
		}.start();
	}

	private void initDatas() {
		context = this;
		Intent intent = getIntent();
		Mr_Data data = (Mr_Data) intent.getSerializableExtra("currentItem");
		tv_dzhm.setText(data.getDzhm());
		tv_ljh.setText(data.getLjh());
		tv_sl.setText(String.valueOf(data.getRequireqty()));
		gr_psAdapter = new ImgPickerGRAdapter(context);
		gr_slbfAdapter = new ImgPickerGRAdapter(context);
		gr_qsAdapter = new ImgPickerGRAdapter(context);
		gr_ps.setAdapter(gr_psAdapter);
		gr_slbf.setAdapter(gr_slbfAdapter);
		gr_qs.setAdapter(gr_qsAdapter);
	}

	private void initViews() {
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("Mr单号详情");
		findViewById(R.id.title_left).setOnClickListener(this);

		tv_dzhm = (TextView) findViewById(R.id.tv_dzhm);
		tv_ljh = (TextView) findViewById(R.id.tv_ljh);
		tv_sl = (TextView) findViewById(R.id.tv_sl);
		tv_pszp = (TextView) findViewById(R.id.tv_pszp);
		tv_slbfzp = (TextView) findViewById(R.id.tv_slbfzp);
		tv_qszp = (TextView) findViewById(R.id.tv_qszp);
		ckb_ps = (CheckBox) findViewById(R.id.ckb_ps);
		ckb_slbf = (CheckBox) findViewById(R.id.ckb_slbf);
		ckb_qs = (CheckBox) findViewById(R.id.ckb_qs);
		ckb_ps.setOnCheckedChangeListener(this);
		ckb_slbf.setOnCheckedChangeListener(this);
		ckb_qs.setOnCheckedChangeListener(this);
		gr_ps = (GridView) findViewById(R.id.gr_ps);
		gr_slbf = (GridView) findViewById(R.id.gr_slbf);
		gr_qs = (GridView) findViewById(R.id.gr_qs);
		findViewById(R.id.btn_submit).setOnClickListener(new MySingleClickListener() {

			@Override
			protected void onSingleClick(View view) {

				if (routes == null) {
					Commons.ShowToast(context, "未获取到节点信息,请检查您的网络连接。");
					return;
				}
				if (SelectBitmap.size() + SelectBitmap1.size() + SelectBitmap2.size() == 0) {
					Commons.ShowToast(context, "若无异常请勿提交");
					return;
				}
				uploadPicDialog = showDialogLoadingPic();
				uploadPicDialog.show();
				new Thread() {
					@Override
					public void run() {
						String dzhm = tv_dzhm.getText().toString();
						// 1、获取图片数量
						String fileSortResult = getFileCount();
						FileCount count = Commons.parseJsonClass(fileSortResult, FileCount.class);
						try {
							fileCount = count.getFileCount();
						} catch (Exception e) {
							handler.sendEmptyMessage(GETFILECOUNT_CODE);
							return;
						}
						// 2、上传破损照片-->插入异常信息表
						upLoadPic(SelectBitmap, "有破损");
						// 3、上传数量不符照片-->插入异常信息表
						upLoadPic(SelectBitmap1, "数量不符");
						// 4、上传签收照片-->插入异常信息表
						upLoadPic(SelectBitmap2, "签收");
						// 5、修改该单号异常状态为异常
						String updateResult = Commons.getResponse(context, "UpdateData", "dzhm", dzhm, "ycxx",
								"有异常,请查看图片！");
						try {
							int updateCode = Integer.parseInt(updateResult);
							if (updateCode == 1) {
								Log.d("TGZZB", "修改成功！");
								handler.sendEmptyMessage(SETPMSEEAGE_CODE);
							} else {
								Log.d("TGZZB", "修改失败！");
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
							Log.d("TGZZB", "修改失败！");
						}
					};

				}.start();
			}
		});
		gr_ps.setOnItemClickListener(this);
		gr_slbf.setOnItemClickListener(this);
		gr_qs.setOnItemClickListener(this);
		sp_route = (Spinner) findViewById(R.id.sp_route);
		sp_route.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

				currentRoute = routes[position];
				if (position == routes.length - 1) {
					ckb_qs.setVisibility(View.VISIBLE);
				} else {
					ckb_qs.setChecked(false);
					ckb_qs.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				if (routes == null) {
					Commons.ShowToast(context, "未获取到节点信息,请检查您的网络连接。");
					return;
				}
				if (SelectBitmap.size() + SelectBitmap1.size() + SelectBitmap2.size() == 0) {
					Commons.ShowToast(context, "若无异常请勿提交");
					return;
				}
				uploadPicDialog = showDialogLoadingPic();
				uploadPicDialog.show();
				new Thread() {
					@Override
					public void run() {
						String dzhm = tv_dzhm.getText().toString();
						// 1、获取图片数量
						String fileSortResult = getFileCount();
						FileCount count = Commons.parseJsonClass(fileSortResult, FileCount.class);
						try {
							fileCount = count.getFileCount();
						} catch (Exception e) {
							handler.sendEmptyMessage(GETFILECOUNT_CODE);
							return;
						}
						// 2、上传破损照片-->插入异常信息表
						if (SelectBitmap != null) {
							upLoadPic(SelectBitmap, "有破损");
							// 修改该单号异常状态为异常
							String updateResult = Commons.getResponse(context, "UpdateData", "dzhm", dzhm, "ycxx",
									"有异常");
							int updateCode = Integer.parseInt(updateResult);
							if (updateCode == 1) {
								Log.d("TGZZB", "mr addinfo ps 修改成功！");
								handler.sendEmptyMessage(SETPMSEEAGE_CODE);
							} else {
								Log.d("TGZZB", "mr addinfo ps 修改失败！");
							}
						}
						// 3、上传数量不符照片-->插入异常信息表
						if (SelectBitmap1 != null) {
							upLoadPic(SelectBitmap1, "数量不符");
							// 修改该单号异常状态为异常
							String updateResult = Commons.getResponse(context, "UpdateData", "dzhm", dzhm, "ycxx",
									"有异常");
							int updateCode = Integer.parseInt(updateResult);
							if (updateCode == 1) {
								Log.d("TGZZB", "mr addinfo slbf 修改成功！");

								handler.sendEmptyMessage(SETPMSEEAGE_CODE);
							} else {
								Log.d("TGZZB", "mr addinfo slbf 修改失败！");
							}
						}
						// 4、上传签收照片-->插入异常信息表
						upLoadPic(SelectBitmap2, "签收");
					};

				}.start();

			}

		});
	}

	public void showDialogUploadPic() {

		selectPicDialog = new SelectPicDialog(context, MY_CAMERA_REQUEST_CODE);
		selectPicDialog.ShowSelectPicDialog();

	}

	public ProgressDialog showDialogLoadingPic() {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(SelectBitmap.size() + SelectBitmap1.size() + SelectBitmap2.size());
		// 设置ProgressDialog 标题
		progressDialog.setTitle("Mr");
		// 设置ProgressDialog 提示信息
		progressDialog.setMessage("正在上传图片...");
		// 设置ProgressDialog 的进度条是否不明确
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(false);
		progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "后台上传", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				currentGR = -1;
				finish();
			}
		});
		return progressDialog;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Bimp.tempSelectBitmap.clear();
			SelectBitmap.clear();
			SelectBitmap1.clear();
			SelectBitmap2.clear();
			currentGR = -1;
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.ckb_ps:
			SelectBitmap.clear();
			gr_psAdapter.notifyDataSetChanged();
			if (isChecked) {
				tv_pszp.setVisibility(View.VISIBLE);
				gr_ps.setVisibility(View.VISIBLE);
			} else {
				tv_pszp.setVisibility(View.GONE);
				gr_ps.setVisibility(View.GONE);
			}
			break;
		case R.id.ckb_slbf:
			SelectBitmap1.clear();
			gr_slbfAdapter.notifyDataSetChanged();

			if (isChecked) {
				tv_slbfzp.setVisibility(View.VISIBLE);
				gr_slbf.setVisibility(View.VISIBLE);
			} else {
				tv_slbfzp.setVisibility(View.GONE);
				gr_slbf.setVisibility(View.GONE);
			}
			break;
		case R.id.ckb_qs:
			SelectBitmap2.clear();
			gr_qsAdapter.notifyDataSetChanged();
			if (isChecked) {
				tv_qszp.setVisibility(View.VISIBLE);
				gr_qs.setVisibility(View.VISIBLE);
			} else {
				tv_qszp.setVisibility(View.GONE);
				gr_qs.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.gr_ps:
			currentGR = 0;
			Bimp.tempSelectBitmap = SelectBitmap;
			if (position == SelectBitmap.size()) {
				showDialogUploadPic();
			} else {
				Intent intent = new Intent(context, GalleryActivity.class);
				intent.putExtra("fromAddress", "MrAddInfoActivity1");

				intent.putExtra("position", "1");
				intent.putExtra("ID", position);
				startActivity(intent);
			}
			break;
		case R.id.gr_slbf:
			currentGR = 1;
			Bimp.tempSelectBitmap = SelectBitmap1;
			if (position == SelectBitmap1.size()) {
				showDialogUploadPic();
			} else {
				Intent intent = new Intent(context, GalleryActivity.class);
				intent.putExtra("fromAddress", "MrAddInfoActivity2");

				intent.putExtra("position", "1");
				intent.putExtra("ID", position);
				startActivity(intent);
			}
			break;
		case R.id.gr_qs:
			currentGR = 2;
			Bimp.tempSelectBitmap = SelectBitmap2;
			if (position == SelectBitmap2.size()) {
				showDialogUploadPic();
			} else {
				Intent intent = new Intent(context, GalleryActivity.class);
				intent.putExtra("fromAddress", "MrAddInfoActivity3");
				intent.putExtra("position", "1");
				intent.putExtra("ID", position);
				startActivity(intent);
			}

			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0) {
			return;
		}
		switch (requestCode) {
		case MY_CAMERA_REQUEST_CODE:
			imageUri = selectPicDialog.getImageUri();
			if (Bimp.tempSelectBitmap.size() < PublicWay.num && resultCode == RESULT_OK) {
				String[] proj = { MediaStore.Images.Media.DATA };

				// MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				Cursor actualImageCursor = this.getContentResolver().query(imageUri, proj, null, null, null);
				int actual_image_column_index = actualImageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				actualImageCursor.moveToFirst();
				String image_path = actualImageCursor.getString(actual_image_column_index);
				ImageItem takePhoto = new ImageItem();
				takePhoto.setImagePath(image_path);
				takePhoto.setBitmap(BitmapFactory.decodeFile(image_path));
				Bimp.tempSelectBitmap.add(takePhoto);
				actualImageCursor.close();
			}
			break;
		case MY_CROP_RESULT_CODE:

			InputStream iStream = null;
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(fileString, options);
				if (options.outWidth > options.outHeight) {
					double widthRatio = 1.0 * options.outWidth / 800;
					double heightRatio = 1.0 * options.outHeight / 480;
					options.inSampleSize = (int) Math.round(Math.max(widthRatio, heightRatio));
				} else {
					double widthRatio = 1.0 * options.outWidth / 480;
					double heightRatio = 1.0 * options.outHeight / 800;
					options.inSampleSize = (int) Math.round(Math.max(widthRatio, heightRatio));
				}
				options.inJustDecodeBounds = false;
				iStream = getContentResolver().openInputStream(imageUri);
				Bitmap temp = BitmapFactory.decodeStream(iStream);
				temp.getByteCount();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (iStream != null) {
					try {
						iStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			break;
		case MY_IMAGE_OPEN_REQUEST_CODE:

			break;
		default:
			break;
		}

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (currentGR == 0) {
			SelectBitmap = Bimp.tempSelectBitmap;
			gr_psAdapter.setCurrentBitmapList(SelectBitmap);
			gr_psAdapter.notifyDataSetChanged();
		}
		if (currentGR == 1) {
			SelectBitmap1 = Bimp.tempSelectBitmap;
			gr_slbfAdapter.setCurrentBitmapList(SelectBitmap1);
			gr_slbfAdapter.notifyDataSetChanged();
		}
		if (currentGR == 2) {
			SelectBitmap2 = Bimp.tempSelectBitmap;
			gr_qsAdapter.setCurrentBitmapList(SelectBitmap2);
			gr_qsAdapter.notifyDataSetChanged();
		}
	}

	// 将原Bitmap进行压缩、保存,并转化成64位字符串返回;
	private String getImageBase64(Bitmap bitmap) {

		Bitmap bp = Commons.compressImage(context, bitmap, 100, 640);
		Commons.saveBitmapFile(bp, initFile().getPath());
		// Commons.saveBitmapFile(bp, initFile().getParent());
		// 将Bitmap转换成字符串
		String string = null;
		ByteArrayOutputStream bStream = null;
		try {
			bStream = new ByteArrayOutputStream();
			bp.compress(CompressFormat.JPEG, 100, bStream);// 100表示不压缩
			byte[] bytes = bStream.toByteArray();
			string = Base64.encode(bytes);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (bStream != null) {
				try {
					bStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return string;
	}

	private File initFile() {
		String fileName = UUID.randomUUID().toString();
		File tempFile = new File(Environment.getExternalStorageDirectory(), fileName + ".jpg");
		if (tempFile.exists()) {
			tempFile.delete();
		} else {
			try {
				tempFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		tempfiles.add(tempFile);
		return tempFile;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.title_left:
			currentGR = -1;
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// 删除手机根目录生成的缩略图
		for (File f : tempfiles) {
			if (f.exists()) {
				f.delete();
			}
		}
		super.onDestroy();
	}

	public String getFileCount() {
		return Commons.getResponse(context, getResources().getString(R.string.method_getfilecount),
				getResources().getString(R.string.method_getfilecount_dzhm), tv_dzhm.getText().toString());
	}

	public String upLoadPic(ArrayList<ImageItem> list, String msg) {
		for (int i = 0; i < list.size(); i++) {
			// 上传图片
			String uploadResult = Commons.getResponse(context, getResources().getString(R.string.method_uploadfile),
					getResources().getString(R.string.method_uploadfile_filebytes),
					getImageBase64(list.get(i).getBitmap()),
					getResources().getString(R.string.method_uploadfile_filename),
					tv_dzhm.getText().toString() + "_" + fileCount + ".jpg",
					getResources().getString(R.string.method_uploadfile_dzhm), tv_dzhm.getText().toString());
			try {
				if (Integer.parseInt(uploadResult) == 1) {
					// 插入异常信息
					String insertResult = Commons.getResponse(context,
							getResources().getString(R.string.method_insert_ycxx),
							getResources().getString(R.string.method_insert_ycxx_dzhm), tv_dzhm.getText().toString(),
							getResources().getString(R.string.method_insert_ycxx_ddh), "",
							getResources().getString(R.string.method_insert_ycxx_qymc), currentRoute,
							getResources().getString(R.string.method_insert_ycxx_msg), msg,
							getResources().getString(R.string.method_insert_ycxx_flag), "M",
							getResources().getString(R.string.method_insert_ycxx_carno),
							MyApp.currentUser.getMogilelogin(),
							getResources().getString(R.string.method_insert_ycxx_filename),
							tv_dzhm.getText().toString() + "_" + fileCount + ".jpg", "ljh",
							tv_ljh.getText().toString());
					if (Integer.parseInt(insertResult) == 1) {
						// 图片名称数量+1,进度条更新进度
						fileCount = fileCount + 1;
						handler.sendEmptyMessage(SETPROGRESS_CODE);
					} else {
						Log.d("TGZZB", "异常信息插入失败！");
					}
				} else {
					Log.d("TGZZB", "图片上传失败！");
					handler.sendEmptyMessage(UPLOADPIC_FAILED);
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	// 用来解析图片数量
	class FileCount {
		public int num;

		public int getFileCount() {
			return num;
		}

		public void setFileCount(int fileCount) {
			this.num = fileCount;
		}
	}

	// @Override
	// public void onRequestPermissionsResult(int requestCode, String[]
	// permissions, int[] grantResults) {
	// super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	// selectPicDialog.onRequestPermissionsResult(requestCode, permissions,
	// grantResults);
	// }

}
