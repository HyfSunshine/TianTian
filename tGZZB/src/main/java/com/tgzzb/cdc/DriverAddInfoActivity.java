package com.tgzzb.cdc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.kobjects.base64.Base64;

import com.google.gson.Gson;
import com.tgzzb.cdc.adapter.ImgPickerGRAdapter;
import com.tgzzb.cdc.bean.DriverYwbhDdcode;
import com.tgzzb.cdc.bean.DriverItem;
import com.tgzzb.cdc.imagepicker.Bimp;
import com.tgzzb.cdc.imagepicker.GalleryActivity;
import com.tgzzb.cdc.imagepicker.ImageItem;
import com.tgzzb.cdc.imagepicker.PublicWay;
import com.tgzzb.cdc.interfaces.MySingleClickListener;
import com.tgzzb.cdc.utils.Commons;
import com.tgzzb.cdc.utils.SelectPicDialog;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import net.bither.util.NativeUtil;

public class DriverAddInfoActivity extends Activity implements OnClickListener, OnItemClickListener {

	private DriverItem data;
	private CheckBox ckb_qsd;
	private CheckBox ckb_ps;
	private GridView gr_qsd;
	private GridView gr_ps;

	private ProgressDialog uploadPicDialog;
	private ImgPickerGRAdapter gr_qsdAdapter;
	private ImgPickerGRAdapter gr_psAdapter;
	public static ArrayList<ImageItem> qsdBitmaps;
	public static ArrayList<ImageItem> psBitmaps;
	private Context context;
	public Uri imageUri;
	public int currentGR = -1;
	private List<File> tempfiles = new ArrayList<File>();
	private TextView tv_ywbh;
	private EditText et_notes;
	private int numProgress = 1;
	private final int UPLOAD_SUCCESS = 1001;
	private final int UPLOAD_FAILED = 1002;
	private final int MY_CAMERA_REQUEST_CODE = 100;
	private final int MY_CROP_RESULT_CODE = 300;
	private String fileString = "";

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == UPLOAD_SUCCESS) {
				uploadPicDialog.setProgress(numProgress++);
				if (uploadPicDialog.getMax() == numProgress - 1) {
					// 所有图片上传完成
					uploadPicDialog.setMessage("操作完成！");
					uploadPicDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("确定");
					Commons.ShowToast(context, "操作完成！");
					clearPic();
				}
			}
			if(msg.what == UPLOAD_FAILED){
				uploadPicDialog.dismiss();
				Commons.ShowDialog(context, "上传失败", "图片上传失败，请稍后尝试！");
			}
		}
	};
	private SelectPicDialog selectPicDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_addinfo);
		context = this;
		qsdBitmaps = new ArrayList<ImageItem>();
		psBitmaps = new ArrayList<ImageItem>();
