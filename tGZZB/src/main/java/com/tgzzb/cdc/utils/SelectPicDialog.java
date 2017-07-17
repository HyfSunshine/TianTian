package com.tgzzb.cdc.utils;

import com.tgzzb.cdc.R;
import com.tgzzb.cdc.imagepicker.AlbumActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;

public class SelectPicDialog implements OnClickListener {

	private Context context;
	private Uri imageUri;
	private int MY_CAMERA_REQUEST_CODE = 1001;
	private PopupWindow popupWindow;

	public Uri getImageUri() {
		return imageUri;
	}

	public void setImageUri(Uri imageUri) {
		this.imageUri = imageUri;
	}

	public SelectPicDialog(Context context, int mY_CAMERA_REQUEST_CODE) {
		super();
		this.context = context;
		MY_CAMERA_REQUEST_CODE = mY_CAMERA_REQUEST_CODE;
	}

	// 显示拍照+相册的Dialog
	public void ShowSelectPicDialog() {
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle("上传照片")
				.setItems(new String[] { "拍照", "打开相册" }, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							// if (Build.VERSION.SDK_INT >=
							// Build.VERSION_CODES.M) {
							// // 判断是否有调用相机的权限
							// if (context.checkSelfPermission(
							// android.Manifest.permission.CAMERA) ==
							// PackageManager.PERMISSION_GRANTED) {
							// openCamera();
							// } else {
							// ((Activity) context).requestPermissions(new
							// String[] {
							// Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},1);
							// }
							// } else {
							// openCamera();
							// }
							openCamera();
						}
						if (which == 1) {
							// if (Build.VERSION.SDK_INT >=
							// Build.VERSION_CODES.M) {
							// if (context.checkSelfPermission(
							// android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
							// == PackageManager.PERMISSION_GRANTED) {
							// openPhotoAlbum();
							// } else {
							// ((Activity) context).requestPermissions(
							// new String[] {
							// Manifest.permission.WRITE_EXTERNAL_STORAGE }, 2);
							// }
							// } else {
							// openPhotoAlbum();
							// }
							openPhotoAlbum();
						}
					}
				}).create();
		dialog.show();
	}

	public void ShowSelectPicPopWindow(View parentView) {

		popupWindow = new PopupWindow(context);
		popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		View popView = LayoutInflater.from(context).inflate(R.layout.item_popupwindows_picture, null);
		popView.findViewById(R.id.item_popupwindows_camera).setOnClickListener(this);
		popView.findViewById(R.id.item_popupwindows_Photo).setOnClickListener(this);
		popView.findViewById(R.id.item_popupwindows_cancel).setOnClickListener(this);

		popupWindow.setContentView(popView);
		popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
		popupWindow.setOutsideTouchable(false);
		popupWindow.setFocusable(true);
		popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);

	}

	public void openCamera() {
		String SDState = Environment.getExternalStorageState();
		if (TextUtils.equals(SDState, Environment.MEDIA_MOUNTED)) {
			ContentValues values = new ContentValues(1);
			values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
			Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			System.out.println("dd imageuri = " + imageUri);
			openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
			if (imageUri == null) {
				return;
			}
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			// openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			((Activity) context).startActivityForResult(openCameraIntent, MY_CAMERA_REQUEST_CODE);
		} else {
			
		}
	}

	public void openPhotoAlbum() {

		Intent intent = new Intent(context, AlbumActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		context.startActivity(intent);

	}

	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		System.out.println("grantResults.length = " + grantResults.length);
		for (int i = 0; i < grantResults.length; i++) {
			System.out.println("grantResults = " + grantResults[i]);
		}
		if (requestCode == 1) {
			boolean albumAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
			if (albumAccepted) {
				openCamera();
			} else {
				Commons.ShowToast(context, "请先开启应用拍照权限。");
			}
		} else if (requestCode == 2) {
			boolean albumAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
			if (albumAccepted) {
				openPhotoAlbum();
			} else {
				Commons.ShowToast(context, "请先开启应用打开相册权限。");
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_popupwindows_camera:
			openCamera();
			break;
		case R.id.item_popupwindows_Photo:
			openPhotoAlbum();
			break;
		case R.id.item_popupwindows_cancel:
			popupWindow.dismiss();
			break;

		default:
			break;
		}
	}
}
