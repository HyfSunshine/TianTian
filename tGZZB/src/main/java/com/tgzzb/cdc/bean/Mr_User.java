package com.tgzzb.cdc.bean;

import java.io.Serializable;

import com.tgzzb.cdc.MyApp;

import android.content.Context;
import android.content.SharedPreferences;

public class Mr_User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mogilelogin;
	private String password;

	public Mr_User(String mogilelogin, String password) {
		super();
		this.mogilelogin = mogilelogin;
		this.password = password;
	}

	public String getMogilelogin() {
		return mogilelogin;
	}

	public void setMogilelogin(String mogilelogin) {
		this.mogilelogin = mogilelogin;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void writeToSharedPreferences(Mr_User user) {
		SharedPreferences sp = MyApp.getInstance().getSharedPreferences("currentUser", Context.MODE_PRIVATE);
		sp.edit().putString("user_name", user.getMogilelogin()).putString("user_password", user.getPassword()).commit();
	}

	public static Mr_User readFromSharedPreferences() {
		SharedPreferences sp = MyApp.getInstance().getSharedPreferences("currentUser", Context.MODE_PRIVATE);
		if (sp == null) {
			return null;
		}
		Mr_User user = new Mr_User(sp.getString("user_name", ""), sp.getString("user_password", ""));
		return user;
	}

	public void loginToSharedPreferences(Mr_User user) {
		SharedPreferences sp = MyApp.getInstance().getSharedPreferences("login", Context.MODE_PRIVATE);
		sp.edit().putString("user_name", user.getMogilelogin()).putString("user_password", user.getPassword()).commit();
	}

	public static Mr_User loginFromSharedPreferences() {
		SharedPreferences sp = MyApp.getInstance().getSharedPreferences("login", Context.MODE_PRIVATE);
		if (sp == null) {
			return null;
		}
		Mr_User user = new Mr_User(sp.getString("user_name", ""), sp.getString("user_password", ""));
		return user;
	}

	public void clearSharedPreference(String which) {
		SharedPreferences sp = MyApp.getInstance().getSharedPreferences(which, Context.MODE_PRIVATE);
		sp.edit().clear().commit();
	}
}