//		Res.init(this);
//		PublicWay.num = 5;// 设置图片最大数量为5
		initViews();
		Intent intent = getIntent();
		data = (DriverItem) intent.getSerializableExtra("DriverItem");
		if (data != null) {
			setDatas();
		}
	}

	private void initViews() {
		Commons.setTitle(this, "单号详情", this);
		ckb_qsd = (CheckBox) findViewById(R.id.ckb_qsd);
		ckb_ps = (CheckBox) findViewById(R.id.ckb_ps);
		ckb_qsd.setOnClickListener(this);
		ckb_ps.setOnClickListener(this);
		et_notes = (EditText) findViewById(R.id.et_notes);
		gr_qsd = (GridView) findViewById(R.id.gr_qsd);
		gr_ps = (GridView) findViewById(R.id.gr_ps);
		gr_qsdAdapter = new ImgPickerGRAdapter(this);
		gr_psAdapter = new ImgPickerGRAdapter(this);
		gr_qsd.setAdapter(gr_qsdAdapter);
		gr_ps.setAdapter(gr_psAdapter);
		gr_qsd.setOnItemClickListener(this);
		gr_ps.setOnItemClickListener(this);
		findViewById(R.id.btn_submit).setOnClickListener(new MySingleClickListener() {

			@Override
			protected void onSingleClick(View view) {
				if (qsdBitmaps.size() + psBitmaps.size() == 0) {
					Commons.ShowToast(context, "若无异常请勿提交");
					return;
				}
			 
				uploadPicDialog = Commons.getDialogLoadingPic(context, "上窜签收单", qsdBitmaps.size() + psBitmaps.size(),true);
				uploadPicDialog.show();

				new Thread() {
					@Override
					public void run() {
						super.run();
						// 2、上传签收单照片
						upLoadPic(qsdBitmaps, "签收单");
						// 3、上传破损照片
						upLoadPic(psBitmaps, "有破损");
					};
				}.start();

			}
		});
	}

	private void setDatas() {
		TextView tv_qyjc = (TextView) findViewById(R.id.tv_qyjc);
		tv_ywbh = (TextView) findViewById(R.id.tv_ywbh);
		TextView tv_shdz = (TextView) findViewById(R.id.tv_shdz);
		TextView tv_js = (TextView) findViewById(R.id.tv_js);
		TextView tv_mz = (TextView) findViewById(R.id.tv_mz);
		TextView tv_qybh = (TextView) findViewById(R.id.tv_qybh);
		TextView tv_zdh = (TextView) findViewById(R.id.tv_zdh);
		TextView tv_fdh = (TextView) findViewById(R.id.tv_fdh);
		TextView tv_fph = (TextView) findViewById(R.id.tv_fph);
		TextView tv_dqzt = (TextView) findViewById(R.id.tv_dqzt);
		TextView tv_isfolding = (TextView) findViewById(R.id.tv_isfolding);
		TextView tv_remark = (TextView) findViewById(R.id.tv_remark);
	
		

		tv_dqzt.setText(data.getStatus());
		tv_qyjc.setText(data.getQyjc());
		tv_ywbh.setText(data.getYwbh());
		tv_shdz.setText(data.getShdz());
		tv_js.setText(data.getJs());
		tv_mz.setText(data.getMz());
		tv_qybh.setText(data.getQybh());
		tv_zdh.setText(data.getZdh());
		tv_fdh.setText(data.getFdh());
		tv_fph.setText(data.getFph());
		tv_isfolding.setText(data.getIsfolding());
		tv_remark.setText(data.getRemark());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			clearPic();
			finish();
			break;
		case R.id.ckb_qsd:
			qsdBitmaps.clear();
			gr_qsdAdapter.notifyDataSetChanged();
			if (ckb_qsd.isChecked()) {
				findViewById(R.id.tv_qsd).setVisibility(View.VISIBLE);
				gr_qsd.setVisibility(View.VISIBLE);
			} else {
				findViewById(R.id.tv_qsd).setVisibility(View.GONE);
				gr_qsd.setVisibility(View.GONE);
			}
			break;
		case R.id.ckb_ps:
			psBitmaps.clear();
			gr_psAdapter.notifyDataSetChanged();
			if (ckb_ps.isChecked()) {
				findViewById(R.id.tv_ps).setVisibility(View.VISIBLE);
				gr_ps.setVisibility(View.VISIBLE);
			} else {
				findViewById(R.id.tv_ps).setVisibility(View.GONE);
				gr_ps.setVisibility(View.GONE);
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
		default:
			break;
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (currentGR == 1) {
			qsdBitmaps = Bimp.tempSelectBitmap;
			// 每次选择完图片返回时，对图片进行压缩，再显示
			// for(int i = 0;i<qsdBitmaps.size();i++){
			// File file = initFile();
			// NativeUtil.compressBitmap(qsdBitmaps.get(i).getBitmap(),file.getPath());
			// qsdBitmaps.get(i).setBitmap(BitmapFactory.decodeFile(file.getPath()));
			// }
			gr_qsdAdapter.setCurrentBitmapList(qsdBitmaps);
			gr_qsdAdapter.notifyDataSetChanged();
		}

		if (currentGR == 2) {
			psBitmaps = Bimp.tempSelectBitmap;
			gr_psAdapter.setCurrentBitmapList(psBitmaps);
			gr_psAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.gr_qsd:
			currentGR = 1;
			Bimp.tempSelectBitmap = qsdBitmaps;
			if (position == qsdBitmaps.size()) {
				// Commons.showDialogUploadPic(context, MY_CAMERA_REQUEST_CODE);
				showDialogUploadPic();

			} else {
				Intent intent = new Intent(context, GalleryActivity.class);
				// intent.putParcelableArrayListExtra("currentBitmapList",
				// qsdBitmaps);
				intent.putExtra("fromAddress", "DriverAddInfoActivity1");
				intent.putExtra("position", "1");
				intent.putExtra("ID", position);
				startActivity(intent);
			}
			break;
		case R.id.gr_ps:
			currentGR = 2;
			Bimp.tempSelectBitmap = psBitmaps;
			if (position == psBitmaps.size()) {
				// Commons.showDialogUploadPic(context, 1001);
				showDialogUploadPic();
			} else {
				Intent intent = new Intent(context, GalleryActivity.class);
				// intent.putParcelableArrayListExtra("currentBitmapList",
				// psBitmaps);
				intent.putExtra("fromAddress", "DriverAddInfoActivity2");
				intent.putExtra("position", "1");
				intent.putExtra("ID", position);
				startActivity(intent);
			}
			break;
		default:
			break;
		}
	}

//	public ProgressDialog showDialogLoadingPic() {
//		ProgressDialog progressDialog = new ProgressDialog(context);
//		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		progressDialog.setMax(qsdBitmaps.size() + psBitmaps.size());
//		// 设置ProgressDialog 标题
//		progressDialog.setTitle("司机操作");
//		// 设置ProgressDialog 提示信息
//		progressDialog.setMessage("正在上传图片...");
//		// 设置ProgressDialog 的进度条是否不明确
//		progressDialog.setIndeterminate(false);
//		progressDialog.setCancelable(false);
//		progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "后台上传", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				currentGR = -1;
//				finish();
//			}
//		});
//		return progressDialog;
//	}

	public String upLoadPic(ArrayList<ImageItem> list, String msg) {
		for (int i = 0; i < list.size(); i++) {
			Gson g = new Gson();
			// params name -->billcodeurl--->billcodes
			ArrayList<DriverYwbhDdcode> billCodes = new ArrayList<DriverYwbhDdcode>();
			billCodes.add(new DriverYwbhDdcode(data.getYwbh(),data.getDdcode()));
			String billDatas = g.toJson(billCodes);
			//UUID.randomUUID().toString()    Commons.getCurrentDaterTimeSSS()
			String uploadResult = Commons.getResponse170(context, "UploadFile", "fileBytes",
					getImageBase64(list.get(i).getBitmap()), "OrafileNameurl",Commons.getCurrentDaterTimeSSS() + ".jpg",
					"typeurl", data.getType(), "billcodesJson", billDatas, "remarkurl",
					et_notes.getText().toString(), "filetypeurl", msg);
			try {
				if (Integer.parseInt(uploadResult) == 1) {
					Log.d("TGZZB", "图片上传成功！uploadResult = " + uploadResult);
					handler.sendEmptyMessage(UPLOAD_SUCCESS);
				} else {
					Log.d("TGZZB","图片上传失败！uploadResult = " + uploadResult);
					handler.sendEmptyMessage(UPLOAD_FAILED);
				}
			} catch (Exception e) {
				handler.sendEmptyMessage(UPLOAD_FAILED);
			}
		}

		return null;
	}

	// 将原Bitmap进行压缩、保存,并转化成64位字符串返回;
	private String getImageBase64(Bitmap bitmap) {

		// Bitmap bp = Commons.compressImage(context, bitmap, 100, 640);
		// Commons.saveBitmapFile(bp, initFile().getPath());
		// // Commons.saveBitmapFile(bp, initFile().getParent());
		// 将Bitmap转换成字符串
		String string = null;
		ByteArrayOutputStream bStream = null;

		File file = initFile();
		NativeUtil.compressBitmap(bitmap, file.getPath());
		Bitmap bp = BitmapFactory.decodeFile(file.getPath());

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
		Log.d("IMG", string);
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
	protected void onDestroy() {
		// 删除手机根目录生成的缩略图
		for (File f : tempfiles) {
			if (f.exists()) {
				f.delete();
			}
		}
		super.onDestroy();
	}

	public void showDialogUploadPic() {
		selectPicDialog = new SelectPicDialog(context, MY_CAMERA_REQUEST_CODE);
		selectPicDialog.ShowSelectPicDialog();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Bimp.tempSelectBitmap = new ArrayList<ImageItem>();
			qsdBitmaps.clear();
			psBitmaps.clear();
			currentGR = -1;
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}
	

	public void clearPic() {
		Bimp.tempSelectBitmap = new ArrayList<ImageItem>();
		qsdBitmaps.clear();
		psBitmaps.clear();

	}


	// @Override
	// public void onRequestPermissionsResult(int requestCode, String[]
	// permissions, int[] grantResults) {
	// super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	// selectPicDialog.onRequestPermissionsResult(requestCode, permissions,
	// grantResults);
	// }
	
	//[{"ddcode":"HDD17030003","ywbh":"CKI16030002"}]
	//  [{"billcodes":"CKI16030002"},{"billcodes":"CKI16030002"},]
	//picname=  20170516164445381.jpg

}
