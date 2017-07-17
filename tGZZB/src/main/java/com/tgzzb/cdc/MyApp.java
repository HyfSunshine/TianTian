package com.tgzzb.cdc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tgzzb.cdc.LoginActivity.UserPermission;
import com.tgzzb.cdc.bean.Mr_User;
import com.tgzzb.cdc.imagepicker.PublicWay;
import com.tgzzb.cdc.imagepicker.Res;
import com.tgzzb.cdc.utils.Commons;
import com.zhy.http.okhttp.OkHttpUtils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import okhttp3.OkHttpClient;

public class MyApp extends Application implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static MyApp myApp;
	public static Mr_User currentUser;
	public static ArrayList<UserPermission> permissions;

	public static MyApp getInstance() {
		if (null == myApp) {
			myApp = new MyApp();
		}
		return myApp;
	}

	public static Mr_User getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(Mr_User currentUser) {
		MyApp.currentUser = currentUser;
	}

	public static ArrayList<UserPermission> getPermissions() {
		if (permissions == null) {
			SharedPreferences sp = getInstance().getSharedPreferences("currentUserPermissions", Context.MODE_PRIVATE);
			if (sp == null) {
				return null;
			}
			String jstr = sp.getString("permissionStr", "");
			permissions = Commons.parseJsonList(jstr, new TypeToken<ArrayList<UserPermission>>() {
			}.getType());
			if (permissions == null) {
				return null;
			}
		}
		return permissions;
	}

	public static void setPermissions(ArrayList<UserPermission> permissions) {
		MyApp.permissions = permissions;
		Gson g = new Gson();
		SharedPreferences sp = MyApp.getInstance().getSharedPreferences("currentUserPermissions", Context.MODE_PRIVATE);
		sp.edit().putString("permissionStr", g.toJson(permissions)).commit();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		myApp = this;
		Res.init(this);
		PublicWay.num = 5;// 设置图片最大数量为5
		OkHttpClient okhttpClient = new OkHttpClient.Builder().connectTimeout(5000L, TimeUnit.MILLISECONDS).readTimeout(5000L, TimeUnit.MILLISECONDS).build();
		OkHttpUtils.initClient(okhttpClient);
      
		
	}
}
