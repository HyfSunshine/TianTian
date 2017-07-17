package com.tgzzb.cdc;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.tgzzb.cdc.bean.AppVersion;
import com.tgzzb.cdc.utils.Commons;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import okhttp3.Call;

public class ShowPageActivity extends Activity {
	private Context context;
	private String downLoadUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showpage);
		context = this;

		if (Commons.isNetworkAvailable(this)) {
			// 检测版本号,若有更新提示更新
			final double currentVersionName = Double.parseDouble(getVersionName());

			OkHttpUtils.post().url(getResources().getString(R.string.GetVersion)).build().execute(new StringCallback() {

				@Override
				public void onResponse(String arg0, int arg1) {
					String jsonData = Commons.parseXML(arg0);
					AppVersion version = Commons.parseJsonClass(jsonData, AppVersion.class);
					if (version != null) {
						double newVersionName = Double.parseDouble(version.getVersion());
						downLoadUrl = version.getUrl();
						// <
						if (currentVersionName < newVersionName) {
							// 需要更新
							Dialog dialog = new AlertDialog.Builder(context).setTitle("版本更新")
									.setMessage("检测到新版本，是否立即更新？")
									.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									downLoadApk();
								}
							}).setNegativeButton("下次更新", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									 goLoginPage();
								}
							}).create();
							dialog.show();
						}else{
							 goLoginPage();
						}
					}
				}

				@Override
				public void onError(Call arg0, Exception arg1, int arg2) {
					Commons.ShowToast(context, "连接超时。");
					goLoginPage();
				}
			});
		} else {
			Commons.ShowToast(this, "网络异常！");
			goLoginPage();
		}
	}

	private void goLoginPage() {
		TimerTask task = new TimerTask() {
			public void run() {
				startActivity(new Intent(context, LoginActivity.class));
				finish();
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 2000);
	}

	/**
	 * 获取当前程序的版本号
	 */
	private String getVersionName() {
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 下载APK
	 */
	protected void downLoadApk() {

		// 进度条对话框
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("正在下载更新...");
		progressDialog.show();
		final File apkFile = new File(Environment.getExternalStorageDirectory(), "掌中宝.apk");
		HttpUtils httpd = new HttpUtils();
		httpd.download(downLoadUrl + "掌中宝.apk", apkFile.getPath(), new RequestCallBack<File>() {
			// 更新进度条
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
				progressDialog.setProgress((int) ((double) current / (double) total * 100));
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Commons.ShowToast(context, "更新失败，请检查您的网络.");
				progressDialog.dismiss();
				goLoginPage();
			}

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				installApk(apkFile);
				progressDialog.dismiss();
				goLoginPage();
			}
		});
	}

	/**
	 * 安装apk
	 */
	protected void installApk(File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
