package com.tgzzb.cdc;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tgzzb.cdc.bean.Mr_User;
import com.tgzzb.cdc.interfaces.MySingleClickListener;
import com.tgzzb.cdc.utils.Commons;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import okhttp3.Call;

public class LoginActivity extends Activity {
	private CheckBox ckb_save;// 复选框-记住密码;
	private EditText actv_uName;// 用户名输入框;
	private EditText et_mima;// 密码输入框;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		context = this;
		initViews();
		initDatas();
		
//		HttpUtils http = new HttpUtils();
//		RequestParams params = new RequestParams();
//		params.addHeader("key","63b57b9d6f1277500256d49240ea2736");
//		//63b57b9d6f1277500256d49240ea2736
//		
//		http.send(HttpMethod.GET, "http://api.3023.com/apple/apple?sn=C6KR9BNYGRY8", params,new RequestCallBack<String>() {
//
//			@Override
//			public void onFailure(HttpException arg0, String arg1) {
//				
//			}
//
//			@Override
//			public void onSuccess(ResponseInfo<String> arg0) {
//				System.out.println("result = "+arg0.result);
//			}
//		});
		
		
	}

	private void initDatas() {
		Mr_User user = Mr_User.loginFromSharedPreferences();
		if (user != null) {
			actv_uName.setText(user.getMogilelogin());
			et_mima.setText(user.getPassword());
		}
	}

	private void initViews() {
		actv_uName = (EditText) findViewById(R.id.actv_uName);
		et_mima = (EditText) findViewById(R.id.et_miMa);
		ckb_save = (CheckBox) findViewById(R.id.ckb_remember);
		ckb_save.setChecked(true); // 设置复选框默认为选中状态;

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getResources().getString(R.string.app_name));
		findViewById(R.id.title_left).setVisibility(View.INVISIBLE);

		findViewById(R.id.btn_login).setOnClickListener(new MySingleClickListener() {

			@Override
			protected void onSingleClick(View view) {
				try {
					connect();
				} catch (NotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void connect() throws NotFoundException, UnsupportedEncodingException {

		final String name = actv_uName.getText().toString();
		final String password = et_mima.getText().toString();

//		HttpUtils http = new HttpUtils();
//		RequestParams params = new RequestParams();
//		params.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//		params.addBodyParameter("usernameurl", name);
//		params.addBodyParameter("passwordurl", password);
//		http.send(HttpMethod.POST, "http://58.210.237.170:8083/ifsweb/Service/IfswebAPPWebservice2.asmx/GetLoginData",
//				params, new RequestCallBack<String>() {
//
//					@Override
//					public void onFailure(HttpException arg0, String arg1) {
//						// TODO Auto-generated method stub
//						System.out.println("Error result = " + arg0.getMessage());
//					}
//
//					@Override
//					public void onSuccess(ResponseInfo<String> arg0) {
//						System.out.println("result = " + arg0.result);
//
//					}
//				});

//		OkHttpUtils.post().url("http://58.210.237.170:8083/ifsweb/Service/IfswebAPPWebservice.asmx/GetLoginData")
//				.addParams("usernameurl", name).addParams("passwordurl", password)
//				 .headers(headers)
//				.build()
//				.execute(new StringCallback() {
//
//					@Override
//					public void onResponse(String arg0, int arg1) {
//						String jsonData = Commons.parseXML(arg0);
//						System.out.println("JsonData=" + jsonData);
//					}
//
//					@Override
//					public void onError(Call arg0, Exception arg1, int arg2) {
//						Commons.ShowToast(context, "登录超时，请您重新登录！");
//					}
//				});

		
		//http://58.210.237.170:8083/Ifsweb/Service/IfsWebAPPWebservice2.asmx/GetLoginData
		//getResources().getString(R.string.GetLoginData)
		OkHttpUtils.post().url(getResources().getString(R.string.GetLoginData))
				.addParams("usernameurl", URLEncoder.encode(name, "UTF-8")).addParams("passwordurl", password).build()
				.execute(new StringCallback() {

					@Override
					public void onResponse(String arg0, int arg1) {
						String jsonData = Commons.parseXML(arg0);
						ArrayList<UserPermission> permissions = Commons.parseJsonList(jsonData,
								new TypeToken<ArrayList<UserPermission>>() {
						}.getType());
						if (permissions != null) {
							// 登录成功且至少有一个权限
							MyApp.setPermissions(permissions);
							MyApp.currentUser = new Mr_User(name, password);
							if (ckb_save.isChecked()) {
								MyApp.currentUser.loginToSharedPreferences(MyApp.currentUser);
							} else {
								MyApp.currentUser.clearSharedPreference("login");
							}
							MyApp.currentUser.writeToSharedPreferences(MyApp.currentUser);
							context.startActivity(new Intent(context, MainActivity.class));
							finish();
						} else {
							// 登录失败给出提示
							Commons.ShowToast(context, "登录失败。");
						}
					}

					@Override
					public void onError(Call arg0, Exception arg1, int arg2) {
						Commons.ShowToast(context, "登录超时，请您重新登录！");
					}
				});
	}

	class UserPermission implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String fun;

		public UserPermission(String fun) {
			super();
			this.fun = fun;
		}

		public String getFun() {
			return fun;
		}

		public void setFun(String fun) {
			this.fun = fun;
		}
	}

}
