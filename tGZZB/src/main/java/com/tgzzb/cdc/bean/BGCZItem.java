package com.tgzzb.cdc.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class BGCZItem implements Parcelable {
	private String bgdh;

	public BGCZItem() {
		super();
	}

	public BGCZItem(String bgdh) {
		super();
		this.bgdh = bgdh;
	}

	public BGCZItem(Parcel in) {
		this.bgdh = in.readString();
	}

	public String getBgdh() {
		return bgdh;
	}

	public void setBgdh(String bgdh) {
		this.bgdh = bgdh;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(bgdh);
	}

	public static final Parcelable.Creator<BGCZItem> CREATOR = new Parcelable.Creator<BGCZItem>() {
		public BGCZItem createFromParcel(Parcel in) {
			return new BGCZItem(in);
		}

		public BGCZItem[] newArray(int size) {
			return new BGCZItem[size];
		}
	};
}
