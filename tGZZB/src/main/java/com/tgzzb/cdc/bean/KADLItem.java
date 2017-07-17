package com.tgzzb.cdc.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class KADLItem implements Parcelable {
	private String qybh;

	public KADLItem(Parcel source) {
		super();
		this.qybh = source.readString();
	}

	public KADLItem(String qybh) {
		super();
		this.qybh = qybh;
	}

	public String getQybh() {
		return qybh;
	}

	public void setQybh(String qybh) {
		this.qybh = qybh;
	}

	public static final Creator<KADLItem> CREATOR = new Creator<KADLItem>() {

		@Override
		public KADLItem[] newArray(int size) {
			return new KADLItem[size];
		}

		@Override
		public KADLItem createFromParcel(Parcel source) {
			return new KADLItem(source);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(qybh);
	}
}
